package com.hw.aggregate.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.user.command.*;
import com.hw.aggregate.user.representation.AdminBizUserCardRep;
import com.hw.aggregate.user.representation.AdminBizUserRep;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.shared.ServiceUtility;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.SumPagedRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.hw.shared.AppConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "users")
public class BizUserController {

    @Autowired
    private AdminBizUserApplicationService adminResourceOwnerService;
    @Autowired
    private PublicBizUserApplicationService publicBizUserApplicationService;
    @Autowired
    private AppBizUserApplicationService appBizUserApplicationService;
    @Autowired
    private UserBizUserApplicationService userBizUserApplicationService;

    @PostMapping("public")
    public ResponseEntity<Void> createForPublic(@RequestBody PublicCreateBizUserCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        CreatedEntityRep createdEntityRep = publicBizUserApplicationService.create(command, changeId);
        return ResponseEntity.ok().header("Location", String.valueOf(createdEntityRep.getId())).build();
    }

    @GetMapping("admin")
    public ResponseEntity<SumPagedRep<AdminBizUserCardRep>> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        return ResponseEntity.ok(adminResourceOwnerService.readByQuery(queryParam, pageParam, config));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<AdminBizUserRep> readForAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminResourceOwnerService.readById(id));
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<Void> updateForAdmin(@RequestBody AdminUpdateBizUserCommand command, @PathVariable Long id, @RequestHeader("authorization") String authorization, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        command.setAuthorization(authorization);
        adminResourceOwnerService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<Void> deleteForAdminById(@PathVariable Long id, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminResourceOwnerService.deleteById(id, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<SumPagedRep<AppBizUserCardRep>> getForAppByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                           @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                           @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        return ResponseEntity.ok(appBizUserApplicationService.readByQuery(queryParam, pageParam, config));
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Void> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String authorization) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        params.put(HTTP_HEADER_AUTHORIZATION, authorization);
        adminResourceOwnerService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin")
    public ResponseEntity<Void> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminResourceOwnerService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("user/pwd")
    public ResponseEntity<Void> updateForUser(@RequestBody UserUpdateBizUserCommand command, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String authorization, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        userBizUserApplicationService.replaceById(Long.parseLong(ServiceUtility.getUserId(authorization)), command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/forgetPwd")
    public ResponseEntity<Void> forgetPwd(@RequestBody ForgetPasswordCommand command) {
        publicBizUserApplicationService.sendForgetPassword(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/resetPwd")
    public ResponseEntity<Void> resetPwd(@RequestBody PublicResetPwdCommand forgetPasswordRequest) {
        publicBizUserApplicationService.resetPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }
}
