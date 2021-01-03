package com.mt.identityaccess.resource;

import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.ApplicationServiceRegistry;
import com.mt.identityaccess.application.revoke_token.CreateRevokeTokenCommand;
import com.mt.identityaccess.application.revoke_token.RevokeTokenCardRepresentation;
import com.mt.identityaccess.domain.model.revoke_token.RevokeToken;
import com.mt.identityaccess.infrastructure.JwtAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.*;

@RestController
@RequestMapping(produces = "application/json", path = "revoke-tokens")
public class RevokeTokenResource {

    @PostMapping("root")
    public ResponseEntity<Void> createForRoot(@RequestBody CreateRevokeTokenCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.revokeTokenApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("root")
    public ResponseEntity<SumPagedRep<RevokeTokenCardRepresentation>> readForRootByQuery(@RequestParam(value = HTTP_PARAM_QUERY, required = false) String queryParam,
                                                                                         @RequestParam(value = HTTP_PARAM_PAGE, required = false) String pageParam,
                                                                                         @RequestParam(value = HTTP_PARAM_SKIP_COUNT, required = false) String config) {
        SumPagedRep<RevokeToken> endpoints = ApplicationServiceRegistry.revokeTokenApplicationService().revokeTokens(queryParam, pageParam, config);
        return ResponseEntity.ok(new SumPagedRep(endpoints, RevokeTokenCardRepresentation::new));
    }

    @PostMapping("admin")
    public ResponseEntity<Void> createForAdmin(@RequestBody CreateRevokeTokenCommand command, @RequestHeader(HTTP_HEADER_CHANGE_ID) String changeId, @RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        ApplicationServiceRegistry.revokeTokenApplicationService().create(command, changeId);
        return ResponseEntity.ok().build();
    }
}
