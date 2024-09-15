package ru.tusur.ShaurmaWebSiteProject.ui.adminPamel;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;

@Route(value = "grid-promotion", layout = AdminPrefixPage.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица акций")
public class AdminPanelPromotionGrid extends VerticalLayout {
    public static final String name = "Таблица акций";
    static TabSheet tabSheet = new TabSheet();
    public HorizontalLayout subViews = new HorizontalLayout();
    public AdminPanelPromotionGrid(ProductTypeEntityRepo productTypeEntityRepo) {
        addAttachListener(event -> {
            subViews.removeAll();
            subViews.add(getSecondaryNavigation(productTypeEntityRepo));
        });

        add(subViews, new Div(new Text("asdasd")));
    }

    public static TabSheet getSecondaryNavigation(ProductTypeEntityRepo productTypeEntityRepo) {
        tabSheet = new TabSheet();
        productTypeEntityRepo.findAll().forEach(productType -> tabSheet.add(productType.getName(), new Div(new Text(productType.getName() + "таблица акций"))));


        HorizontalLayout navigation = new HorizontalLayout();
        tabSheet.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM);
        return tabSheet;
    }
}
