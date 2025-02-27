package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductOptionRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.PromotionService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.BigDecimalPriceField;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Route(value = "Панель администратора - таблица добавок", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица добавок")
public class AdminPanelProductsOptionsGrid extends Main {

    public static final String name = "Добавки";
    private final ProductOptionRepo productOptionRepo;
    private final PromotionService promotionService;
    private final Grid<ProductOption> productOptionGrid = new Grid<>(ProductOption.class, false);
    private final List<ProductOption> productOptions = new ArrayList<>();
    private final Div hint = new Div();
    private ProductOption productOptionInEditing;
    private Section sidebar;

    public AdminPanelProductsOptionsGrid(ProductOptionRepo productOptionRepo, PromotionService promotionService) {
        this.productOptionRepo = productOptionRepo;
        this.promotionService = promotionService;
        productOptions.addAll(productOptionRepo.findAll());
        productOptionInEditing = productOptions.stream().filter(productOption -> productOption.getName().equals("Н/д")).findFirst().orElse(null);
        createGrid();
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, LumoUtility.Overflow.HIDDEN, Layout.FlexDirection.ROW.getClassName());
        Layout layout = new Layout(createHat(), productOptionGrid, hint);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setWidth("-webkit-fill-available");
        add(createSidebar(), layout);
        closeSidebar();
    }

    private void createGrid() {
        productOptionGrid.getStyle().setMargin("8px").setHeight("500px");
        productOptionGrid.addColumn(ProductOption::getName).setHeader("Название").setSortable(true).setResizable(true);
        productOptionGrid.addColumn(productOption -> productOption.getPrice().toString() + " ₽").setHeader("Цена").setSortable(true);
        productOptionGrid.addColumn(productOption -> productOption.getMass().toString() + " г").setHeader("Масса").setSortable(true);
        productOptionGrid.addColumn(
                new ComponentRenderer<>(MenuBar::new, (menuBar, productOption) -> {
                    menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
                    MenuItem deleateMenuItem = createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.TRASH_ALT.getSvgName() + ".svg", "Удалить", _ -> removeProductDialog(productOption), null);
                    deleateMenuItem.addThemeNames("error");
                })).setHeader("Управление");
        productOptionGrid.setItems(productOptions);
        refreshGrid();
        hint.setVisible(false);
        hint.setText("Добавок нет");
        hint.getStyle().set("padding", "var(--lumo-size-l)")
                .set("text-align", "center").set("font-style", "italic")
                .set("color", "var(--lumo-contrast-70pct)");
    }

    private MenuItem createMenuItem(MenuBar menu, String iconName, String tooltipText, ComponentEventListener<ClickEvent<MenuItem>> listener, String label) {
        SvgIcon icon = new SvgIcon(iconName);
        MenuItem menuItem = menu.addItem(icon, tooltipText, listener);
        if (label != null) {
            menuItem.setAriaLabel(label);
            menuItem.add(new Text(label));
        }
        return menuItem;
    }

    private Layout createHat() {
        Layout layout = new Layout();
        layout.setFlexDirection(Layout.FlexDirection.ROW);
        layout.setGap(Layout.Gap.SMALL);
        layout.getStyle().setMargin("8px");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
        menuBar.getStyle().setPaddingTop("35px");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.PLUS_SOLID.getSvgName() + ".svg", "Создать новую добавку", _ -> {
            if (productOptionInEditing == null) {
                productOptionInEditing = new ProductOption();
                productOptionInEditing.setName("Н/д");
                productOptionInEditing.setMass(0);
                productOptionInEditing.setPrice(BigDecimal.ZERO);
                productOptionInEditing = productOptionRepo.save(productOptionInEditing);
                productOptions.add(productOptionInEditing);
                productOptionGrid.setItems(productOptions);
                refreshGrid();
                openCreateProductOptionDialog();
            }
            productOptionGrid.select(productOptionInEditing);
            openSidebar();
        }, "Объект");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.BARS_SOLID.getSvgName() + ".svg", "Открыть форму добавки", _ -> toggleSidebar(), "Форма");

        layout.add(menuBar);

        return layout;
    }

    private void openCreateProductOptionDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        TextField textField = new TextField("Название нового типа товара:");

        Button confirmButton = new Button("Создать", _ -> {
            ProductOption productOption = new ProductOption();
            productOption.setName(textField.getValue());
            productOptionRepo.save(productOption);
            productOptions.clear();
            productOptions.addAll(productOptionRepo.findAll());
            productOptionGrid.setItems(productOptions);
            refreshGrid();
            dialog.close();
        });
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());

        dialog.getHeader().add(closeButton);
        dialog.getFooter().add(confirmButton);
        dialog.add(textField);
    }

    private void refreshGrid() {
        if (!productOptions.isEmpty()) {
            productOptionGrid.setVisible(true);
            hint.setVisible(false);
            productOptionGrid.getDataProvider().refreshAll();
        } else {
            productOptionGrid.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void removeProductDialog(ProductOption productOption) {
        if (productOption == null)
            return;

        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        Div content = new Div();
        content.setText("Вы уверены, что хотите удалить этот элемент?");

        Button confirmButton = new Button("Удалить", _ -> {
            productOptionGrid.select(productOptions.getFirst());
            productOptions.remove(productOption);
            refreshGrid();
            productOptionRepo.delete(productOption);
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Отмена", _ -> confirmDialog.close());

        confirmDialog.setHeaderTitle("Удаление");

        Layout layout = new Layout();
        layout.setJustifyContent(Layout.JustifyContent.BETWEEN);
        layout.add(confirmButton, cancelButton);
        layout.getStyle().set("min-width", "-webkit-fill-available");

        confirmDialog.getFooter().add(layout);
        confirmDialog.add(content);
        confirmDialog.open();
    }

    private void toggleSidebar() {
        if (this.sidebar.isEnabled()) {
            closeSidebar();
        } else {
            openSidebar();
        }
    }

    private void openSidebar() {
        this.sidebar.setEnabled(true);
        this.sidebar.addClassNames(LumoUtility.Border.RIGHT);
        this.sidebar.getStyle().remove("margin-inline-start");
        this.sidebar.addClassNames("start-0");
        this.sidebar.removeClassName("-start-full");
    }

    private void closeSidebar() {
        this.sidebar.setEnabled(false);
        this.sidebar.removeClassName(LumoUtility.Border.RIGHT);
        // Desktop
        this.sidebar.getStyle().set("margin-inline-start", "-25rem");
        // Mobile
        this.sidebar.addClassNames("-start-full");
        this.sidebar.removeClassName("start-0");
    }

    private Section createSidebar() {
        AtomicReference<ProductOption> productOptionAtomicReference = new AtomicReference<>();
        H2 title = new H2("Редактирование");
        title.addClassNames(LumoUtility.FontSize.MEDIUM);

        Button close = new Button(LineAwesomeIcon.TIMES_SOLID.create(), e -> closeSidebar());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.setAriaLabel("Закрыть");
        close.setTooltipText("Закрыть");

        Layout header = new Layout(title, close);
        header.addClassNames(LumoUtility.Padding.End.MEDIUM, LumoUtility.Padding.Start.LARGE, LumoUtility.Padding.Vertical.SMALL);
        header.setAlignItems(Layout.AlignItems.CENTER);
        header.setJustifyContent(Layout.JustifyContent.BETWEEN);

        TextField nameField = new TextField();
        nameField.setLabel("Название");
        nameField.setPlaceholder("Название товара");

        IntegerField massField = new IntegerField();
        massField.setLabel("Масса г");
        massField.setPlaceholder("Масса в граммах");
        massField.setMin(1);

        //todo this value can be negative, fix it some ady, maybe
        BigDecimalPriceField priceField = new BigDecimalPriceField();
        priceField.setWidth("auto");
        priceField.setLabel("Цена");
        priceField.setPlaceholder("Цена");

        Button updateProductButton = new Button("Сохранить");
        updateProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProductButton.addClickListener(event -> {
            ProductOption o = productOptionAtomicReference.get();
            if (!productOptions.isEmpty()) {
                productOptionGrid.setVisible(true);
                hint.setVisible(false);
                productOptions.remove(o);
                o.setName(nameField.getValue());
                o.setMass(massField.getValue());
                o.setPrice(priceField.getValue());
                productOptions.add(o);
                productOptionRepo.save(o);
                productOptionGrid.getDataProvider().refreshItem(o);
            } else {
                productOptionGrid.setVisible(false);
                hint.setVisible(true);
            }
            if (o.equals(productOptionInEditing)) {
                productOptionInEditing = null;
                productOptionRepo.save(o);
            } else productOptionRepo.save(o);
        });

        Layout form = new Layout(nameField, priceField, massField, updateProductButton);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);
        productOptionGrid.addItemDoubleClickListener(event -> openSidebar());
        productOptionGrid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent())
                productOptionAtomicReference.set(event.getFirstSelectedItem().get());
            massField.setValue(productOptionAtomicReference.get().getMass());
            priceField.setValue(productOptionAtomicReference.get().getPrice());
            nameField.setValue(productOptionAtomicReference.get().getName());
        });
        this.sidebar = new Section(header, form);
        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0",
                "transition-all", "z-10");
        this.sidebar.setWidth(25, Unit.REM);
        return this.sidebar;
    }

}
