package com.hw.controller;

import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.entity.Client;
import com.hw.interfaze.TokenRevocationService;
import com.hw.repo.OAuthClientRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class ClientController {

    @Autowired
    OAuthClientRepo oAuthClientRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<Client> tokenRevocationService;

    /**
     * if client is marked as resource then it must be a backend and first party application
     *
     * @param client
     * @return
     */
    @PostMapping("client")
    public ResponseEntity<?> createClient(@Valid @RequestBody Client client) {
        validateResourceId(client);
        validateResourceIndicator(client);
        Client clientId = oAuthClientRepo.findByClientId(client.getClientId());
        if (clientId == null) {
            client.setClientSecret(encoder.encode(client.getClientSecret().trim()));
            Client saved = oAuthClientRepo.save(client);
            return ResponseEntity.ok().header("Location", String.valueOf(saved.getId())).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("clients")
    public List<Client> readClients() {
        return oAuthClientRepo.findAll();
    }

    /**
     * replace an existing client, if no change to pwd then send empty
     *
     * @param client
     * @param id
     * @return
     */
    @PutMapping("client/{id}")
    public ResponseEntity<?> replaceClient(@Valid @RequestBody Client client, @PathVariable Long id) {
        validateResourceId(client);
        validateResourceIndicator(client);
        Optional<Client> oAuthClient1 = oAuthClientRepo.findById(id);
        if (oAuthClient1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {

            Client client2 = oAuthClient1.get();
            String oldClientId = client2.getClientId();
            boolean b = tokenRevocationService.shouldRevoke(client2, client);

            if (StringUtils.hasText(client.getClientSecret())) {
                client.setClientSecret(encoder.encode(client.getClientSecret()));
            } else {
                client.setClientSecret(oAuthClient1.get().getClientSecret());
            }
            /**
             * copy to prevent new id gen, below method rely on correct following java conventions
             * setter & getter should return same type
             */
            BeanUtils.copyProperties(client, client2);
            oAuthClientRepo.save(client2);
            /** only revoke token after change has been persisted*/
            tokenRevocationService.blacklist(oldClientId,b);
            return ResponseEntity.ok().build();
        }
    }

    /**
     * rule: root client can not be deleted
     * @param id
     * @return
     */
    @DeleteMapping("client/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        preventRootAccountChange(id);
        Optional<Client> oAuthClient1 = oAuthClientRepo.findById(id);
        if (oAuthClient1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            oAuthClientRepo.delete(oAuthClient1.get());
            /** deleted client token must be revoked*/
            tokenRevocationService.blacklist(oAuthClient1.get().getClientId(),true);
            return ResponseEntity.ok().build();
        }
    }

    private void validateResourceId(Client client) throws IllegalArgumentException {
        /**
         * selected resource ids should be eligible resource
         */
        if (client.getResourceIds() == null || client.getResourceIds().size() == 0
                || client.getResourceIds().stream().anyMatch(resourceId -> oAuthClientRepo.findByClientId(resourceId) == null
                || !oAuthClientRepo.findByClientId(resourceId).getResourceIndicator()))
            throw new IllegalArgumentException("invalid resourceId found");
    }

    private void validateResourceIndicator(Client client) throws IllegalArgumentException {
        if (client.getResourceIndicator())
            if (client.getGrantedAuthorities().stream().anyMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_BACKEND))
                    && client.getGrantedAuthorities().stream().anyMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_FIRST_PARTY))) {
            } else {
                throw new IllegalArgumentException("invalid grantedAuthorities to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
            }

    }

    private void preventRootAccountChange(Long id) throws AccessDeniedException {
        Optional<Client> byId = oAuthClientRepo.findById(id);
        if (!byId.isEmpty() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new AccessDeniedException("root client can not be deleted");
    }

}
