package com.telusko.loginandregister.config;

import com.telusko.loginandregister.model.User;
import com.telusko.loginandregister.repository.UserRepository;
import com.telusko.loginandregister.services.UserService;
import com.telusko.loginandregister.services.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        User user = userRepository.findByEmail(email);

        if (user != null)
        {
            if (user.isEnable())
            {
                if (user.isAccountNonLocked())
                {
                    if (user.getFailedAttempt() < UserServiceImpl.attempt_time - 1)
                    {
                        userService.increaseFailedAttempt(user);
                    }
                    else
                    {
                        userService.lock(user);
                        exception = new LockedException("Account is locked.. after 3 failed Attempts");
                    }
                }
                else if (!user.isAccountNonLocked())
                {
                    if (userService.unlockAccountTimeExpired(user))
                    {
                        exception = new LockedException("Account is unlocked.. Please try to re-login");
                    }
                    else
                    {
                        exception = new LockedException("Account is locked.. Please try after 5 seconds");
                    }
                }
            }
            else{
                exception = new LockedException("Account is inactive.. Verify your account");
            }
        }

        super.setDefaultFailureUrl("/signin?error");

        super.onAuthenticationFailure(request, response, exception);
    }
}
