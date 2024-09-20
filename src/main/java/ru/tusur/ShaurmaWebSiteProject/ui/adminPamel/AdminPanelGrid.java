package ru.tusur.ShaurmaWebSiteProject.ui.adminPamel;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.io.FilenameUtils;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.Dialogs;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

@Route(value = "grid-product", layout = AdminPrefixPage.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица товара")
@CssImport(value = "vaadin-grid.css", themeFor = "vaadin-grid")
public class AdminPanelGrid extends VerticalLayout implements Dialogs {
    public static final String name = "Таблица товара";
    public HorizontalLayout subViews = new HorizontalLayout();
    private LinkedList<Product> productLinkedList = null;
    private Grid<Product> grid = null;
    private ProductEditComponent productEditComponent;

    public AdminPanelGrid(ProductService productService, ProductTypeEntityRepo productTypeEntityRepo) {
        productLinkedList = new LinkedList<>(productService.findByProductTypeOrderByRankAsc(productTypeEntityRepo.findById(1L).orElseThrow(() -> new NotFoundException("product list ont found"))));
        productEditComponent = new ProductEditComponent(productService);
        setupGrid();
//        ProductContextMenu productContextMenu = new ProductContextMenu(grid, productEditComponent);
        productEditComponent.getDelete().addClickListener(event -> {
            Product product = productEditComponent.getProduct();
            if (product == null)
                return;
            // TODO пиздец...
            Optional<Dialog> confirmDeletionDialog = Optional.of((Dialog) event.getSource().getChildren().filter(component -> {
                return component.equals("confirmDeletionDialog") && component.getId().isPresent();
            }).findFirst().get());
            confirmDeletionDialog.get().addDetachListener(event1 -> {
                Button acceptButton = (Button) event1.getSource().getChildren().filter(component -> {
                    return component.equals("acceptButton") && component.getId().isPresent();
                }).findFirst().orElseThrow(() -> new NotFoundException("failed to find button by id: acceptButton"));
                acceptButton.addClickListener(event2 -> {
                    productLinkedList.remove(product);
                    grid.getDataProvider().refreshAll();
                });
            });
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
            grid.getDataProvider().refreshItem(product);
        });

        addAttachListener(event -> {
            subViews.removeAll();
            TabSheet tabSheet = getSecondaryNavigation(productTypeEntityRepo);
            tabSheet.addSelectedChangeListener(event1 -> {
                changeGridContent(productService, productTypeEntityRepo.findByName(event1.getSelectedTab().getLabel()).orElseThrow(() -> new NotFoundException("product types not found")));
            });

            subViews.add(tabSheet);
        });
//        HorizontalLayout layout = new HorizontalLayout(grid);
//        layout.setAlignSelf(FlexComponent.Alignment.STRETCH, grid);
//        layout.getStyle().setWidth("100%");
//        layout.setPadding(true);
//        this.add(subViews, grid, productContextMenu);
        this.add(subViews, grid);

    }

    public TabSheet getSecondaryNavigation(ProductTypeEntityRepo productTypeEntityRepo) {
        TabSheet tabSheet = new TabSheet();
        productTypeEntityRepo.findAll().forEach(productType -> tabSheet.add(productType.getName(), new Div(new Text(productType.getName()))));
        tabSheet.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM);
        return tabSheet;
    }

    private void changeGridContent(ProductService productService, ProductTypeEntity productType) {
        productLinkedList.clear();
        productLinkedList.addAll(productService.findByProductTypeOrderByRankAsc(productType));
        grid.getDataProvider().refreshAll();
    }


    public void setupGrid() {
        grid = new Grid<>(Product.class, false);
        grid.addClassName("hover-on-cursor");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(product -> Optional.ofNullable(product.getName()).orElse("Н/д")).setHeader("Название").setResizable(true).setSortable(false).setFrozen(true).setAutoWidth(true).setFlexGrow(0);
        grid.addComponentColumn(product -> {
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
//        grid.addComponentColumn(product -> createStatusIcon(product.getPreviewUrl().isEmpty())).setHeader("Картинка").setResizable(true).setSortable(false);
//        grid.addColumn(product -> Optional.ofNullable(product.getDiscount()).map(Object::toString).orElse("Н/д")).setHeader("Скидка %").setResizable(true).setSortable(false);
        grid.addColumn(product -> Optional.ofNullable(product.getPrice()).map(bigDecimal -> bigDecimal.setScale(2, RoundingMode.UP).toString()).orElse("Н/д")).setHeader("Цена ₽").setResizable(true).setSortable(false);
        grid.addColumn(product -> Optional.ofNullable(product.getMass()).map(Objects::toString).orElse("Н/д")).setHeader("Масса г").setResizable(true).setSortable(false);
        grid.addComponentColumn(product -> new HideProductCheckbox(product, productEditComponent)).setHeader("Сокрытие").setResizable(true).setSortable(false);
        grid.addComponentColumn(product -> new ProductContextMenu(product, productEditComponent));
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

    private static class HideProductCheckbox extends Checkbox {
        public HideProductCheckbox(Product product, ProductEditComponent productEditComponent) {
            this.setValue(!product.isActive());
            this.addValueChangeListener(event -> {
                product.setActive(!event.getValue());
                productEditComponent.setProduct(product);
                productEditComponent.getSave().click();
            });
            this.setTooltipText("Скрыт ли этот продукт");
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
