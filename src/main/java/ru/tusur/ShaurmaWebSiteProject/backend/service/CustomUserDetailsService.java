package ru.tusur.ShaurmaWebSiteProject.backend.service;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserDetailsRepo userDetailsRepo;
    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;
    @Autowired
    PasswordEncoder passwordEncoder;


    public void checkCredentials(String username, String password) throws AuthException {
        //State auth
//        UserDetails userDetails = loadUserByUsername(username);
        //Stateless auth
        User userDetails = loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new AuthException("Что-то пошло не так, попробуйте снова.");
        VaadinSession.getCurrent().setAttribute(String.valueOf(UserDetails.class), userDetails);
    }

    //State auth
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userDetailsRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
//    }

    //Stateless auth
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetails> user = userDetailsRepo.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    getAuthorities(user.get()));
        }
    }

    private static List<GrantedAuthority> getAuthorities(UserDetails user) {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
        }


    public void store(UserDetails userDetails) throws ServiceException {
        if (!userDetailsRepo.findByUsername(userDetails.getUsername()).isEmpty())
            throw new ServiceException("Это имя пользователя уже занято.");
        if (!Objects.equals(userDetails.getRole(), Roles.ADMIN)) userDetails.setRole(Roles.USER);
        userDetails.setPassword(delegatingPasswordEncoder.passwordEncoder().encode(userDetails.getTransientPassword()));
        userDetailsRepo.save(userDetails);
    }

    public static class ServiceException extends Exception {
        public ServiceException(String msg) {
            super(msg);
        }
    }
}
