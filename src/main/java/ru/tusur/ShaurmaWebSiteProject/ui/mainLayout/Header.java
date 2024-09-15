package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.adminPamel.AdminPanelGrid;
import ru.tusur.ShaurmaWebSiteProject.ui.mainPage.MainPage;
import ru.tusur.ShaurmaWebSiteProject.ui.security.LoginView;
import ru.tusur.ShaurmaWebSiteProject.ui.security.UserProfile;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public interface Header {

    default Div getMyTitle(String text){
        Div div = new Div();
        H1 title = new H1(text);
        div.add(title);
//        div.addClickListener(event -> UI.getCurrent().navigate(MainPage.class));
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "relative");
        title.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.AlignSelf.CENTER);
//        div.getStyle().setMarginLeft("15%");
        div.getStyle().setFlexGrow("4");
        return div;
    }

    default Div getMyTitle(){
        Div div = new Div();
        H1 title = new H1("PitaMaster");
        div.add(title);
        div.addClickListener(event -> UI.getCurrent().navigate(MainPage.class));
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "relative");
        title.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.AlignSelf.CENTER);
        div.getStyle().setMarginLeft("2.5%");
        div.getStyle().setFlexGrow("4");
        return div;
    }

    default Button setupAdminPanelButton(SecurityService securityService){
        Button adminPanelButton = new Button(VaadinIcon.COG_O.create(), buttonClickEvent -> UI.getCurrent().navigate(AdminPanelGrid.class));
        adminPanelButton.addThemeVariants(ButtonVariant.LUMO_ICON);
        adminPanelButton.setClassName("adminPanelButton");
        adminPanelButton.addClassName(LumoUtility.AlignSelf.END);
        return adminPanelButton;
    }

    default Div setupAvatar (String username, String avatarUrl){
        Avatar avatar = new Avatar(username);
        Div div = new Div(avatar);
        div.addClickListener(event -> UI.getCurrent().navigate(UserProfile.Main.class));
        avatar.setImage(avatarUrl);
        avatar.setColorIndex((username.hashCode() % 7) - 1);
        div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignSelf.END,
                LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
        return div;
    }


    default HorizontalLayout getMyNavBar(SecurityService securityService, LinkedList<Div> linkedList){
        String username = null;
        String avatarUrl = null;
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addClassNames(LumoUtility.JustifyContent.END,
                LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM,
                LumoUtility.Width.XLARGE, LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.BASELINE);
        horizontalLayout.getStyle().setMarginRight("2.5%");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        linkedList.forEach(div -> {
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignSelf.END,
                    LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.MEDIUM,
                    LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
            horizontalLayout.add(div);
        });

        try {
            username = securityService.getAuthenticatedUser().getUsername();
            avatarUrl = securityService.getAuthenticatedUser().getAvatarUrl();
            horizontalLayout.add(setupAvatar(username, avatarUrl));
        } catch(Exception _) {
            Button login = new Button("Войти ", buttonClickEvent -> {
                UI.getCurrent().navigate(LoginView.class);
            });
            login.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignSelf.END,
                    LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.MEDIUM,
                    LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
            horizontalLayout.add(login);
        }
        return horizontalLayout;
    }

    default HorizontalLayout getMyAvatarInMyNavBar(SecurityService securityService){
        String username = null;
        String avatarUrl = null;
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addClassNames(LumoUtility.JustifyContent.END,
                LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM,
                LumoUtility.Width.XLARGE, LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.BASELINE);
        horizontalLayout.getStyle().setMarginRight("2.5%");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        try {
            username = securityService.getAuthenticatedUser().getUsername();
            avatarUrl = securityService.getAuthenticatedUser().getAvatarUrl();
            horizontalLayout.add(setupAvatar(username, avatarUrl));
        } catch(Exception _) {
            Button login = new Button("Войти ", buttonClickEvent -> {
                UI.getCurrent().navigate(LoginView.class);
            });
            login.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignSelf.END,
                    LumoUtility.AlignItems.CENTER, LumoUtility.Padding.Horizontal.MEDIUM,
                    LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
            horizontalLayout.add(login);
        }
        return horizontalLayout;
    }

}