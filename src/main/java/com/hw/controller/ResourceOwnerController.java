package com.hw.controller;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.interfaze.TokenRevocationService;
import com.hw.repo.ResourceOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@RestController
@RequestMapping("api/v1")
public class ResourceOwnerController {

    @Autowired
    ResourceOwnerRepo userRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<ResourceOwner> tokenRevocationService;

    /**
     * update pwd, id is not required due to it may not be available,
     * also in order to get id, extra call required for ui
     */
    @PatchMapping("resourceOwner/pwd")
    public ResponseEntity<?> updateUserPwd(@RequestBody ResourceOwner resourceOwner) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ResourceOwner existUser;

        if (!StringUtils.hasText(resourceOwner.getPassword()) || !StringUtils.hasText(resourceOwner.getEmail())) {

            throw new IllegalArgumentException("password or email");

        } else {

            existUser = userRepo.findOneByEmail(authentication.getName());

            if (existUser == null)
                throw new IllegalArgumentException("user not exist:" + resourceOwner.getEmail());

        }

        existUser.setPassword(encoder.encode(resourceOwner.getPassword()));

        userRepo.save(existUser);

        /** must revoke issued token if pwd changed*/
        tokenRevocationService.blacklist(existUser.getUsername(), true);

        return ResponseEntity.ok().build();

    }

    @GetMapping("resourceOwners")
    public List<ResourceOwner> readUsers() {

        return userRepo.findAll().stream().filter(e -> e.getGrantedAuthorities().stream().noneMatch(e1 -> ResourceOwnerAuthorityEnum.ROLE_ROOT.equals(e1.getGrantedAuthority()))).collect(Collectors.toList());

    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     */
    @PostMapping("resourceOwner")
    public ResponseEntity<?> createUser(@RequestBody ResourceOwner newUser) {

        ResourceOwner existUser;

        if (!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getEmail())) {

            throw new IllegalArgumentException("password or email");

        } else {

            existUser = userRepo.findOneByEmail(newUser.getEmail());

            if (existUser != null)
                throw new IllegalArgumentException("user already exist:" + newUser.getEmail());

        }

        newUser.setGrantedAuthorities(Collections.singletonList(new GrantedAuthorityImpl(ResourceOwnerAuthorityEnum.ROLE_USER)));

        newUser.setLocked(false);

        newUser.setPassword(encoder.encode(newUser.getPassword()));

        ResourceOwner saved = userRepo.save(newUser);

        return ResponseEntity.ok().header("Location", String.valueOf(saved.getId())).build();
    }

    /**
     * update grantedAuthorities, root user access can never be given, admin can only lock or unlock user
     */
    @PutMapping("resourceOwner/{id}")
    public ResponseEntity<?> updateUser(@RequestBody ResourceOwner resourceOwner, @PathVariable Long id) {

        preventRootAccountChange(id);

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (resourceOwner.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new AccessDeniedException("assign root grantedAuthorities is prohibited");

        if (authorities.stream().noneMatch(e -> "ROLE_ROOT".equals(e.getAuthority())) && resourceOwner.getAuthorities() != null)
            throw new AccessDeniedException("only root user can change grantedAuthorities");

        Optional<ResourceOwner> byId = userRepo.findById(id);

        if (byId.isEmpty())
            throw new IllegalArgumentException(("user not exist:" + resourceOwner.getEmail()));

        boolean b = tokenRevocationService.shouldRevoke(byId.get(), resourceOwner);

        if (resourceOwner.getAuthorities() != null)
            byId.get().setGrantedAuthorities(new ArrayList<>((Collection<? extends GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>>) resourceOwner.getAuthorities()));

        if (resourceOwner.getLocked() != null)
            byId.get().setLocked(resourceOwner.getLocked());

        userRepo.save(byId.get());

        tokenRevocationService.blacklist(byId.get().getUsername(), b);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("resourceOwner/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        preventRootAccountChange(id);
        Optional<ResourceOwner> byId = userRepo.findById(id);

        if (byId.isEmpty())
            throw new IllegalArgumentException("user not exist" + id);

        userRepo.delete(byId.get());

        tokenRevocationService.blacklist(byId.get().getUsername(), true);

        return ResponseEntity.ok().build();
    }

    private void preventRootAccountChange(Long id) throws AccessDeniedException {
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (!byId.isEmpty() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new AccessDeniedException("root account can not be modified");
    }

}
