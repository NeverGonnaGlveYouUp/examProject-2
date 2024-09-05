package ru.tusur.ShaurmaWebSiteProject.backend.service;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.internal.nodefeature.ElementListenerMap;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;

import java.util.Objects;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserDetailsRepo userDetailsRepo;
    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;
    @Autowired
    PasswordEncoder passwordEncoder;


    public void checkCredentials(String username, String password) throws AuthException {
        UserDetails userDetails = loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new AuthException("Что-то пошло не так, попробуйте снова.");
        VaadinSession.getCurrent().setAttribute(UserDetails.class, userDetails);

//        getAuthorizedRoutes(userDetails.getRoles()).stream().forEach(authorizedRoute ->
//                RouteConfiguration
//                        .forSessionScope()
//                        .setRoute(authorizedRoute.route,
//                                authorizedRoute.view,
//                                authorizedRoute.parent));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }


    public record AuthorizedRoute(String route, Class<? extends Component> view, Class<? extends RouterLayout> parent) {
    }


//    public List<AuthorizedRoute> getAuthorizedRoutes(String roles) {
//        var routes = new ArrayList<AuthorizedRoute>();
//
//        if (roles.equals(Roles.USER)) {
//            routes.add(new AuthorizedRoute("/main-authenticated", AuthenticatedMainPage.class, MainLayout.class));
//
//        } else if (roles.equals(Roles.ADMIN)) {
//            routes.add(new AuthorizedRoute("/main-authenticated-admin", AuthenticatedMainPage.class, MainLayout.class));
//        }
//
//        return routes;
//    }

    /**
     * 'Stores' the bean.
     * <p>
     * In /reality it just throws ServiceException from time to time.
     */
    public void store(UserDetails userDetails) throws ServiceException {
        if (!userDetailsRepo.findByUsername(userDetails.getUsername()).isEmpty())
            throw new ServiceException("Это имя пользователя уже занято.");
        if (!Objects.equals(userDetails.getRoles(), Roles.ADMIN)) userDetails.setRoles(Roles.USER);
        userDetails.setPassword(delegatingPasswordEncoder.passwordEncoder().encode(userDetails.getTransientPassword()));
        userDetailsRepo.save(userDetails);
    }

    /**
     * Utility Exception class that we can use in the frontend to show that
     * something went wrong during save.
     */
    public static class ServiceException extends Exception {
        public ServiceException(String msg) {
            super(msg);
        }
    }
}
