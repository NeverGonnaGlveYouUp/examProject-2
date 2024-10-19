package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import ru.tusur.ShaurmaWebSiteProject.ui.components.ComponentView;

@PageTitle("Home")
@AnonymousAllowed
@Route(value = "", layout = MainLayout.class)
public class HomeView extends ComponentView {

    public HomeView() {
        addClassNames(Padding.Top.LARGE);

        add(new Paragraph("Welcome to Vaadin+!"));
    }

}