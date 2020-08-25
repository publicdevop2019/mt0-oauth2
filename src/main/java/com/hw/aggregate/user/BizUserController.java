package com.hw.aggregate.user;

import com.hw.aggregate.user.command.CreateBizUserCommand;
import com.hw.aggregate.user.command.UpdateBizUserCommand;
import com.hw.aggregate.user.command.UpdateBizUserPwdCommand;
import com.hw.aggregate.user.model.ForgetPasswordRequest;
import com.hw.shared.rest.CreatedEntityRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "users")
public class BizUserController {

    @Autowired
    AdminBizUserApplicationService adminResourceOwnerService;
    @Autowired
    PublicBizUserApplicationService publicBizUserApplicationService;
    @Autowired
    AppBizUserApplicationService appBizUserApplicationService;
    @Autowired
    UserBizUserApplicationService userBizUserApplicationService;

    @PostMapping("public")
    public ResponseEntity<?> createForPublic(@RequestBody CreateBizUserCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        CreatedEntityRep createdEntityRep = publicBizUserApplicationService.create(command, changeId);
        return ResponseEntity.ok().header("Location", String.valueOf(createdEntityRep.getId())).build();
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                 @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                 @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        return ResponseEntity.ok(adminResourceOwnerService.readByQuery(queryParam, pageParam, config));
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> updateForAdmin(@RequestBody UpdateBizUserCommand command, @PathVariable Long id, @RequestHeader("authorization") String authorization, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        command.setAuthorization(authorization);
        adminResourceOwnerService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteRO(@PathVariable Long id) {
        adminResourceOwnerService.deleteResourceOwner(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<?> getForAppByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                              @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                              @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        return ResponseEntity.ok(appBizUserApplicationService.readByQuery(queryParam, pageParam, config));
    }


    @PatchMapping("resourceOwner/pwd")
    public ResponseEntity<?> updateROPwd(@RequestBody UpdateBizUserPwdCommand resourceOwner, @RequestHeader("authorization") String authorization) {
        userBizUserApplicationService.updateResourceOwnerPwd(resourceOwner, authorization);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/forgetPwd")
    public ResponseEntity<?> forgetPwd(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        publicBizUserApplicationService.sendForgetPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/resetPwd")
    public ResponseEntity<?> resetPwd(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        publicBizUserApplicationService.resetPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }
}
