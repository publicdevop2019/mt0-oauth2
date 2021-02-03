package com.mt.identityaccess.application.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.domain_event.SubscribeForEvent;
import com.mt.common.persistence.QueryConfig;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.validate.Validator;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.user.command.*;
import com.mt.identityaccess.application.user.representation.UserSpringRepresentation;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.user.*;
import com.mt.identityaccess.domain.model.user.event.UserDeleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserApplicationService implements UserDetailsService {

    @SubscribeForEvent
    @Transactional
    public String create(UserCreateCommand command, String operationId) {
        UserId userId = DomainRegistry.userRepository().nextIdentity();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, userId,
                () -> DomainRegistry.userService().create(
                        new UserEmail(command.getEmail()),
                        new UserPassword(command.getPassword()),
                        new ActivationCode(command.getActivationCode()),
                        userId
                ), User.class
        );

    }

    public SumPagedRep<User> users(String queryParam, String pageParam, String config) {
        return DomainRegistry.userRepository().usersOfQuery(new UserQuery(queryParam), new DefaultPaging(pageParam), new QueryConfig(config));
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
            DomainRegistry.userService().batchLock(commands);
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
                DomainRegistry.userService().updatePassword(user1, new CurrentPassword(command.getCurrentPwd()), new UserPassword(command.getPassword()));
            }, User.class);
            DomainRegistry.userRepository().add(user1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void forgetPassword(UserForgetPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            DomainRegistry.userService().forgetPassword(new UserEmail(command.getEmail()));
        }, User.class);
    }

    @SubscribeForEvent
    @Transactional
    public void resetPassword(UserResetPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            DomainRegistry.userService().resetPassword(new UserEmail(command.getEmail()), new UserPassword(command.getNewPassword()), new PasswordResetCode(command.getToken()));
        }, User.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> client;
        if (Validator.isValidEmail(username)) {
            //for login
            client = DomainRegistry.userRepository().searchExistingUserWith(new UserEmail(username));
        } else {
            //for refresh token
            client = DomainRegistry.userRepository().userOfId(new UserId(username));
        }
        return client.map(UserSpringRepresentation::new).orElse(null);
    }
}
