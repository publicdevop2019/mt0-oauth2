package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.model.PendingBizUser;
import com.hw.aggregate.user.BizUserApplicationService;
import com.hw.aggregate.user.model.BizUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "users")
public class PendingBizUserController {
    @Autowired
    BizUserApplicationService resourceOwnerService;

    @PostMapping("/public")
    public ResponseEntity<?> createForPublic(@RequestBody PendingBizUser pendingResourceOwner) {
        BizUser user = resourceOwnerService.createResourceOwner(pendingResourceOwner);
        return ResponseEntity.ok().header("Location", String.valueOf(user.getId())).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> createPendingRO(@RequestBody PendingBizUser pendingResourceOwner) {
        resourceOwnerService.createPendingResourceOwner(pendingResourceOwner);
        return ResponseEntity.ok().build();
    }
}
