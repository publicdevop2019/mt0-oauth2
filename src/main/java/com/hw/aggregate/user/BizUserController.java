package com.hw.aggregate.user;

import com.hw.aggregate.user.command.BizBizUserUpdatePwd;
import com.hw.aggregate.user.model.ForgetPasswordRequest;
import com.hw.aggregate.pending_user.model.PendingBizUser;
import com.hw.aggregate.user.model.BizUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json", path = "users")
public class BizUserController {

    @Autowired
    BizUserApplicationService resourceOwnerService;

    @PatchMapping("resourceOwner/pwd")
    public ResponseEntity<?> updateROPwd(@RequestBody BizBizUserUpdatePwd resourceOwner, @RequestHeader("authorization") String authorization) {
        resourceOwnerService.updateResourceOwnerPwd(resourceOwner, authorization);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<?> readForAdminByQuery() {
        return ResponseEntity.ok(resourceOwnerService.readAllResourceOwners());
    }

    @PostMapping("/forgetPwd")
    public ResponseEntity<?> forgetPwd(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        resourceOwnerService.sendForgetPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPwd")
    public ResponseEntity<?> resetPwd(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        resourceOwnerService.resetPassword(forgetPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRO(@RequestBody BizUser resourceOwner, @PathVariable Long id, @RequestHeader("authorization") String authorization) {
        resourceOwnerService.updateResourceOwner(resourceOwner, id, authorization);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRO(@PathVariable Long id) {
        resourceOwnerService.deleteResourceOwner(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("email/subscriber")
    public ResponseEntity<?> getEmailSubscriber() {
        return ResponseEntity.ok(resourceOwnerService.getEmailSubscriber());
    }

}
