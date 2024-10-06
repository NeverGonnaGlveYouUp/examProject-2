package ru.tusur.ShaurmaWebSiteProject.ui.adminPamel;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.Dialogs;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "grid-product", layout = AdminPrefixPage.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица товара")
@CssImport(value = "vaadin-grid.css", themeFor = "vaadin-grid")
public class AdminPanelGrid extends VerticalLayout implements Dialogs {
    public static final String name = "Товары";
    private final ProductService productService;
    @Getter
    private final SecurityService securityService;
    @Getter
    private final Set<ProductTypeEntity> allProdictEntitySet = new HashSet<>();
    @Getter
    private final TabSheet tabSheet = new TabSheet();

    public AdminPanelGrid(ProductService productService,
                          ProductTypeEntityRepo productTypeEntityRepo,
                          SecurityService securityService) {
        this.productService = productService;
        this.securityService = securityService;
        addAttachListener(event -> {

            tabSheet.setWidth("100%");

            productTypeEntityRepo.findAll().forEach(
                    productType -> {
                        allProdictEntitySet.add(productType);
                        Tab tab = tabSheet.add(
                                productType.getName(),
                                new GridForTab(productType)
                        );
                        Button close = new Button("X", clickEvent -> {
                            tabSheet.remove(tab);
                        });
                        close.getElement().getThemeList().add("badge small contrast");
                        close.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
                        close.setVisible(false);
                        tab.getElement().addEventListener("mouseover", mouseover -> close.setVisible(true));
                        tab.getElement().addEventListener("mouseout", mouseover -> close.setVisible(false));
                        tab.addComponentAtIndex(1, close);

                    }
            );

            tabSheet.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM);

            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_PRIMARY);
            menuBar.addItem(VaadinIcon.PLUS.create(), addTab -> {

                Set<String> openTabs = tabSheet.getChildren()
                        .filter(component -> component instanceof Tabs)
                        .map(component -> (Tabs) component)
                        .flatMap(Component::getChildren)
                        .map(component -> (Tab) component)
                        .map(Tab::getLabel)
                        .collect(Collectors.toSet());

                Set<String> allTabs = allProdictEntitySet.stream()
                                .map(ProductTypeEntity::getName)
                                .collect(Collectors.toSet());

                allTabs.removeAll(openTabs);

                Set<ProductTypeEntity> tabsToOpen = allProdictEntitySet
                        .stream()
                        .filter(productType -> allTabs.contains(productType.getName()))
                        .collect(Collectors.toSet());
                    selectProductTypeDialog(this, tabsToOpen).open();
            });

            MenuItem item = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
            SubMenu subItems = item.getSubMenu();
            subItems.addItem("Создать тип");

            Span removeAllTabs = new Span("Закрыть все вкладки");
            removeAllTabs.getStyle().setColor("red");
            subItems.addItem(removeAllTabs, removeAllTabsEvent -> {
                long end = tabSheet.getChildren().count();
                int start = 0;
                while (end != start) {
                    tabSheet.remove(start);
                    end -= 1;
                }
            });

            tabSheet.setSuffixComponent(menuBar);
            this.add(tabSheet);
        });
    }

    public GridForTab createGridForTab(ProductTypeEntity productType) {
        return new GridForTab(productType);
    }

    private class GridForTab extends Grid<Product> {
        private final ProductEditComponent productEditComponent;
        private LinkedList<Product> productLinkedList = null;

        public GridForTab(ProductTypeEntity productType) {
            super(Product.class, false);
            productLinkedList = new LinkedList<>(productService.findByProductTypeOrderByRankAsc(productType));
            productEditComponent = new ProductEditComponent(productService);
            setupGrid();

            productEditComponent.getDelete().addClickListener(event -> {
                Product product = productEditComponent.getProduct();
                if (product == null)
                    return;
                // пиздец...
                Optional<Dialog> confirmDeletionDialog = Optional.of((Dialog) event.getSource().getChildren().filter(component -> {
                    return component.equals("confirmDeletionDialog") && component.getId().isPresent();
                }).findFirst().get());
                confirmDeletionDialog.get().addDetachListener(event1 -> {
                    Button acceptButton = (Button) event1.getSource().getChildren().filter(component -> {
                        return component.equals("acceptButton") && component.getId().isPresent();
                    }).findFirst().orElseThrow(() -> new NotFoundException("failed to find button by id: acceptButton"));
                    acceptButton.addClickListener(event2 -> {
                        productLinkedList.remove(product);
                        this.getDataProvider().refreshAll();
                    });
                });
            });

//            productEditComponent.getClear().addClickListener(buttonClickEvent -> {
//                Product product = productEditComponent.getProduct();
//                if (product == null)
//                    return;
//                this.getDataProvider().refreshItem(product);
//            });

            productEditComponent.getSave().addClickListener(buttonClickEvent -> {
                Product product = productEditComponent.getProduct();
                if (product == null)
                    return;
                productLinkedList.set(productLinkedList.indexOf(product), product);
                this.getDataProvider().refreshItem(product);
            });
        }

        public void setupGrid() {
            this.addClassName("hover-on-cursor");
            this.setSelectionMode(Grid.SelectionMode.NONE);
            this.addColumn(product -> Optional.ofNullable(product.getName()).orElse("Н/д")).setHeader("Название").setResizable(true).setSortable(false).setFrozen(true).setAutoWidth(true).setFlexGrow(0);
            this.addComponentColumn(product -> {
                if (!product.getPreviewUrl().isEmpty()) {
                    Image image = new Image(new StreamResource(FilenameUtils.getName(product.getPreviewUrl()), (InputStreamFactory) () -> {
                        try {
                            return new DataInputStream(new FileInputStream(product.getPreviewUrl()));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }), "My Streamed Image");
                    image.getStyle().setHeight("25%").setWidth("50%");
                    return image;
                } else {
                    return new Icon(VaadinIcon.FILE_PICTURE);
                }
            }).setHeader("Картинка").setResizable(true).setSortable(false);
            this.addColumn(product -> Optional.ofNullable(product.getPrice()).map(bigDecimal -> bigDecimal.setScale(2, RoundingMode.UP).toString()).orElse("Н/д")).setHeader("Цена ₽").setResizable(true).setSortable(false);
            this.addColumn(product -> Optional.ofNullable(product.getMass()).map(Objects::toString).orElse("Н/д")).setHeader("Масса г").setResizable(true).setSortable(false);
            this.addComponentColumn(product -> new ProductContextMenu(product, productEditComponent));
            this.setItems(productLinkedList);
        }
    }

    private class ProductContextMenu extends MenuBar {
        public ProductContextMenu(Product product, ProductEditComponent productEditComponent) {
            MenuItem onEdit = createIconItem(this);
            SubMenu editMediaSubMenu = onEdit.getSubMenu();

            Span span = new Span("Удалить");
            span.getStyle().setColor("red");

            editMediaSubMenu.addItem("Редактировать", e -> productEditComponent.open(product));
            editMediaSubMenu.add(new Hr());
            editMediaSubMenu.addItem(span, e -> confirmDeletionDialog(product, productEditComponent.getProductService()).open());
        }

        private MenuItem createIconItem(MenuBar menu) {
            Icon icon = new Icon(VaadinIcon.ELLIPSIS_DOTS_H);
            MenuItem item = menu.addItem(icon);
            item.setAriaLabel("Edit");
            return item;
        }
    }
}
