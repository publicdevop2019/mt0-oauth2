package com.hw.interfaze;

public interface TokenRevocationService<T> {

    boolean shouldRevoke(T oldObj, T newObj);

    /**
     * client/resourceOwner get blacklisted in zuul proxy,
     * any token issued before will be blocked by zuul
     * blacklisted token can not be removed until it's expire
     * client/resourceOwner has to be authenticated again with new jwt token
     *
     */
    void blacklist(String name,boolean shouldRevoke);


}
