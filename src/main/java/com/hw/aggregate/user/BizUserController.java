package com.hw.aggregate.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.hw.aggregate.user.command.*;
import com.hw.shared.ServiceUtility;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.sql.PatchCommand;
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
    public ResponseEntity<?> createForPublic(@RequestBody PublicCreateBizUserCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        CreatedEntityRep createdEntityRep = publicBizUserApplicationService.create(command, changeId);
        return ResponseEntity.ok().header("Location", String.valueOf(createdEntityRep.getId())).build();
    }

    @GetMapping("admin")
    public ResponseEntity<?> readForAdminByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                 @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                 @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        return ResponseEntity.ok(adminResourceOwnerService.readByQuery(queryParam, pageParam, config));
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<?> readForAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminResourceOwnerService.readById(id));
    }


    @PutMapping("admin/{id}")
    public ResponseEntity<?> updateForAdmin(@RequestBody AdminUpdateBizUserCommand command, @PathVariable Long id, @RequestHeader("authorization") String authorization, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        command.setAuthorization(authorization);
        adminResourceOwnerService.replaceById(id, command, changeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("admin/{id}")
    public ResponseEntity<?> deleteForAdminById(@PathVariable Long id) {
        adminResourceOwnerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("app")
    public ResponseEntity<?> getForAppByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                              @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                              @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        return ResponseEntity.ok(appBizUserApplicationService.readByQuery(queryParam, pageParam, config));
    }


    @PutMapping("user/pwd")
    public ResponseEntity<?> updateForUser(@RequestBody UserUpdateBizUserCommand command, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String authorization, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        userBizUserApplicationService.replaceById(Long.parseLong(ServiceUtility.getUserId(authorization)), command, changeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/forgetPwd")
    public ResponseEntity<?> forgetPwd(@RequestBody PublicForgetPasswordCommand command) {
        publicBizUserApplicationService.sendForgetPassword(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("public/resetPwd")
    public ResponseEntity<?> resetPwd(@RequestBody PublicResetPwdCommand forgetPasswordRequest) {
        publicBizUserApplicationService.resetPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchForAdminById(@PathVariable(name = "id") Long id, @RequestBody JsonPatch patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String authorization) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(HTTP_HEADER_CHANGE_ID, changeId);
        params.put(HTTP_HEADER_AUTHORIZATION, authorization);
        adminResourceOwnerService.patchById(id, patch, params);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "admin")
    public ResponseEntity<?> patchForAdminBatch(@RequestBody List<PatchCommand> patch, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        adminResourceOwnerService.patchBatch(patch, changeId);
        return ResponseEntity.ok().build();
    }
}
