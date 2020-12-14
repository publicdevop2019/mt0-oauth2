package com.mt.identityaccess.domain.model;

import com.mt.identityaccess.domain.model.client.ClientService;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.mt.identityaccess.domain.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DomainRegistry {
    @Autowired
    private static ClientRepository bizClientRepository;
    @Autowired
    private static UserRepository bizUserRepo;
    @Autowired
    private static PendingUserRepository pendingUserRepo;
    @Autowired
    private static EncryptionService encryptionService;
    @Autowired
    private static ClientService clientService;

    public static ClientRepository clientRepository() {
        return bizClientRepository;
    }

    public static EncryptionService encryptionService() {
        return encryptionService;
    }

    public static ClientService clientService() {
        return clientService;
    }
}
