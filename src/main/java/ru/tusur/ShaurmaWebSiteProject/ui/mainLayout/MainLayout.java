package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;

import java.util.LinkedList;
import java.util.List;

public class MainLayout extends AppLayout implements RouterLayout, Header {
    protected MainLayout(SecurityService securityService){
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Width.FULL);
        addToNavbar(getMyTitle());
    }
}