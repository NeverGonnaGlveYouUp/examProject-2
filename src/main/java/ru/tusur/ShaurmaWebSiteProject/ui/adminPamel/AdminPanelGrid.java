package ru.tusur.ShaurmaWebSiteProject.ui.adminPamel;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.button.Button;
import jakarta.annotation.security.RolesAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductType;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;

import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Route(value = "grid-product", layout = AdminPrefixPage.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица товара")
public class AdminPanelGrid extends VerticalLayout {
    public static final String name = "Таблица товара";
    static LinkedList<Product> productLinkedList = null;
    static Grid<Product> grid = null;
    static TabSheet tabSheet = new TabSheet();
    public AdminPanelGrid(ProductRepo productRepo, SecurityService securityService, ProductTypeEntityRepo productTypeEntityRepo) {
        productLinkedList = new LinkedList<>(productRepo.findByProductTypeOrderByRankAsc(productTypeEntityRepo.findById(1L).get()));
        setupGrid(productRepo);
        tabSheet.addSelectedChangeListener(event -> {
//            changeGridContent(productRepo, ProductType.of(event.getSelectedTab().getLabel()).orElse(null));
            changeGridContent(productRepo, productTypeEntityRepo.findByName(event.getSelectedTab().getLabel()).get());
        });


        ProductEditComponent productEditComponent = new ProductEditComponent(productRepo);
        ProductContextMenu productContextMenu = new ProductContextMenu(grid, productEditComponent);
        productEditComponent.getDelete().addClickListener(buttonClickEvent -> {
            Product product = productEditComponent.getProduct();
            if (product == null)
                return;
            productLinkedList.remove(product);
            grid.getDataProvider().refreshAll();
        });

        productEditComponent.getClear().addClickListener(buttonClickEvent -> {
            Product product = productEditComponent.getProduct();
            if (product == null)
                return;
            grid.getDataProvider().refreshItem(product);
        });

        productEditComponent.getSave().addClickListener(buttonClickEvent -> {
            Product product = productEditComponent.getProduct();
            if (product == null)
                return;
            productLinkedList.set(productLinkedList.indexOf(product), product);
            grid.getDataProvider().refreshAll();
        });

        addAttachListener(event -> {
            AdminPrefixPage.subViews.removeAll();
            AdminPrefixPage.subViews.add(getSecondaryNavigation(productRepo, securityService, productTypeEntityRepo));
        });
//        HorizontalLayout layout = new HorizontalLayout(grid);
//        layout.setAlignSelf(FlexComponent.Alignment.STRETCH, grid);
//        layout.getStyle().setWidth("100%");
//        layout.setPadding(true);
        this.add(grid, productContextMenu);

    }

    public static TabSheet  getSecondaryNavigation(ProductRepo productRepo, SecurityService securityService, ProductTypeEntityRepo productTypeEntityRepo) {
        tabSheet = new TabSheet();
        productTypeEntityRepo.findAll().forEach(productType -> tabSheet.add(productType.getName(), new Div(new Text(productType.getName()))));


        HorizontalLayout navigation = new HorizontalLayout();
        tabSheet.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM);
        return tabSheet;
    }

    private static void changeGridContent(ProductRepo productRepo, ProductTypeEntity productType){
        productLinkedList = new LinkedList<>(productRepo.findByProductTypeOrderByRankAsc(productType));
        grid.setItems(productLinkedList);
    }


    public static void setupGrid(ProductRepo productRepo) {
        grid = new Grid<>(Product.class, false);
        grid.addColumn(product -> Optional.ofNullable(product.getName()).orElse("Н/д")).setHeader("Название").setResizable(true).setSortable(false).setFrozen(true).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(product -> Optional.ofNullable(product.getPrice()).map(bigDecimal -> bigDecimal.setScale(2, RoundingMode.UP).toString()).orElse("Н/д")).setHeader("Цена ₽").setResizable(true).setSortable(false);
        grid.addColumn(product -> Optional.ofNullable(product.getDiscount()).map(Object::toString).orElse("Н/д")).setHeader("Скидка %").setResizable(true).setSortable(false);
        grid.addColumn(product -> Optional.ofNullable(product.getMass()).map(Objects::toString).orElse("Н/д")).setHeader("Масса г").setResizable(true).setSortable(false);
        grid.addComponentColumn(product -> createStatusIcon(product.getPreviewUrl().isEmpty())).setHeader("Картинка").setResizable(true).setSortable(false);
        grid.addComponentColumn(product -> createStatusIcon(product.isActive())).setTooltipGenerator(product -> String.valueOf(product.isActive())).setHeader("Активно").setResizable(true).setSortable(false);
        grid.setItems(productLinkedList);
    }

    private static Icon createStatusIcon(boolean status) {
        Icon icon;
        if (!status) {
            icon = VaadinIcon.CHECK.create();
            icon.getElement().getThemeList().add("badge success");
        } else {
            icon = VaadinIcon.CLOSE_SMALL.create();
            icon.getElement().getThemeList().add("badge error");
        }
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }

    private static class ProductContextMenu extends GridContextMenu<Product> {
        public ProductContextMenu(Grid<Product> target, ProductEditComponent productEditComponent) {
            super(target);
            setOpenOnClick(true);
            addItem("Редактировать", productGridContextMenuItemClickEvent -> productGridContextMenuItemClickEvent.getItem().ifPresent(productEditComponent::open));

            addItem("Скрыть", productGridContextMenuItemClickEvent -> productGridContextMenuItemClickEvent.getItem().ifPresent(product ->
            {

            }));

            addItem("Удалить", productGridContextMenuItemClickEvent -> productGridContextMenuItemClickEvent.getItem().ifPresent(product -> {

            }));

            add(new Hr());

            GridMenuItem<Product> nameItem = addItem("Название", productGridContextMenuItemClickEvent ->
                    productGridContextMenuItemClickEvent.getItem().ifPresent(product -> {

                    }));


            GridMenuItem<Product> isActiveItem = addItem("Активно", productGridContextMenuItemClickEvent ->
                    productGridContextMenuItemClickEvent.getItem().ifPresent(product -> {

                    }));

            setDynamicContentHandler(product -> {

                if (product == null) return false;

                nameItem.setText(String.format("Название: %s", product.getName()));
                isActiveItem.setText(String.format("Активно: %s", product.isActive()));

                return true;

            });

        }
    }

}
