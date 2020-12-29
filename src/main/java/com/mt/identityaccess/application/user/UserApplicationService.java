package com.mt.identityaccess.application.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mt.common.application.SubscribeForEvent;
import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.common.rest.exception.AggregatePatchException;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.client.QueryConfig;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.ActivationCode;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.UserEmail;
import com.mt.identityaccess.domain.model.user.UserId;
import com.mt.identityaccess.domain.model.user.event.UserDeleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ObjectMapper om;

    @SubscribeForEvent
    @Transactional
    public UserId create(AppCreateUserCommand command, String operationId) {
        UserId userId = DomainRegistry.userRepository().nextIdentity();
        return (UserId) ApplicationServiceRegistry.idempotentWrapper().idempotentCreate(command, operationId, userId,
                () -> DomainRegistry.userService().create(
                        new UserEmail(command.getEmail()),
                        command.getPassword(),
                        new ActivationCode(command.getActivationCode()),
                        userId
                )
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
            });
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
                });
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
        Optional<User> user = DomainRegistry.userRepository().userOfId(userId);
        if (user.isPresent()) {
            User original = user.get();
            UserPatchingCommand middleLayer = new UserPatchingCommand(original);
            try {
                JsonNode jsonNode = om.convertValue(middleLayer, JsonNode.class);
                JsonNode patchedNode = command.apply(jsonNode);
                middleLayer = om.treeToValue(patchedNode, UserPatchingCommand.class);
            } catch (JsonPatchException | JsonProcessingException e) {
                log.error("error during patching", e);
                throw new AggregatePatchException();
            }
            UserPatchingCommand finalMiddleLayer = middleLayer;
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                original.replace(
                        finalMiddleLayer.getGrantedAuthorities(),
                        finalMiddleLayer.isLocked(),
                        original.isSubscription()
                );
            });
        }
    }

    @SubscribeForEvent
    @Transactional
    public void patchBatch(List<PatchCommand> patch, String changeId) {

    }

    @SubscribeForEvent
    @Transactional
    public void updatePassword(UserUpdateBizUserPasswordCommand command, String changeId) {
        UserId userId = DomainRegistry.authenticationService().getUserId();
        Optional<User> user = DomainRegistry.userRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
                DomainRegistry.userService().updatePassword(user1,command.getCurrentPwd(), command.getPassword());
            });
            DomainRegistry.userRepository().add(user1);
        }
    }

    @SubscribeForEvent
    @Transactional
    public void forgetPassword(AppForgetUserPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            DomainRegistry.userService().forgetPassword(command.getEmail());
        });
    }

    @SubscribeForEvent
    @Transactional
    public void resetPassword(AppResetUserPasswordCommand command, String changeId) {
        ApplicationServiceRegistry.idempotentWrapper().idempotent(command, changeId, (ignored) -> {
            DomainRegistry.userService().resetPassword(command.getEmail(), command.getNewPassword(), command.getToken());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<User> client = DomainRegistry.userRepository().userOfId(new UserId(id));
        return client.map(SpringOAuth2UserDetailRepresentation::new).orElse(null);
    }
}
