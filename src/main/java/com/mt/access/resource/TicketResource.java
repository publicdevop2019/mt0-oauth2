package com.mt.access.resource;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.ticket.SignedTicket;
import com.mt.access.infrastructure.JwtAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mt.common.CommonConstant.HTTP_HEADER_AUTHORIZATION;

@RestController
@RequestMapping(produces = "application/json", path = "tickets")
public class TicketResource {
    @PostMapping("admin/{resourceId}")
    public ResponseEntity<Void> createForRootByQuery(@RequestHeader(HTTP_HEADER_AUTHORIZATION) String jwt, @PathVariable String resourceId) {
        JwtAuthenticationService.JwtThreadLocal.unset();
        JwtAuthenticationService.JwtThreadLocal.set(jwt);
        SignedTicket encryptedTicket = ApplicationServiceRegistry.ticketApplicationService().create(resourceId);
        return ResponseEntity.ok().header("Location", encryptedTicket.getValue()).build();
    }

}
