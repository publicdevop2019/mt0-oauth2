package com.mt.identityaccess.resource;

import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.pending_user.PendingUserCreateCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.HTTP_HEADER_CHANGE_ID;

@RestController
@RequestMapping(produces = "application/json", path = "pending-users")
public class PendingUserResource {
    @PostMapping("/app")
    public ResponseEntity<Void> createForPublic(@RequestBody PendingUserCreateCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        ApplicationServiceRegistry.pendingUserApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }
}
