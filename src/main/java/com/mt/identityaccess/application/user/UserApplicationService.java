package com.mt.identityaccess.application.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain_event.DomainEvent;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.common.persistence.QueryConfig;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.domain.model.user.UserId;
import com.mt.identityaccess.domain.model.user.event.UserAuthorityChanged;
import com.mt.identityaccess.domain.model.user.event.UserDeleted;
import com.mt.identityaccess.domain.model.user.event.UserGetLocked;
import com.mt.identityaccess.domain.model.user.event.UserPasswordChanged;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserApplicationService implements UserDetailsService {

    @SubscribeForEvent
    @Transactional
    public String create(AppCreateUserCommand command, String operationId) {
        UserId userId = DomainRegistry.userRepository().nextIdentity();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, userId,
                () -> DomainRegistry.userService().create(
                        new UserEmail(command.getEmail()),
                        command.getPassword(),
                        new ActivationCode(command.getActivationCode()),
                        userId
                ), User.class
        );

    }

    @Transactional(readOnly = true)
    public SumPagedRep<User> users(String queryParam, String pageParam, String config) {
        return DomainRegistry.userRepository().usersOfQuery(new UserQuery(queryParam), new UserPaging(pageParam), new QueryConfig(config));
    }

    public Optional<User> user(String id) {
        return DomainRegistry.userRepository().userOfId(new UserId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void update(String id, UpdateUserCommand command, String changeId) {
        UserId userId = new UserId(id);
        Optional<User> user = DomainRegistry.userRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                user1.replace(
                        command.getGrantedAuthorities(),
                        command.isLocked(),
                        command.isSubscription()
                );
            }, User.class);
            DomainRegistry.userRepository().add(user1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void delete(String id, String changeId) {
        UserId userId = new UserId(id);
        Optional<User> user = DomainRegistry.userRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            if (user1.isNonRoot()) {
                ApplicationServiceRegistry.idempotentWrapper().idempotent(null, changeId, (ignored) -> {
                    DomainRegistry.userRepository().remove(user1);
                }, User.class);
                DomainEventPublisher.instance().publish(new UserDeleted(userId));
            } else {
                throw new RootUserDeleteException();
            }
        }
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            UserId userId = new UserId(id);
            Optional<User> user = DomainRegistry.userRepository().userOfId(userId);
            if (user.isPresent()) {
                User original = user.get();
                UserPatchingCommand beforePatch = new UserPatchingCommand(original);
                UserPatchingCommand afterPatch = DomainRegistry.customObjectSerializer().applyJsonPatch(command, beforePatch, UserPatchingCommand.class);
                original.replace(
                        afterPatch.getGrantedAuthorities(),
                        afterPatch.isLocked(),
                        original.isSubscription()
                );
            }
        }, User.class);
    }

    @SubscribeForEvent
    @Transactional
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        List<PatchCommand> deepCopy = DomainRegistry.customObjectSerializer().deepCopy(commands);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(deepCopy, changeId, (ignored) -> {
            DomainRegistry.userRepository().batchLock(commands);
        }, User.class);
    }

    @SubscribeForEvent
    @Transactional
    public void updatePassword(UserUpdateBizUserPasswordCommand command, String changeId) {
        UserId userId = DomainRegistry.authenticationService().getUserId();
        Optional<User> user = DomainRegistry.userRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                DomainRegistry.userService().updatePassword(user1, command.getCurrentPwd(), command.getPassword());
            }, User.class);
            DomainRegistry.userRepository().add(user1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void forgetPassword(AppForgetUserPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            DomainRegistry.userService().forgetPassword(command.getEmail());
        }, User.class);
    }

    @SubscribeForEvent
    @Transactional
    public void resetPassword(AppResetUserPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            DomainRegistry.userService().resetPassword(command.getEmail(), command.getNewPassword(), command.getToken());
        }, User.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> client;
        if (isEmail(username)) {
            //for login
            client = DomainRegistry.userRepository().searchExistingUserWith(username);
        } else {
            //for refresh token
            client = DomainRegistry.userRepository().userOfId(new UserId(username));
        }
        return client.map(SpringOAuth2UserDetailRepresentation::new).orElse(null);
    }

    public void revokeTokenBasedOnChange(Object o) {
        if (
                o instanceof UserAuthorityChanged ||
                        o instanceof UserDeleted ||
                        o instanceof UserGetLocked ||
                        o instanceof UserPasswordChanged
        ) {
            DomainRegistry.revokeTokenService().revokeUserToken(((DomainEvent) o).getDomainId());
        }
    }

    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    private static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
