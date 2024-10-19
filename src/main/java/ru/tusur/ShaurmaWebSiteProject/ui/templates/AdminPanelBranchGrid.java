package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.ui.components.GridHeader;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.Breakpoint;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.HeadingLevel;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


@Route(value = "Панель администратора - таблица филиалов", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица филиалов")
public class AdminPanelBranchGrid extends Main {
    public static final String name = "Филиалы";
    private final BranchProductRepo branchProductRepo;
    private final LinkedList<BranchProduct> branchProducts;
    private final LinkedList<Product> products = new LinkedList<>();
    private final Set<String> branchesNames = new HashSet<>();
    private GridHeader header = null;
    private Branch selectedBranch = null;
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private Grid<Product> grid;

    public AdminPanelBranchGrid(BranchProductRepo branchProductRepo) {
        this.branchProductRepo = branchProductRepo;
        this.branchProducts = new LinkedList<>(branchProductRepo.findAll());
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.LARGE, LumoUtility.Padding.LARGE);
        collectData();
        collectDataForBranchChange(branchProductRepo.findTopByOrderByIdDesc().orElseThrow().getBranch().getAddress());
        add(createToolbar(), createContent());
    }

    private void collectData() {
        branchProducts.forEach(branchProduct -> {
            Branch branch = branchProduct.getBranch();
            branchesNames.add(branch.getAddress());
        });
    }

    private void collectDataForBranchChange(String selectedBranchName) {
        this.atomicInteger.set(0);
        this.selectedBranch = branchProductRepo.findTopByBranchAddress(selectedBranchName).orElseThrow(() -> {
            Notification notification = Notification.show("Филиала с таким именем нет");
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            notification.setPosition(Notification.Position.BOTTOM_END);
            notification.setDuration(3000);
            return new NotFoundException();
        }).getBranch();

        branchProducts.stream().filter(branchProduct -> branchProduct.getBranch().getId().equals(selectedBranch.getId())).forEach(branchProduct -> {
            Product product = branchProduct.getProduct();
            if (branchProduct.isHide()) atomicInteger.incrementAndGet();
            products.add(product);
        });
    }

    private Component createToolbar() {
        ComboBox<String> destination = new ComboBox<>();
        destination.setAriaLabel("Филиал");
        destination.addClassNames(LumoUtility.MinWidth.NONE);
        destination.setItems(branchesNames);
        destination.setValue(selectedBranch.getAddress());
        destination.addValueChangeListener(event -> {
            collectDataForBranchChange(event.getValue());
            header.setTitle("Активно " + (products.size() - atomicInteger.intValue()) + "из " + products.size());
            grid.setItems(products);
        });

        Layout toolbar = new Layout(destination);
        toolbar.setFlexDirection(Layout.FlexDirection.COLUMN);
        toolbar.setFlexDirection(Breakpoint.SMALL, Layout.FlexDirection.ROW);
        toolbar.setGap(Layout.Gap.MEDIUM);
        toolbar.setJustifyContent(Layout.JustifyContent.BETWEEN);
        return toolbar;
    }

    private Component createContent() {
        grid = new Grid<>(Product.class, true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setItems(products);

        header = new GridHeader("", HeadingLevel.H3, grid);
        header.setTitle("Активно " + (products.size() - atomicInteger.intValue()) + "из " + products.size());
        header.getRowLayout().addClassNames(LumoUtility.Padding.End.SMALL);

        Layout content = new Layout(header, grid);
        content.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderRadius.LARGE);
        content.setFlexDirection(Layout.FlexDirection.COLUMN);
        content.setOverflow(Layout.Overflow.HIDDEN);
        return content;
    }
}
