package ru.netology.cloudstorage.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repositories.AuthRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepository.findUserByLogin(username).map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public static User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        User user = new User();
        user.setLogin(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        Set<Role> set = new HashSet<>();
        for (var auth : userDetails.getAuthorities()) {
            set.add(Role.valueOf(auth.getAuthority()));
        }
        user.setRoles(set);
        return user;
    }
}
