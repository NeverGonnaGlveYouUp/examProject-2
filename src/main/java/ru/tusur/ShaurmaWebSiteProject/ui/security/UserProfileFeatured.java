package ru.tusur.ShaurmaWebSiteProject.ui.security;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;

@RolesAllowed(value = {Roles.USER, Roles.ADMIN})
@PageTitle("Профиль - Избранное")
@Route(value = "featured", layout = UserProfilePrefix.class)
public class UserProfileFeatured extends VerticalLayout {
    public final static String name = "Избранное";
    UserProfileFeatured(){

    }
}
