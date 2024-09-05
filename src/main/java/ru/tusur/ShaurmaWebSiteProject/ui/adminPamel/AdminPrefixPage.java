package ru.tusur.ShaurmaWebSiteProject.ui.adminPamel;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.Header;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;

import java.util.Optional;


@RoutePrefix(value = "admin")
@CssImport(value = "vaadin-app-layout.css", themeFor = "vaadin-app-layout")
public class AdminPrefixPage extends AppLayout implements Header {
    public static HorizontalLayout subViews = new HorizontalLayout();;
    protected AdminPrefixPage(ProductRepo productRepo, SecurityService securityService, ProductTypeEntityRepo productTypeEntityRepo) {

        SideNav views = getPrimaryNavigation();
        Scroller scroller = new Scroller(views);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        DrawerToggle toggle = new DrawerToggle();

        Div title = getMyTitle();
        title.getStyle().setFlexGrow("0");

//        HorizontalLayout subViews = AdminPanelGrid.getSecondaryNavigation(productRepo, securityService, productTypeEntityRepo);


        HorizontalLayout wrapper = new HorizontalLayout(toggle, getMyTitle(AdminPanelGrid.name), getMyAvatarInMyNavBar(securityService));
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        wrapper.getStyle().setWidth("100%");
        wrapper.setSpacing(false);

        VerticalLayout viewHeader = new VerticalLayout(wrapper, subViews);
        viewHeader.setPadding(false);
        viewHeader.setSpacing(false);

        addToDrawer(title, scroller);
        addToNavbar(viewHeader);

        setPrimarySection(Section.DRAWER);

    }

    private SideNav getPrimaryNavigation() {
        SideNav sideNav = new SideNav();
        sideNav.addItem(new SideNavItem(AdminPanelGrid.name, AdminPanelGrid.class, VaadinIcon.GRID.create()));
        sideNav.addItem(new SideNavItem(AdminPanelPromotionGrid.name, AdminPanelPromotionGrid.class, VaadinIcon.BOOK_PERCENT.create()));
        return sideNav;
    }


}
