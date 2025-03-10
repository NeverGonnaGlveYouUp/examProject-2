package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.templates.*;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Badge;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Item;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.dialogs.MessagesDialog;
import ru.tusur.ShaurmaWebSiteProject.ui.dialogs.NotificationsDialog;
import ru.tusur.ShaurmaWebSiteProject.ui.dialogs.UserDialog;
import ru.tusur.ShaurmaWebSiteProject.ui.security.LoginView;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.BadgeVariant;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;

import java.util.Objects;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    private UserDetails userDetails;
    private H1 viewTitle;

    protected MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        userDetails = securityService.getAuthenticatedUser();
        addHeaderContent();
        addDrawerContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE);

        MessagesDialog messagesMenu = new MessagesDialog();

        Badge messageBadge = new Badge();
        messageBadge.addClassNames("end-xs", LumoUtility.Position.ABSOLUTE, "top-xs");
        messageBadge.addThemeVariants(BadgeVariant.SUCCESS, BadgeVariant.PILL, BadgeVariant.PRIMARY, BadgeVariant.SMALL);

        Button messageButton = new Button(LineAwesomeIcon.COMMENTS.create(), e -> messagesMenu.showModal());
        messageButton.addClassNames(LumoUtility.Margin.Start.AUTO);
        messageButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        messageButton.setAriaLabel("View messages (4)");
        messageButton.setSuffixComponent(messageBadge);
        messageButton.setTooltipText("View messages (4)");
        messageButton.setVisible(false);

        NotificationsDialog notificationsMenu = new NotificationsDialog();

        Badge notificationsBadge = new Badge();
        notificationsBadge.addClassNames("end-xs", LumoUtility.Position.ABSOLUTE, "top-xs");
        notificationsBadge.addThemeVariants(BadgeVariant.ERROR, BadgeVariant.PILL, BadgeVariant.PRIMARY, BadgeVariant.SMALL);

        Button notificationsButton = new Button(LumoIcon.BELL.create(), e -> notificationsMenu.showModal());
        notificationsButton.addClassNames(LumoUtility.Margin.Start.XSMALL);
        notificationsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        notificationsButton.setAriaLabel("View notifications (2)");
        notificationsButton.setSuffixComponent(notificationsBadge);
        notificationsButton.setTooltipText("View notifications (2)");
        notificationsButton.setVisible(false);


        if (userDetails!=null){
            UserDialog userMenu = new UserDialog();
            Avatar avatar = new Avatar(userDetails.getUsername());
            avatar.setImageResource(ImageResourceUtils.getImageResource(userDetails.getAvatarUrl()));
            avatar.addClassNames(LumoUtility.Margin.Horizontal.SMALL);
            avatar.getElement().addEventListener("click", e -> userMenu.showModal());
            avatar.setTooltipEnabled(true);
            avatar.getStyle().set("margin-inline-start", "auto");
            addToNavbar(true, toggle, viewTitle, messageButton, messagesMenu, notificationsButton,
                    notificationsMenu, avatar, userMenu);
        } else {
            Item item = new Item("Войти", LineAwesomeIcon.SIGN_IN_ALT_SOLID);
            item.addClassNames(LumoUtility.LineHeight.XSMALL, LumoUtility.Padding.SMALL, "hover:bg-contrast-5");
            item.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));
            item.getStyle().set("margin-inline-start", "auto");
            addToNavbar(true, toggle, viewTitle, messageButton, messagesMenu, notificationsButton,
                    notificationsMenu, item);
        }

    }

    private void addDrawerContent() {
        Layout nav = new Layout(createTemplatesNavigation());
        nav.setFlexDirection(Layout.FlexDirection.COLUMN);
        nav.setGap(Layout.Gap.MEDIUM);

        Scroller scroller = new Scroller(nav);

        addToDrawer(scroller);
    }

    private Layout createTemplatesNavigation() {
        Layout navWrapper = new Layout();
        navWrapper.setFlexDirection(Layout.FlexDirection.COLUMN);
        navWrapper.setSizeUndefined();

        SideNav nav = new SideNav();
        nav.setWidthFull();
        nav.addItem(new SideNavItem("Главная", MainProductView.class, LineAwesomeIcon.COOKIE_BITE_SOLID.create()));
        nav.addItem(new SideNavItem("Корзина", ShoppingCartView.class, LineAwesomeIcon.SHOPPING_CART_SOLID.create()));
        nav.addItem(new SideNavItem("Оплата", CheckoutView.class, LineAwesomeIcon.CREDIT_CARD.create()));
        navWrapper.add(nav);
        if(userDetails!=null)nav.addItem(new SideNavItem("Профиль", ProfileView.class, LineAwesomeIcon.USER.create()));
        if(userDetails!=null && Objects.equals(userDetails.getRole(), Roles.ADMIN)){
            SideNav adminNav = new SideNav();
            adminNav.setLabel("Администратор");
            adminNav.setWidthFull();
            adminNav.setCollapsible(true);
            adminNav.addItem(new SideNavItem("Таблица товара", AdminPanelProductGrid.class, LineAwesomeIcon.DATABASE_SOLID.create()));
            adminNav.addItem(new SideNavItem("Таблица добавок", AdminPanelProductsOptionsGrid.class, LineAwesomeIcon.COFFEE_SOLID.create()));
            adminNav.addItem(new SideNavItem("Таблица акций", AdminPanelPromotionGrid.class, LineAwesomeIcon.FILE_ALT.create()));
            adminNav.addItem(new SideNavItem("Таблица филиалов", AdminPanelBranchGrid.class, LineAwesomeIcon.CODE_BRANCH_SOLID.create()));
            adminNav.addItem(new SideNavItem("Таблица отзывов", AdminPanelReviewsGrid.class, LineAwesomeIcon.COMMENT_ALT.create()));
//            adminNav.addItem(new SideNavItem("Отчеты", AdminPanelPDFReport.class, LineAwesomeIcon.FILE_PDF.create()));
            navWrapper.add(adminNav);
//            nav.addItem(new SideNavItem("чат", Chat.class, LineAwesomeIcon.CODE_BRANCH_SOLID.create()));
        }

        return navWrapper;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

}