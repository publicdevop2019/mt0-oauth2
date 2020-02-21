package com.hw.service;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.entity.ResourceOwner;
import com.hw.interfaze.TokenRevocationService;
import com.hw.repo.ResourceOwnerRepo;
import com.hw.shared.BadRequestException;
import com.hw.utility.ServiceUtilityExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
public class ResourceOwnerServiceImpl {
    @Autowired
    ResourceOwnerRepo userRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    TokenRevocationService<ResourceOwner> tokenRevocationService;

    /**
     * update pwd, id is part of bearer token,
     */
    public void updateUserPwd(ResourceOwner resourceOwner, String authorization) {
        String userId = ServiceUtilityExt.getUserId(authorization);
        ResourceOwner existUser;
        if (!StringUtils.hasText(resourceOwner.getPassword()) || !StringUtils.hasText(resourceOwner.getEmail())) {
            throw new BadRequestException("password or email is empty");
        } else {
            Optional<ResourceOwner> byId = userRepo.findById(Long.parseLong(userId));
            if (byId.isEmpty())
                throw new BadRequestException("user not exist : " + resourceOwner.getEmail());
            existUser = byId.get();
        }
        existUser.setPassword(encoder.encode(resourceOwner.getPassword()));
        userRepo.save(existUser);
        /** must revoke issued token if pwd changed*/
        tokenRevocationService.blacklist(existUser.getId().toString(), true);
    }

    public List<ResourceOwner> readUsers() {
        return userRepo.findAll().stream().filter(e -> e.getGrantedAuthorities().stream().noneMatch(e1 -> ResourceOwnerAuthorityEnum.ROLE_ROOT.equals(e1.getGrantedAuthority()))).collect(Collectors.toList());
    }

    /**
     * create user, grantedAuthorities is overwritten to ROLE_USER
     */
    public ResourceOwner createUser(ResourceOwner newUser) {
        /**
         * if id present it will used instead generated
         */
        ResourceOwner existUser;
        if (!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getEmail())) {
            throw new BadRequestException("password or email is empty");
        } else {
            existUser = userRepo.findOneByEmail(newUser.getEmail());
            if (existUser != null)
                throw new BadRequestException("user already exist : " + newUser.getEmail());
        }
        newUser.setGrantedAuthorities(Collections.singletonList(new GrantedAuthorityImpl(ResourceOwnerAuthorityEnum.ROLE_USER)));
        newUser.setLocked(false);
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        return userRepo.save(newUser);
    }

    /**
     * update grantedAuthorities, root user access can never be given, admin can only lock or unlock user
     */
    public void updateUser(ResourceOwner resourceOwner, Long id, String authorization) {
        preventRootAccountChange(id);
        List<String> authorities = ServiceUtilityExt.getAuthority(authorization);
        if (resourceOwner.getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("assign root grantedAuthorities is prohibited");
        if (authorities.stream().noneMatch("ROLE_ROOT"::equals) && resourceOwner.getAuthorities() != null)
            throw new BadRequestException("only root user can change grantedAuthorities");
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("user not exist : " + resourceOwner.getEmail());
        boolean b = tokenRevocationService.shouldRevoke(byId.get(), resourceOwner);
        if (resourceOwner.getAuthorities() != null)
            byId.get().setGrantedAuthorities(new ArrayList<>((Collection<? extends GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>>) resourceOwner.getAuthorities()));
        if (resourceOwner.getLocked() != null)
            byId.get().setLocked(resourceOwner.getLocked());
        userRepo.save(byId.get());
        tokenRevocationService.blacklist(byId.get().getId().toString(), b);
    }

    public void deleteUser(Long id) {
        preventRootAccountChange(id);
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (byId.isEmpty())
            throw new BadRequestException("user not exist : " + id);
        userRepo.delete(byId.get());
        tokenRevocationService.blacklist(byId.get().getId().toString(), true);
    }


    public String getEmailSubscriber() {
        List<ResourceOwner> collect = userRepo.findAll().stream().filter(e -> e.getGrantedAuthorities().stream().anyMatch(el -> el.getGrantedAuthority().equals(ResourceOwnerAuthorityEnum.ROLE_ADMIN))).collect(Collectors.toList());
        List<String> collect1 = collect.stream().map(ResourceOwner::getEmail).collect(Collectors.toList());
        return String.join(",", collect1);
    }

    private void preventRootAccountChange(Long id) throws AccessDeniedException {
        Optional<ResourceOwner> byId = userRepo.findById(id);
        if (!byId.isEmpty() && byId.get().getAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new AccessDeniedException("root account can not be modified");
    }

}
