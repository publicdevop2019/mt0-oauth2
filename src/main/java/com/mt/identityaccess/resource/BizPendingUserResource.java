package com.mt.identityaccess.resource;

import com.mt.identityaccess.application.AppPendingUserApplicationService;
import com.mt.identityaccess.application.command.AppCreatePendingUserCommand;
import com.hw.shared.validation.BizValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hw.shared.AppConstant.HTTP_HEADER_CHANGE_ID;

@RestController
@RequestMapping(produces = "application/json", path = "pending-users")
public class BizPendingUserResource {
    @Autowired
    AppPendingUserApplicationService resourceOwnerService;
    @Autowired
    BizValidator validator;
    @PostMapping("/app")
    public ResponseEntity<Void> createForPublic(@RequestBody AppCreatePendingUserCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId) {
        validator.validate("appCreatePendingUserCommand", command);
        resourceOwnerService.create(command, changeId);
        return ResponseEntity.ok().build();
    }
}
