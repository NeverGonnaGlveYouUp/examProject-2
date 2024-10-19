package ru.tusur.ShaurmaWebSiteProject.ui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.templates.MainProductView;

import java.util.LinkedList;
import java.util.List;

@RoutePrefix(value = "account")
//@CssImport(value = "vaadin-app-layout.css", themeFor = "vaadin-app-layout")
public class UserProfilePrefix  extends AppLayout {
    SecurityService securityService;

    public UserProfilePrefix(SecurityService securityService, UserDetailsRepo userDetailsRepo) {
        this.securityService = securityService;

        DrawerToggle drawerToggle = new DrawerToggle();
//        addToNavbar(drawerToggle, getMyTitle("Профиль"), getMyAvatarInMyNavBar(securityService));
        SideNav nav = getSideNav();
        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
            if(details.getWindowInnerWidth() >= 800) addClassName("PCLayout");
            else removeClassName("PCLayout");
        });

        addToDrawer(getSideNav());
    }

    private SideNav getSideNav() {
        SideNav nav = new SideNav();
        SideNavItem toMain = new SideNavItem("На главную", MainProductView.class, VaadinIcon.ARROW_LEFT.create());
        LinkedList<SideNavItem> list = new LinkedList<>(List.of(
                new SideNavItem(UserProfileMain.name, UserProfileMain.class, VaadinIcon.USER.create()),
                new SideNavItem(UserProfileDetails.name, UserProfileDetails.class, VaadinIcon.USER_CARD.create()),
                new SideNavItem(UserProfileFeatured.name, UserProfileFeatured.class, VaadinIcon.USER_HEART.create()),
                new SideNavItem(UserProfileHistory.name, UserProfileHistory.class, VaadinIcon.RECORDS.create())));
        list.add(toMain);
        list.forEach(nav::addItem);
        return nav;
    }
}
