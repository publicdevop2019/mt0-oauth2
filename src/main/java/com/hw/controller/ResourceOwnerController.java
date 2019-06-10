package com.hw.controller;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.entity.ResourceOwner;
import com.hw.repo.ResourceOwnerRepo;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("api/v1")
public class ResourceOwnerController {

    @Autowired
    ResourceOwnerRepo userRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    /**
     * update pwd
     */
    @PatchMapping("resourceOwner/{id}/pwd")
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

//    @CrossOrigin(origins = "*")
//    @GetMapping("/user/resourceId")
//    @PreAuthorize("hasRole('ROLE_USER') and #oauth2.hasScope('trust') and #oauth2.isUser()")
//    public ResponseEntity<String> getUserResourceId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        ResourceOwner user2 = userRepo.findOneByEmail(authentication.getName());
//        if (user2 == null) {
//            return ResponseEntity.notFound().build();
//        } else {
//            if (user2.getResourceId() == null) {
//                return ResponseEntity.notFound().build();
//            } else {
//                return ResponseEntity.ok(user2.getResourceId());
//            }
//        }
//    }

    /**
     * default to lowest authority
     */
    @GetMapping("resourceOwners")
    @PreAuthorize("hasRole('ROLE_ROOT') and #oauth2.hasScope('trust') and #oauth2.isUser()")
    public List<ResourceOwner> readUsers() {
        return userRepo.findAll();

    }

    @Description("default to lowest authority")
    @PostMapping("resourceOwner")
    @PreAuthorize("hasRole('ROLE_FRONTEND') and #oauth2.hasScope('write') and #oauth2.isClient()")
    public ResponseEntity<?> createUser(@RequestBody ResourceOwner newUser) {
        ResourceOwner existUser;
        if (!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getEmail())) {
            throw new IllegalArgumentException("password or email");
        } else {
            existUser = userRepo.findOneByEmail(newUser.getEmail());
            if (existUser != null)
                throw new IllegalArgumentException("user already exist:" + newUser.getEmail());
        }
        // security : set authority to lowest
        newUser.setGrantedAuthority(Arrays.asList(new GrantedAuthorityImpl("ROLE_USER")));
        newUser.setLocked(false);
        ResourceOwner saved = userRepo.save(newUser.setPassword(encoder.encode(newUser.getPassword())));
        return ResponseEntity.ok().header("Location", String.valueOf(saved.getId())).build();
    }

    /**
     * update authority
     */
    @PutMapping("resourceOwner/{id}")
    @PreAuthorize("hasRole('ROLE_ROOT') and #oauth2.hasScope('trust') and #oauth2.isUser()")
    public ResponseEntity<?> updateUser(@Valid @RequestBody ResourceOwner resourceOwner, @PathVariable Long id) {
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (byId.isEmpty())
            throw new NullPointerException(("user not exist:" + resourceOwner.getEmail()));
        byId.get().setGrantedAuthority(new ArrayList<>((Collection<? extends GrantedAuthorityImpl>) resourceOwner.getAuthorities()));
        byId.get().setLocked(resourceOwner.isAccountNonLocked());
        userRepo.save(byId.get());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("resourceOwner/{id}")
    @PreAuthorize("hasRole('ROLE_ROOT') and #oauth2.hasScope('trust') and #oauth2.isUser()")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        ResourceOwner existUser;
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (byId.isEmpty()) {
            throw new NullPointerException("user not exist" + id);
        }
        userRepo.delete(byId.get());
        return ResponseEntity.ok().build();
    }

}
