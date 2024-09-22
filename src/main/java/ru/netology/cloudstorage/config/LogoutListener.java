package ru.netology.cloudstorage.config;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.services.AuthService;

import java.util.List;

import static ru.netology.cloudstorage.log.Log.log;

// К сожалению не работаетgi
@Component
public class LogoutListener implements ApplicationListener<SessionDestroyedEvent> {

    private final AuthService authService;

    public LogoutListener(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event)
    {
        log("!!!", "", "");
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();

        for (SecurityContext securityContext : lstSecurityContext)
        {
            //String login = CustomUserServiceImpl.getCurrentUser().getLogin();
            String login = authService.logout();
            log("INFO: Пользователь " + login + " вышел из системы.", "", "");

        }
    }

}
