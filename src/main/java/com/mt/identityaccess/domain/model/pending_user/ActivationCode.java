package com.mt.identityaccess.domain.model.pending_user;

public class ActivationCode {
    private final String activationCode;

    public ActivationCode() {
        this.activationCode = "123456";// for testing
//        int m = (int) Math.pow(10, 6 - 1);
//        this.activationCode = String.valueOf(m + new Random().nextInt(9 * m));
    }

    public String plainValue() {
        return activationCode;
    }
}
