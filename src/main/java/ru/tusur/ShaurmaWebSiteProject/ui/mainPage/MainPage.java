package ru.tusur.ShaurmaWebSiteProject.ui.mainPage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.AdaptiveMode;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.util.LinkedList;
import java.util.List;

@AnonymousAllowed
@Route(value = "/", layout = MainLayout.class)
@PageTitle("PitaMaster")
public class MainPage extends MainLayout {
    LinkedList<Div> linkedList = new LinkedList<>(List.of(new Div(new Button("btn1-MainLayout")), new Div(new Button("btn2-MainLayout")), new Div(new Button("btn3-MainLayout")), new Div(new Button("btn4-MainLayout"))));

    public MainPage(SecurityService securityService){
        super(securityService);
        addToNavbar(getMyNavBar(securityService, linkedList));
        try {
            if(securityService.getAuthenticatedUser().getRoles().equals(Roles.ADMIN)){
                setContent(setupAdminPanelButton(securityService));
            }
        } catch (Exception _) {}

    }
}