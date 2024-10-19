package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;

@Route("выйти")
@RolesAllowed(value = {Roles.USER, Roles.ADMIN})
public class Logout extends Div implements BeforeEnterObserver {
    private final SecurityService securityService;

    public Logout(SecurityService securityService){
        this.securityService = securityService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
            securityService.logout();
    }
}
