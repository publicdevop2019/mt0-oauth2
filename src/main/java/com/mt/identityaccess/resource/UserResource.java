package com.mt.identityaccess.resource;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.SumPagedRep;
import com.mt.common.validate.BizValidator;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.user.command.*;
import com.mt.identityaccess.application.user.representation.UserSystemCardRepresentation;
import com.mt.identityaccess.application.user.representation.UserCardRepresentation;
import com.mt.identityaccess.application.user.representation.UserAdminRepresentation;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.infrastructure.JwtAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "users")
public class UserResource {

    @Autowired
    BizValidator validator;

    @PostMapping("app")
    public ResponseEntity<Void> createForApp(@RequestBody UserCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("appCreateUserCommand", command);
        return ResponseEntity.ok().header("Location", ApplicationServiceRegistry.userApplicationService().create(command, changeId)).build();
    }

    @GetMapping("admin")
    public ResponseEntity<SumPagedRep<UserCardRepresentation>> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                   @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                   @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<User> users = ApplicationServiceRegistry.userApplicationService().users(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep(users, UserCardRepresentation::new));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<UserAdminRepresentation> readForAdminById(@PathVariable String id) {
        Optional<User> user = ApplicationServiceRegistry.userApplicationService().user(id);
        return user.map(value -> ResponseEntity.ok(new UserAdminRepresentation(value))).orElseGet(() -> ResponseEntity.ok().build());
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<Void> updateForAdmin(@RequestBody UpdateUserCommand command,
                                               @PathVariable String id,
                                               @RequestHeader("authorization") String jwt,
                                               @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("adminUpdateUserCommand", command);
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.userApplicationService().update(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<Void> deleteForAdminById(@PathVariable String id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.userApplicationService().delete(id, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<SumPagedRep<UserSystemCardRepresentation>> getForAppByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                      @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                      @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<User> users = ApplicationServiceRegistry.userApplicationService().users(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep(users, UserSystemCardRepresentation::new));
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForAdminById(@PathVariable(name = "id") String id,
                                                  @RequestBody JsonPatch command,
                                                  @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId,
                                                  @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.userApplicationService().patch(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin")
    public ResponseEntity<Void> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.userApplicationService().patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("user/pwd")
    public ResponseEntity<Void> updateForUser(@RequestBody UserUpdateBizUserPasswordCommand command,
                                              @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt,
                                              @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        validator.validate("userUpdatePwdCommand", command);
        ApplicationServiceRegistry.userApplicationService().updatePassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("app/forgetPwd")
    public ResponseEntity<Void> forgetPwd(@RequestBody UserForgetPasswordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("appForgetUserPasswordCommand", command);
        ApplicationServiceRegistry.userApplicationService().forgetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("app/resetPwd")
    public ResponseEntity<Void> resetPwd(@RequestBody UserResetPasswordCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("appResetUserPasswordCommand", command);
        ApplicationServiceRegistry.userApplicationService().resetPassword(command, changeId);
        return ResponseEntity.ok().build();
    }
}
