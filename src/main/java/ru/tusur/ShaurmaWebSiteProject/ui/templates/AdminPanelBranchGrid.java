package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProductKey;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.PromotionService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.components.i18n.UploadExamplesI18N;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Route(value = "Панель администратора - таблица филиалов", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица филиалов")
public class AdminPanelBranchGrid extends Main {

    public static final String name = "Филиалы";
    private final BranchRepo branchRepo;
    private final BranchProductRepo branchProductRepo;
    private final ProductRepo productRepo;
    private final PromotionService promotionService;
    private final Grid<BranchProduct> branchProductGrid = new Grid<>(BranchProduct.class, false);
    private final ComboBox<Branch> branchComboBox = new ComboBox<>();
    private final List<BranchProduct> branchProducts = new ArrayList<>();
    private final List<Branch> branches = new ArrayList<>();
    private final Div hint = new Div();
    private BranchProduct branchProductInEditing;
    private Layout imageLayout;
    private Section sidebar;
    private Branch currentBranch;
    private boolean changeBranchFlag = false;


    public AdminPanelBranchGrid(BranchRepo branchRepo, BranchProductRepo branchProductRepo, ProductRepo productRepo, PromotionService promotionService) {
        this.branchRepo = branchRepo;
        this.branchProductRepo = branchProductRepo;
        this.productRepo = productRepo;
        this.promotionService = promotionService;
        List<Branch> branchList = branchRepo.findAll();
        this.currentBranch = branchList.getFirst();
        branches.addAll(branchList);
        branchProducts.addAll(branchProductRepo.findAll());
        branchProductInEditing = branchProducts.stream().findFirst().orElse(null);
        createGrid();
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, LumoUtility.Overflow.HIDDEN, Layout.FlexDirection.ROW.getClassName());
        Layout layout = new Layout(createHat(), branchProductGrid, hint);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setWidth("-webkit-fill-available");
        add(createSidebar(), layout);
        closeSidebar();
    }

    private void createGrid() {
        branchProductGrid.getStyle().setMargin("8px").setHeight("500px");
        branchProductGrid.addColumn(branchProduct -> branchProduct.getProduct().getName()).setHeader("Название").setSortable(true).setResizable(true);
        branchProductGrid.addColumn(
                new ComponentRenderer<>(Checkbox::new, (checkbox, branchProduct) -> {
                    checkbox.setValue(branchProduct.isHide());
                    checkbox.addValueChangeListener(_ -> {
                        branchProduct.setHide(checkbox.getValue());
                        branchProductRepo.save(branchProduct);
                    });
                })).setHeader("Скрыт");
        branchProductGrid.setItems(branchProducts);
        refreshGrid();
        hint.setVisible(false);
        hint.setText("Отношений для этого филиала нет");
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

        ComboBox.ItemFilter<Branch> filter = (branch, filterString) -> (branch.getAddress()).toLowerCase().contains(filterString.toLowerCase());
        branchComboBox.setWidth("fit-content");
        branchComboBox.setLabel("Филиал");
        branchComboBox.setItems(filter, branches);
        branchComboBox.setItemLabelGenerator(Branch::getAddress);
        branchComboBox.setAllowCustomValue(false);
        branchComboBox.setValue(currentBranch);
        branchComboBox.addValueChangeListener(event -> {
            currentBranch = event.getValue();
            branchProducts.clear();
            branchProducts.addAll(branchProductRepo.findAllByBranch(currentBranch));
            branchProductGrid.setItems(branchProducts);
            if (branchProductInEditing != null) branchProductInEditing.setBranch(currentBranch);
            refreshGrid();
            changeBranchFlag = true;
            if (!branchProducts.isEmpty()) branchProductGrid.select(branchProducts.getFirst());
            else branchProductGrid.select(null);
        });

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
        menuBar.getStyle().setPaddingTop("35px");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.PLUS_CIRCLE_SOLID.getSvgName() + ".svg", "Создать новый филиал", _ -> openCreateBranchDialog(), "Филиал");
//        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.TRASH_ALT_SOLID.getSvgName() + ".svg", "Удалить филиал", _ -> removeBranchDialog(), "Удалить");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.PLUS_SOLID.getSvgName() + ".svg", "Создать новый объект связи", _ -> {
            if (branchProductInEditing == null) {
                branchProductInEditing = new BranchProduct();
                currentBranch = branchComboBox.getValue();
                branchProductInEditing.setBranch(currentBranch);
                branchProductInEditing.setProduct(null);
                changeBranchFlag = true;
//                branchProductInEditing.setProduct(productRepo.findById(1L).get());
//                branchProductInEditing.setId(new BranchProductKey(productRepo.findById(1L).get().getId(), currentBranch.getId()));
//                branchProductInEditing = branchProductRepo.save(branchProductInEditing);
//                branchProducts.add(branchProductInEditing);
//                branchProductGrid.setItems(branchProducts);
//                refreshGrid();
            }
            branchProductGrid.select(branchProductInEditing);
            openSidebar();
        }, "Объект");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.BARS_SOLID.getSvgName() + ".svg", "Открыть форму отношения филиал-продукт", _ -> toggleSidebar(), "Форма");

        layout.add(branchComboBox, menuBar);

        return layout;
    }

    private void removeBranchDialog() {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        Div content = new Div();
        content.setText("Вы уверены, что хотите удалить этот элемент?");

        Button confirmButton = new Button("Удалить", event -> {
//            branchComboBox.setValue(currentBranch);
            branchProductRepo.deleteAll(branchProducts);
            promotionService.createBranchAddressMap();
            branchProducts.clear();
            branches.remove(currentBranch);
            branchRepo.delete(currentBranch);
            currentBranch = branches.isEmpty() ? null : branches.getFirst();
            branchProducts.addAll(branchProductRepo.findAllByBranch(currentBranch));
            branchComboBox.setItems(branches);
            branchComboBox.setValue(currentBranch);
            branchProductGrid.setItems(branchProducts);
            refreshGrid();
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Отмена", event -> confirmDialog.close());

        confirmDialog.setHeaderTitle("Удаление");

        Layout layout = new Layout();
        layout.setJustifyContent(Layout.JustifyContent.BETWEEN);
        layout.add(confirmButton, cancelButton);
        layout.getStyle().set("min-width", "-webkit-fill-available");

        confirmDialog.getFooter().add(layout);
        confirmDialog.add(content);
        confirmDialog.open();
    }

    private void openCreateBranchDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        TextField textField = new TextField("Адрес нового филиала:");

        Button confirmButton = new Button("Создать", event -> {
            Branch branch = new Branch();
            branch.setHide(true);
            branch.setOpenFrom(new Date());
            branch.setOpenTill(new Date());
            branch.setAddress(textField.getValue());
            branch.setDeliveryStreets("");
            currentBranch = branchRepo.save(branch);
            branches.add(currentBranch);
            branchComboBox.setItems(branches);
            branchProducts.clear();
            branchProducts.addAll(branchProductRepo.findAllByBranch(branch));
            branchProductGrid.setItems(branchProducts);
            branchComboBox.setValue(currentBranch);
            promotionService.createBranchAddressMap();
            refreshGrid();
            dialog.close();
        });
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());

        dialog.getHeader().add(closeButton);
        dialog.getFooter().add(confirmButton);
        dialog.add(textField);
        dialog.open();
    }

    private void refreshGrid() {
        if (!branchProducts.isEmpty()) {
            branchProductGrid.setVisible(true);
            hint.setVisible(false);
            branchProductGrid.getDataProvider().refreshAll();
        } else {
            branchProductGrid.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void openRemoveBranchProductDialog(BranchProduct branchProduct) {
        if (branchProduct == null)
            return;

        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        Div content = new Div();
        content.setText("Вы уверены, что хотите удалить этот элемент?");

        Button confirmButton = new Button("Удалить", event -> {
            branchProductGrid.select(branchProducts.getFirst());
            branchProducts.remove(branchProduct);
            refreshGrid();
            branchProductRepo.delete(branchProduct);
            confirmDialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Отмена", event -> confirmDialog.close());

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

        List<Product> products = productRepo.findAll();
        Select<Product> productSelect = new Select<>();
        productSelect.setEnabled(false);
        productSelect.setLabel("Товар");
        productSelect.setPlaceholder("Связанный товар");
        productSelect.setItems(products);
        productSelect.setValue(branchProductInEditing.getProduct());

        TextField addressField = new TextField();
        addressField.setLabel("Адрес филиала");
        addressField.setPlaceholder("Адрес филиала");

        //todo add phone num
        TextField phoneField = new TextField();
        phoneField.setLabel("Телефон филиала");
        phoneField.setPlaceholder("Телефон филиала");

        TimePicker openFromTimePicker = new TimePicker();
        openFromTimePicker.setLabel("Открыто с");
        openFromTimePicker.setStep(Duration.ofMinutes(30));
        openFromTimePicker.setValue(LocalTime.of(12, 30));

        TimePicker openTillTimePicker = new TimePicker();
        openTillTimePicker.setLabel("Открыто до");
        openTillTimePicker.setStep(Duration.ofMinutes(30));
        openTillTimePicker.setValue(LocalTime.of(12, 30));

        TextArea deliveryStreetsField = new TextArea();
        deliveryStreetsField.setLabel("Адреса бесплатной доставки");
        deliveryStreetsField.setPlaceholder("Адреса бесплатной доставки");
        deliveryStreetsField.getStyle().setMarginTop("8px");

        Checkbox hideBranchCheckbox = new Checkbox();
        hideBranchCheckbox.setLabel("Скрыть филиал");
        hideBranchCheckbox.setValue(currentBranch.isHide());

        Button updateProductButton = new Button("Сохранить");
        updateProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProductButton.addClickListener(_ -> {
            Branch o = currentBranch;
            if (!branchProducts.isEmpty()) {
                branchProductGrid.setVisible(true);
                hint.setVisible(false);
                o.setHide(hideBranchCheckbox.getValue());
                o.setAddress(addressField.getValue());
                o.setPhoneNumber(phoneField.getValue());
                o.setDeliveryStreets(deliveryStreetsField.getValue());
                o.setOpenFrom(Date.from(LocalDate.EPOCH.atTime(openFromTimePicker.getValue()).atZone(ZoneId.systemDefault()).toInstant()));
                o.setOpenTill(Date.from(LocalDate.EPOCH.atTime(openTillTimePicker.getValue()).atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                branchProductGrid.setVisible(false);
                hint.setVisible(true);
            }

            if (branchProductInEditing != null) {
                if (o.equals(branchProductInEditing.getBranch()) && productSelect.getValue() != null) {
                    branchProductInEditing.setProduct(productSelect.getValue());
                    branchProductInEditing.setId(new BranchProductKey(productSelect.getValue().getId(), currentBranch.getId()));
                    branchProductInEditing = branchProductRepo.save(branchProductInEditing);
                    branchProducts.add(branchProductInEditing);
                    branchProductGrid.setItems(branchProducts);
                    refreshGrid();
                }
            }
            branchProductInEditing = null;
            currentBranch = branchRepo.save(o);
            promotionService.createBranchAddressMap();
        });

        imageLayout = new Layout(getImage(currentBranch));
        imageLayout.setWidth("70%");
        imageLayout.addClassName(LumoUtility.AlignSelf.CENTER);
        imageLayout.addClickListener(event -> {
            if (currentBranch != null) attachUploadDialog(imageLayout, currentBranch);
        });

        Layout timeLayout = new Layout(openFromTimePicker, openTillTimePicker);
        timeLayout.setGap(Layout.Gap.MEDIUM);
        timeLayout.getStyle().set("display", "contents");
        timeLayout.setFlexDirection(Layout.FlexDirection.ROW);

        Layout form = new Layout(imageLayout, addressField, productSelect, phoneField, timeLayout, deliveryStreetsField, hideBranchCheckbox, updateProductButton);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);

        branchProductGrid.addItemDoubleClickListener(event -> openSidebar());
        branchProductGrid.addSelectionListener(event -> {
            if (currentBranch == null) {
                currentBranch = branches.getFirst();
                branchComboBox.setValue(currentBranch);
            }

            event.getFirstSelectedItem().ifPresent(branchProduct -> {
                if (changeBranchFlag) {
                    productSelect.setValue(branchProduct.getProduct());
                    productSelect.setEnabled(true);
                    changeBranchFlag = false;
                } else {
                    productSelect.setValue(branchProduct.getProduct());
                    productSelect.setEnabled(false);
                }
            });
            addressField.setValue(currentBranch.getAddress());
            phoneField.setValue(currentBranch.getPhoneNumber());
            openFromTimePicker.setValue(LocalTime.ofInstant(currentBranch.getOpenFrom().toInstant(), ZoneId.systemDefault()));
            openTillTimePicker.setValue(LocalTime.ofInstant(currentBranch.getOpenTill().toInstant(), ZoneId.systemDefault()));
            deliveryStreetsField.setValue(currentBranch.getDeliveryStreets());
            imageLayout.removeAll();
            imageLayout.add(getImage(currentBranch));
        });
        this.sidebar = new Section(header, form);
        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0",
                "transition-all", "z-10");
        this.sidebar.setWidth(25, Unit.REM);
        return this.sidebar;
    }

    private Image getImage(Branch branch) {
        Image image;
        if (branch != null && branch.getDeliveryZoneUrl() == null) {
            image = new Image("line-awesome/svg/" + LineAwesomeIcon.FILE_IMAGE.getSvgName() + ".svg", "");
            image.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignContent.CENTER);
        } else if (branch != null) {
            image = new Image(ImageResourceUtils.getImageResource(branch.getDeliveryZoneUrl()), branch.getAddress());
        } else {
            image = new Image("line-awesome/svg/" + LineAwesomeIcon.FILE_IMAGE.getSvgName() + ".svg", "");
            image.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignContent.CENTER);
        }
        image.getStyle().set("min-width", "-webkit-fill-available");
        return image;
    }

    private void attachUploadDialog(Layout imageLayout, Branch branch) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Загрузка изображения");
        VerticalLayout verticalLayout1 = new VerticalLayout();
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream();
            try {
                Files.write(Path.of("src/main/resources/META-INF/resources/images/" + fileName), buffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            StreamResource imageResource = new StreamResource(fileName, (InputStreamFactory) () -> inputStream);

            branch.setDeliveryZoneUrl("src/main/resources/META-INF/resources/images/" + fileName);
            imageLayout.removeAll();
            Image myStreamedImage = new Image(imageResource, "My Streamed Image");
            myStreamedImage.getStyle().set("min-width", "-webkit-fill-available");
            imageLayout.add(myStreamedImage);
            dialog.close();
        });
        UploadExamplesI18N i18n = new UploadExamplesI18N();
        H4 title = new H4("Загрузить изображение");
        Paragraph hint = new Paragraph("Размер файла должен быть меньше или равен 5 МБ. Принимаются только файлы \".png\", \".jpeg\" и \".jpg\"...");
        upload.setI18n(i18n);
        upload.setWidthFull();
        upload.setAcceptedFileTypes(".png", ".jpeg", ".jpg");
        upload.setMaxFileSize(5 * 1024 * 1024);
        upload.setMaxFiles(1);
        verticalLayout1.setWidthFull();
        verticalLayout1.add(title, hint, upload);
        dialog.add(verticalLayout1);
        dialog.open();
    }
}
