package com.mt.access.application.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.*;
import com.mt.access.application.user.representation.UserSpringRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.ActivationCode;
import com.mt.access.domain.model.user.*;
import com.mt.access.domain.model.user.event.UserDeleted;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserApplicationService implements UserDetailsService {

    @SubscribeForEvent
    @Transactional
    public String create(UserCreateCommand command, String operationId) {
        UserId userId = new UserId();
        return ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, userId,
                () -> DomainRegistry.getUserService().create(
                        new UserEmail(command.getEmail()),
                        new UserPassword(command.getPassword()),
                        new ActivationCode(command.getActivationCode()),
                        userId
                ), User.class
        );

    }

    public SumPagedRep<User> users(String queryParam, String pageParam, String config) {
        return DomainRegistry.getUserRepository().usersOfQuery(new UserQuery(queryParam, pageParam, config));
    }

    public Optional<User> user(String id) {
        return DomainRegistry.getUserRepository().userOfId(new UserId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void update(String id, UpdateUserCommand command, String changeId) {
        UserId userId = new UserId(id);
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(userId, command, changeId, (ignored) -> {
                user1.replace(
                        command.getGrantedAuthorities(),
                        command.isLocked(),
                        command.isSubscription()
                );
            }, User.class);
            DomainRegistry.getUserRepository().add(user1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void delete(String id, String changeId) {
        UserId userId = new UserId(id);
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            if (user1.isNonRoot()) {
                ApplicationServiceRegistry.idempotentWrapper().idempotent(userId, null, changeId, (ignored) -> {
                    DomainRegistry.getUserRepository().remove(user1);
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
        UserId userId = new UserId(id);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(userId, command, changeId, (ignored) -> {
            Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
            if (user.isPresent()) {
                User original = user.get();
                UserPatchingCommand beforePatch = new UserPatchingCommand(original);
                UserPatchingCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, UserPatchingCommand.class);
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
        Collection<PatchCommand> patchCommands = CommonDomainRegistry.getCustomObjectSerializer().deepCopyCollection(commands);
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, patchCommands, changeId, (ignored) -> {
            DomainRegistry.getUserService().batchLock(commands);
        }, User.class);
    }

    @SubscribeForEvent
    @Transactional
    public void updatePassword(UserUpdateBizUserPasswordCommand command, String changeId) {
        UserId userId = DomainRegistry.getAuthenticationService().getUserId();
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(userId, command, changeId, (ignored) -> {
                DomainRegistry.getUserService().updatePassword(user1, new CurrentPassword(command.getCurrentPwd()), new UserPassword(command.getPassword()));
            }, User.class);
            DomainRegistry.getUserRepository().add(user1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void forgetPassword(UserForgetPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, command, changeId, (ignored) -> {
            DomainRegistry.getUserService().forgetPassword(new UserEmail(command.getEmail()));
        }, User.class);
    }

    @SubscribeForEvent
    @Transactional
    public void resetPassword(UserResetPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(null, command, changeId, (ignored) -> {
            DomainRegistry.getUserService().resetPassword(new UserEmail(command.getEmail()), new UserPassword(command.getNewPassword()), new PasswordResetCode(command.getToken()));
        }, User.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> client;
        if (Validator.isValidEmail(username)) {
            //for login
            client = DomainRegistry.getUserRepository().searchExistingUserWith(new UserEmail(username));
        } else {
            //for refresh token
            client = DomainRegistry.getUserRepository().userOfId(new UserId(username));
        }
        return client.map(UserSpringRepresentation::new).orElse(null);
    }

    public static class RootUserDeleteException extends RuntimeException {
    }
}
