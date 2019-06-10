package com.hw.controller;

import com.hw.entity.OAuthClient;
import com.hw.repo.OAuthClientRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
@PreAuthorize("hasRole('ROLE_ROOT') and #oauth2.hasScope('trust') and #oauth2.isUser()")
public class OAuthClientController {

    @Autowired
    OAuthClientRepo oAuthClientRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @PostMapping("client")
    public ResponseEntity<?> createClient(@Valid @RequestBody OAuthClient oAuthClient) {
        OAuthClient clientId = oAuthClientRepo.findByClientId(oAuthClient.getClientId());
        if (clientId == null) {
            OAuthClient saved = oAuthClientRepo.save(oAuthClient.setClientSecret(encoder.encode(oAuthClient.getClientSecret().trim())));
            return ResponseEntity.ok().header("Location", String.valueOf(saved.getId())).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("clients")
    public List<OAuthClient> readClients() {
        return oAuthClientRepo.findAll();
    }

    @PutMapping("client/{id}")
    public ResponseEntity<?> replaceClient(@Valid @RequestBody OAuthClient oAuthClient, @PathVariable Long id) {
        Optional<OAuthClient> oAuthClient1 = oAuthClientRepo.findById(id);
        if (oAuthClient1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            if (StringUtils.hasText(oAuthClient1.get().getClientSecret())) {
                oAuthClient.setClientSecret(encoder.encode(oAuthClient.getClientSecret()));
            } else {
                oAuthClient.setClientSecret(oAuthClient1.get().getClientSecret());
            }
            OAuthClient oAuthClient2 = oAuthClient1.get();
            // Note: copy to prevent new id gen
            BeanUtils.copyProperties(oAuthClient, oAuthClient2);
            oAuthClientRepo.save(oAuthClient2);
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("client/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        Optional<OAuthClient> oAuthClient1 = oAuthClientRepo.findById(id);
        if (oAuthClient1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            oAuthClientRepo.delete(oAuthClient1.get());
            return ResponseEntity.ok().build();
        }
    }
}
