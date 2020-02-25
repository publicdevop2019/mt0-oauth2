package com.hw.controller;

import com.hw.clazz.ResourceOwnerUpdatePwd;
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
    public ResponseEntity<?> updateROPwd(@RequestBody ResourceOwnerUpdatePwd resourceOwner, @RequestHeader("authorization") String authorization) {
        resourceOwnerService.updateResourceOwnerPwd(resourceOwner, authorization);
        return ResponseEntity.ok().build();
    }

    @GetMapping("resourceOwners")
    public ResponseEntity<?> readROs() {
        return ResponseEntity.ok(resourceOwnerService.readAllResourceOwners());
    }

    @PostMapping("resourceOwners")
    public ResponseEntity<?> createRO(@RequestBody ResourceOwner newUser) {
        ResourceOwner user = resourceOwnerService.createResourceOwner(newUser);
        return ResponseEntity.ok().header("Location", String.valueOf(user.getId())).build();
    }

    @PutMapping("resourceOwners/{id}")
    public ResponseEntity<?> updateRO(@RequestBody ResourceOwner resourceOwner, @PathVariable Long id, @RequestHeader("authorization") String authorization) {
        resourceOwnerService.updateResourceOwner(resourceOwner, id, authorization);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("resourceOwners/{id}")
    public ResponseEntity<?> deleteRO(@PathVariable Long id) {
        resourceOwnerService.deleteResourceOwner(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("email/subscriber")
    public ResponseEntity<?> getEmailSubscriber() {
        return ResponseEntity.ok(resourceOwnerService.getEmailSubscriber());
    }

}
