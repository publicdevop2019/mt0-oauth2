package com.mt.identityaccess.domain.service;

public interface EncryptionService {
    String encryptedValue(String secret);
    boolean compare(String encrypted,String raw);
}
