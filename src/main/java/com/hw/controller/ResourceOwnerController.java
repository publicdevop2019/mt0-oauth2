package com.hw.controller;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.repo.ResourceOwnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 *  root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 *  admin has ROLE_ADMIN, ROLE_USER
 *  user has ROLE_USER
 */
@RestController
@RequestMapping("api/v1")
public class ResourceOwnerController {

    @Autowired
    ResourceOwnerRepo userRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    /**
     * update pwd, id is not required due to it may not be available,
     * also in order to get id, extra call required
     */
    @PatchMapping("resourceOwner/pwd")
    @PreAuthorize("hasRole('ROLE_USER') and #oauth2.hasScope('trust') and #oauth2.isUser()")
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

        return ResponseEntity.ok().build();

    }

    @GetMapping("resourceOwners")
    @PreAuthorize("hasRole('ROLE_ADMIN') and #oauth2.hasScope('trust') and #oauth2.isUser()")
    public List<ResourceOwner> readUsers() {

        return userRepo.findAll();

    }

    /**
     * create user, grantedAuthority is overwritten to ROLE_USER
     */
    @PostMapping("resourceOwner")
    @PreAuthorize("hasRole('ROLE_FRONTEND') and hasRole('ROLE_FIRST_PARTY') and #oauth2.hasScope('write') and #oauth2.isClient()")
    public ResponseEntity<?> createUser(@RequestBody ResourceOwner newUser) {

        ResourceOwner existUser;

        if (!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getEmail())) {

            throw new IllegalArgumentException("password or email");

        } else {

            existUser = userRepo.findOneByEmail(newUser.getEmail());

            if (existUser != null)
                throw new IllegalArgumentException("user already exist:" + newUser.getEmail());

        }

        newUser.setGrantedAuthority(Collections.singletonList(new GrantedAuthorityImpl(ResourceOwnerAuthorityEnum.ROLE_USER)));

        newUser.setLocked(false);

        newUser.setPassword(encoder.encode(newUser.getPassword()));

        ResourceOwner saved = userRepo.save(newUser);

        return ResponseEntity.ok().header("Location", String.valueOf(saved.getId())).build();
    }

    /**
     * update grantedAuthority, root user access can never be given, admin can only lock or unlock user
     */
    @PutMapping("resourceOwner/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') and #oauth2.hasScope('trust') and #oauth2.isUser()")
    public ResponseEntity<?> updateUser(@RequestBody ResourceOwner resourceOwner, @PathVariable Long id) {

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (resourceOwner.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new AccessDeniedException("assign root grantedAuthority is prohibited");

        if (authorities.stream().noneMatch(e -> "ROLE_ROOT".equals(e.getAuthority())) && resourceOwner.getAuthorities() != null)
            throw new AccessDeniedException("only root user can change grantedAuthority");

        Optional<ResourceOwner> byId = userRepo.findById(id);

        if (byId.isEmpty())
            throw new IllegalArgumentException(("user not exist:" + resourceOwner.getEmail()));

        if (resourceOwner.getAuthorities() != null)
            byId.get().setGrantedAuthority(new ArrayList<>((Collection<? extends GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>>) resourceOwner.getAuthorities()));

        if (resourceOwner.getLocked() != null)
            byId.get().setLocked(resourceOwner.getLocked());

        userRepo.save(byId.get());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("resourceOwner/{id}")
    @PreAuthorize("hasRole('ROLE_ROOT') and #oauth2.hasScope('trust') and #oauth2.isUser()")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        Optional<ResourceOwner> byId = userRepo.findById(id);

        if (byId.isEmpty())
            throw new IllegalArgumentException("user not exist" + id);

        userRepo.delete(byId.get());

        return ResponseEntity.ok().build();
    }

}
