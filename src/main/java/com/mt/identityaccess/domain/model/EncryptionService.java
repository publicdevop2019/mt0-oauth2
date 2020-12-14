package com.mt.identityaccess.domain.model;

public interface EncryptionService {
    String encryptedValue(String secret);
}
