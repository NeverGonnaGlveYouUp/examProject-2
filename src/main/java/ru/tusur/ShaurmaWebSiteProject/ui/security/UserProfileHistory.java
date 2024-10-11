package ru.tusur.ShaurmaWebSiteProject.ui.security;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;

@RolesAllowed(value = {Roles.USER, Roles.ADMIN})
@PageTitle("Профиль - История")
@Route(value = "history", layout = UserProfilePrefix.class)
public class UserProfileHistory extends VerticalLayout {
    public final static String name = "История";
    UserProfileHistory(){

    }
}
