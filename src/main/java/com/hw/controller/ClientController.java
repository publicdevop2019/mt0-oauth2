package com.hw.controller;

import com.hw.clazz.eenum.ClientAuthorityEnum;
import com.hw.entity.Client;
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
public class ClientController {

    @Autowired
    OAuthClientRepo oAuthClientRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

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
            if (StringUtils.hasText(client.getClientSecret())) {
                client.setClientSecret(encoder.encode(client.getClientSecret()));
            } else {
                client.setClientSecret(oAuthClient1.get().getClientSecret());
            }
            Client client2 = oAuthClient1.get();
            /**
             * copy to prevent new id gen, below method rely on correct following java conventions
             * setter & getter should return same type
             */
            BeanUtils.copyProperties(client, client2);
            oAuthClientRepo.save(client2);
            return ResponseEntity.ok().build();
        }
    }

    @DeleteMapping("client/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        Optional<Client> oAuthClient1 = oAuthClientRepo.findById(id);
        if (oAuthClient1.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            oAuthClientRepo.delete(oAuthClient1.get());
            return ResponseEntity.ok().build();
        }
    }

    private void validateResourceId(Client client) throws IllegalArgumentException {
        /**
         * selected resource ids should be eligible resource
         */
        if (client.getResourceIds() != null && client.getResourceIds().stream().anyMatch(resourceId -> oAuthClientRepo.findByClientId(resourceId) == null))
            throw new IllegalArgumentException("invalid resourceId found");
    }

    private void validateResourceIndicator(Client client) throws IllegalArgumentException {
        if (client.getResourceIndicator())
            if (client.getGrantedAuthority().stream().anyMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_BACKEND))
                    && client.getGrantedAuthority().stream().anyMatch(e -> e.getGrantedAuthority().equals(ClientAuthorityEnum.ROLE_FIRST_PARTY))) {
            } else {
                throw new IllegalArgumentException("invalid grantedAuthority to be a resource, must be ROLE_FIRST_PARTY & ROLE_BACKEND");
            }

    }
}
