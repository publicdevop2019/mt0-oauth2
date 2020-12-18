package com.mt.identityaccess.port.adapter.service.revoke_token;

public interface RevokeTokenAdapter {
    void revoke(String id, String targetType);
}
