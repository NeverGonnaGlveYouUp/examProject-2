package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.sun.jdi.event.BreakpointEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Promotion;
import ru.tusur.ShaurmaWebSiteProject.backend.model.PromotionType;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.PromotionRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.ui.components.BigDecimalPriceField;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "Панель администратора - таблица акций", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица акций")
public class AdminPanelPromotionGrid extends Main {
    public static final String name = "Акции";
    private final Grid<Promotion> promotionGrid = new Grid<>(Promotion.class, false);
    private final List<Promotion> promotionList = new ArrayList<>();
    private final Div hint = new Div();
    private final PromotionRepo promotionRepo;
    private Promotion promotionInEditing;
    private Section sidebar;

    public AdminPanelPromotionGrid(PromotionRepo promotionRepo) {
        this.promotionRepo = promotionRepo;
        promotionList.addAll(promotionRepo.findAll());
        promotionInEditing = promotionList.stream().findFirst().orElse(null);
        createGrid();
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, LumoUtility.Overflow.HIDDEN, Layout.FlexDirection.ROW.getClassName());
        Layout layout = new Layout(createHat(), promotionGrid, hint);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setWidth("-webkit-fill-available");
        add(createSidebar(), layout);
        closeSidebar();
    }

    private void createGrid() {
        promotionGrid.getStyle().setMargin("8px").setHeight("500px");
        promotionGrid.addColumn(Promotion::getName).setHeader("Название").setSortable(true).setResizable(true);
        promotionGrid.addColumn(Promotion::getCondition).setHeader("Условие").setSortable(true);
        promotionGrid.addColumn(Promotion::getPromotionEffect).setHeader("Эффект").setSortable(true);
        promotionGrid.addColumn(Promotion::getPromotionType).setHeader("Тип").setSortable(true).setResizable(true);
        promotionGrid.addColumn(
                new ComponentRenderer<>(Checkbox::new, (checkbox, promotion) -> {
                    checkbox.setValue(promotion.isHide());
                    checkbox.addValueChangeListener(_ -> {
                        promotion.setHide(checkbox.getValue());
                        promotionRepo.save(promotion);
                    });
                })).setHeader("Активно");
        promotionGrid.addColumn(
                new ComponentRenderer<>(MenuBar::new, (menuBar, promotion) -> {
                    menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
                    MenuItem deleateMenuItem = createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.TRASH_ALT.getSvgName() + ".svg", "Удалить", _ -> removeProductDialog(promotion), null);
                    deleateMenuItem.addThemeNames("error");
                })).setHeader("Управление");
        promotionGrid.setItems(promotionList);
        refreshGrid();
        hint.setVisible(false);
        hint.setText("Акций нет");
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
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.PLUS_SOLID.getSvgName() + ".svg", "Создать новую акцию", _ -> {
            if (promotionInEditing == null) {
                promotionInEditing = new Promotion();
                promotionInEditing.setName("Н/д");
                promotionInEditing.setDescription("Н/д");
                promotionInEditing.setCondition("Н/д");
                promotionInEditing.setHide(true);
                promotionInEditing.setPromotionEffect(BigDecimal.ZERO);
                promotionInEditing = promotionRepo.save(promotionInEditing);
                promotionList.add(promotionInEditing);
                promotionGrid.setItems(promotionList);
                refreshGrid();
                openCreateProductOptionDialog();
            }
            promotionGrid.select(promotionInEditing);
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

        TextField textField = new TextField("Название новой акции товара:");

        Button confirmButton = new Button("Создать", _ -> {
            Promotion promotion = new Promotion();
            promotion.setName(textField.getValue());
            promotionRepo.save(promotion);
            promotionList.clear();
            promotionList.addAll(promotionRepo.findAll());
            promotionGrid.setItems(promotionList);
            refreshGrid();
            dialog.close();
        });
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());

        dialog.getHeader().add(closeButton);
        dialog.getFooter().add(confirmButton);
        dialog.add(textField);
    }

    private void refreshGrid() {
        if (!promotionList.isEmpty()) {
            promotionGrid.setVisible(true);
            hint.setVisible(false);
            promotionGrid.getDataProvider().refreshAll();
        } else {
            promotionGrid.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void removeProductDialog(Promotion promotion) {
        if (promotion == null)
            return;

        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        Div content = new Div();
        content.setText("Вы уверены, что хотите удалить этот элемент?");

        Button confirmButton = new Button("Удалить", _ -> {
            promotionGrid.select(promotionList.getFirst());
            promotionList.remove(promotion);
            refreshGrid();
            promotionRepo.delete(promotion);
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
        AtomicReference<Promotion> promotionAtomicReference = new AtomicReference<>();
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
        nameField.setPlaceholder("Название акции");

        TextField conditionField = new TextField();
        conditionField.setLabel("Условие");
        conditionField.setPlaceholder("Условие акции");

        BigDecimalPriceField effectField = new BigDecimalPriceField();
        effectField.setWidth("auto");
        effectField.setLabel("Эффект");
        effectField.setPlaceholder("Эффект");
        effectField.setHelperText("Если это константная скидка, то эффект должен быть меньше нуля.");

        Select<PromotionType> promotionTypeSelect = new Select<>();
        promotionTypeSelect.setLabel("Товар");
        promotionTypeSelect.setPlaceholder("Тип акции");
        promotionTypeSelect.setItems(PromotionType.values());
        promotionTypeSelect.setValue(promotionInEditing == null ? null : promotionInEditing.getPromotionType());

        TextArea descriptionField = new TextArea();
        descriptionField.setLabel("Описание");
        descriptionField.setPlaceholder("Описание акции");
        descriptionField.getStyle().setMarginTop("8px");
        int charLimit = 1000;
        descriptionField.setMaxLength(charLimit);
        descriptionField.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
        });

        Button updateProductButton = new Button("Сохранить");
        updateProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProductButton.addClickListener(event -> {
            Promotion o = promotionAtomicReference.get();
            if (!promotionList.isEmpty()) {
                promotionGrid.setVisible(true);
                hint.setVisible(false);
                promotionList.remove(o);
                o.setName(conditionField.getValue());
                o.setCondition(conditionField.getValue());
                o.setPromotionType(promotionTypeSelect.getValue());
                o.setDescription(descriptionField.getValue());
                promotionList.add(o);
                promotionRepo.save(o);
                promotionGrid.getDataProvider().refreshItem(o);
            } else {
                promotionGrid.setVisible(false);
                hint.setVisible(true);
            }
            if (o.equals(promotionInEditing)) {
                promotionInEditing = null;
                promotionRepo.save(o);
            } else promotionRepo.save(o);
        });

        Layout form = new Layout(nameField, conditionField, promotionTypeSelect, effectField, descriptionField, updateProductButton);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);
        promotionGrid.addItemDoubleClickListener(_ -> openSidebar());
        promotionGrid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent())
                promotionAtomicReference.set(event.getFirstSelectedItem().get());
            nameField.setValue(promotionAtomicReference.get().getName());
            conditionField.setValue(promotionAtomicReference.get().getCondition());
            effectField.setValue(promotionAtomicReference.get().getPromotionEffect());
            promotionTypeSelect.setValue(promotionAtomicReference.get().getPromotionType());
            descriptionField.setValue(promotionAtomicReference.get().getDescription());
        });
        this.sidebar = new Section(header, form);
        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0", "transition-all", "z-10");
        this.sidebar.setWidth(25, Unit.REM);
        return this.sidebar;
    }

}
