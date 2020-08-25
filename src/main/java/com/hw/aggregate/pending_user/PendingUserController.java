package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.command.CreatePendingUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.HTTP_HEADER_CHANGE_ID;

@RestController
@RequestMapping(produces = "application/json", path = "pending-users")
public class PendingUserController {
    @Autowired
    PublicPendingUserApplicationService resourceOwnerService;

    @PostMapping("/public")
    public ResponseEntity<?> createForPublic(@RequestBody CreatePendingUserCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        resourceOwnerService.create(command, changeId);
        return ResponseEntity.ok().build();
    }
}
