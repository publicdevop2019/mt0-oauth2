package com.hw.controller;

import com.hw.entity.ResourceOwner;
import com.hw.service.ResourceOwnerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api")
public class ResourceOwnerController {

    @Autowired
    ResourceOwnerServiceImpl resourceOwnerService;

    @PatchMapping("resourceOwner/pwd")
    public ResponseEntity<?> updateUserPwd(@RequestBody ResourceOwner resourceOwner, @RequestHeader("authorization") String authorization) {
        resourceOwnerService.updateUserPwd(resourceOwner, authorization);
        return ResponseEntity.ok().build();
    }

    @GetMapping("resourceOwners")
    public ResponseEntity<?> readUsers() {
        return ResponseEntity.ok(resourceOwnerService.readUsers());
    }

    @PostMapping("resourceOwners")
    public ResponseEntity<?> createUser(@RequestBody ResourceOwner newUser) {
        ResourceOwner user = resourceOwnerService.createUser(newUser);
        return ResponseEntity.ok().header("Location", String.valueOf(user.getId())).build();
    }

    @PutMapping("resourceOwners/{id}")
    public ResponseEntity<?> updateUser(@RequestBody ResourceOwner resourceOwner, @PathVariable Long id, @RequestHeader("authorization") String authorization) {
        resourceOwnerService.updateUser(resourceOwner, id, authorization);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("resourceOwners/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        resourceOwnerService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("email/subscriber")
    public ResponseEntity<?> getEmailSubscriber() {
        return ResponseEntity.ok(resourceOwnerService.getEmailSubscriber());
    }

}
