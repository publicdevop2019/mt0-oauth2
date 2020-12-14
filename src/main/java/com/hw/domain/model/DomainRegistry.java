package com.hw.domain.model;

import com.hw.domain.model.client.ClientService;
import com.hw.domain.model.pending_user.PendingUserRepository;
import com.hw.domain.model.user.UserRepository;
import com.hw.application.AuthenticationApplicationService;
import com.hw.domain.model.client.ClientRepository;
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
    private static AuthenticationApplicationService authenticationService;
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
