package com.hw.service;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.ResourceOwnerUpdatePwd;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.interfaze.TokenRevocationService;
import com.hw.repo.ResourceOwnerRepo;
import com.hw.shared.BadRequestException;
import com.hw.utility.ServiceUtilityExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * root has ROLE_ROOT, ROLE_ADMIN, ROLE_USER
 * admin has ROLE_ADMIN, ROLE_USER
 * user has ROLE_USER
 */
@Service
@Slf4j
public class ResourceOwnerServiceImpl {
    @Autowired
    ResourceOwnerRepo userRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<ResourceOwner> tokenRevocationService;

    /**
     * update pwd, id is part of bearer token,
     * must revoke issued token if pwd changed
     */
    public void updateResourceOwnerPwd(ResourceOwnerUpdatePwd resourceOwner, String authorization) {
        String userId = ServiceUtilityExt.getUserId(authorization);
        if (!StringUtils.hasText(resourceOwner.getPassword()) || !StringUtils.hasText(resourceOwner.getEmail()) || !StringUtils.hasText(resourceOwner.getCurrentPwd()))
            throw new BadRequestException("password(s) or email empty");
        ResourceOwner resourceOwnerById = getResourceOwnerById(Long.parseLong(userId));
        if (!encoder.matches(resourceOwner.getCurrentPwd(), resourceOwnerById.getPassword()))
            throw new BadRequestException("wrong password");
        resourceOwnerById.setPassword(encoder.encode(resourceOwner.getPassword()));
        userRepo.save(resourceOwnerById);
        tokenRevocationService.blacklist(resourceOwnerById.getId().toString(), true);
    }

    public List<ResourceOwner> readAllResourceOwners() {
        return userRepo.findAll();
    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     * if id present it will used instead generated
     */
    public ResourceOwner createResourceOwner(ResourceOwner newUser) {
        log.info("create new resource owner for email {}", newUser.getEmail());
        ResourceOwner existUser;
        if (!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getEmail())) {
            log.info("password or email is empty");
            throw new BadRequestException("password or email is empty");
        } else {
            existUser = userRepo.findOneByEmail(newUser.getEmail());
            if (existUser != null) {
                log.info("user already exist");
                throw new BadRequestException("user already exist : " + newUser.getEmail());
            }
        }
        newUser.setGrantedAuthorities(Collections.singletonList(new GrantedAuthorityImpl(ResourceOwnerAuthorityEnum.ROLE_USER)));
        newUser.setLocked(false);
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        ResourceOwner save = userRepo.save(newUser);
        log.info("user successfully created with id {}", save.getId());
        return save;
    }

    /**
     * update grantedAuthorities, root user access can never be given, admin can only lock or unlock user
     */
    public void updateResourceOwner(ResourceOwner updatedRO, Long id, String authorization) {
        preventRootAccountChange(id);
        List<String> currentAuthorities = ServiceUtilityExt.getAuthority(authorization);
        if (updatedRO.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("assign root grantedAuthorities is prohibited");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && updatedRO.getAuthorities() != null)
            throw new BadRequestException("only root user can change grantedAuthorities");
        if (currentAuthorities.stream().noneMatch("ROLE_ROOT"::equals) && updatedRO.isSubscription())
            throw new BadRequestException("only root user can change subscription");
        ResourceOwner storedRO = getResourceOwnerById(id);
        boolean b = tokenRevocationService.shouldRevoke(storedRO, updatedRO);
        if (updatedRO.getAuthorities() != null)
            storedRO.setGrantedAuthorities(new ArrayList<>((Collection<? extends GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>>) updatedRO.getAuthorities()));
        if (updatedRO.getLocked() != null)
            storedRO.setLocked(updatedRO.getLocked());
        if (updatedRO.isSubscription()) {
            if (storedRO.getAuthorities().stream().anyMatch(e -> "ROLE_ADMIN".equals(e.getAuthority()))) {
                storedRO.setSubscription(Boolean.TRUE);
            } else {
                throw new BadRequestException("only admin or root can subscribe to new order");
            }
        }
        userRepo.save(storedRO);
        tokenRevocationService.blacklist(storedRO.getId().toString(), b);
    }

    public void deleteResourceOwner(Long id) {
        preventRootAccountChange(id);
        ResourceOwner resourceOwnerById = getResourceOwnerById(id);
        userRepo.delete(resourceOwnerById);
        tokenRevocationService.blacklist(resourceOwnerById.getId().toString(), true);
    }


    public String getEmailSubscriber() {
        List<String> collect1 = userRepo.findAll().stream()
                .filter(ResourceOwner::isSubscription)
                .map(ResourceOwner::getEmail).collect(Collectors.toList());
        return String.join(",", collect1);
    }

    private void preventRootAccountChange(Long id) {
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (byId.isPresent() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("root account can not be modified");
    }

    private ResourceOwner getResourceOwnerById(Long id) {
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("user not exist : " + id);
        return byId.get();
    }
}
