package com.telusko.loginandregister.services;

import com.telusko.loginandregister.model.User;

public interface UserService {
    User saveUser(User user, String url);

    void removeSessionMessage();

    void sendMail(User user, String path);

    boolean verifyAccount(String verificationCode);

    void increaseFailedAttempt(User user);

    void resetAttempt(String email);

    void lock(User user);

    boolean unlockAccountTimeExpired(User user);
}
