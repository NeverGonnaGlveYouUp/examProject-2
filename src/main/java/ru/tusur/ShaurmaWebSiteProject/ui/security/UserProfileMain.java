package ru.tusur.ShaurmaWebSiteProject.ui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.Header;

import java.util.Optional;

@RolesAllowed(value = {Roles.USER, Roles.ADMIN})
@PageTitle("Профиль - Главная")
@Route(value = "main", layout = UserProfilePrefix.class)
public class UserProfileMain extends VerticalLayout implements Header {
    public final static String name = "Главная";
    UserDetails userDetails;

    UserProfileMain(SecurityService securityService){
        userDetails = securityService.getAuthenticatedUser();
        addClassNames(LumoUtility.Gap.MEDIUM);
        getStyle().setPosition(Style.Position.RELATIVE);
        add(getUserCell());
    }

    private Div getUserCell(){
        Div div = new Div();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        VerticalLayout verticalLayout = new VerticalLayout();
        Div avatar = setupAvatar(userDetails.getUsername(), userDetails.getAvatarUrl());
        Span span1 = new Span(Optional.of(userDetails.getUsername()).orElse("username - not present"));
        Span span2 = new Span("email: " + Optional.of(userDetails.getEmail()).orElse("email - not present"));
        Icon icon = new Icon(VaadinIcon.ANGLE_RIGHT);

        div.add(horizontalLayout);
        div.addClassNames("userCell", "trans03s");
        div.setWidth("100%");

        horizontalLayout.addClassNames(LumoUtility.FlexWrap.WRAP, LumoUtility.AlignSelf.BASELINE);
        horizontalLayout.addClickListener(event -> UI.getCurrent().navigate(UserProfileDetails.class));
        horizontalLayout.add(avatar, verticalLayout, icon);
        horizontalLayout.getStyle()
                .setPaddingLeft("16px")
                .setPaddingRight("16px")
                .setPaddingTop("8px")
                .setPaddingBottom("8px")
                .setBorder("1px");

        verticalLayout.add(span1, span2);
        verticalLayout.getStyle().setFlexGrow("4").setPadding("0px");
        verticalLayout.setSpacing(false);

        span1.getStyle()
                .setPadding("0px")
                .setMargin("0px")
                .setFontWeight("bold")
                .setFontSize("14px");

        span2.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.FontWeight.THIN);
        span2.getStyle()
                .setPadding("0px")
                .setMargin("0px")
                .setFontWeight("thin")
                .setFontSize("12px");;

        icon.addClassName(LumoUtility.AlignSelf.BASELINE);
        return div;
    }
}

