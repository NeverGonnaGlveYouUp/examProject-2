package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
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
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductContentsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductOptionRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.BigDecimalPriceField;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.components.i18n.UploadExamplesI18N;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "Панель администратора - таблица товара", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица товара")
public class AdminPanelProductGrid extends Main {

    public static final String name = "Товары";
    private final ProductContentsRepo productContentsRepo;
    private final ProductService productService;
    private final ProductTypeEntityRepo productTypeEntityRepo;
    private final ProductRepo productRepo;
    private final ProductOptionRepo productOptionRepo;
    private final Grid<Product> productGrid = new Grid<>(Product.class, false);
    private final ComboBox<ProductTypeEntity> productTypeEntityComboBox = new ComboBox<>();
    private final List<Product> products = new ArrayList<>();
    private List<ProductContent> productContents = new ArrayList<>();
    private final List<ProductTypeEntity> productTypeEntities = new ArrayList<>();
    private final Div hint = new Div();
    private final Layout productComponentsLayout = new Layout();
    private final Layout componentsLayout = new Layout();
    private Product productInEditing;
    private Layout imageLayout;
    private Section sidebar;
    private ProductTypeEntity currentProductType;


    public AdminPanelProductGrid(ProductContentsRepo productContentsRepo, ProductService productService, ProductTypeEntityRepo productTypeEntityRepo, ProductRepo productRepo, ProductOptionRepo productOptionRepo) {
        this.productContentsRepo = productContentsRepo;
        this.productService = productService;
        this.productTypeEntityRepo = productTypeEntityRepo;
        this.productRepo = productRepo;
        this.productOptionRepo = productOptionRepo;
        List<ProductTypeEntity> productTypeEntityRepoAll = productTypeEntityRepo.findAll();
        this.currentProductType = productTypeEntityRepoAll.getFirst();
        productTypeEntities.addAll(productTypeEntityRepoAll);
        products.addAll(productService.findByProductTypeOrderByRankAsc(currentProductType));
        productInEditing = products.stream().filter(product -> product.getName().equals("Н/д")).findFirst().orElse(null);
        createGrid();
        getStyle().set("overflow", "visible");
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, Layout.FlexDirection.ROW.getClassName());
        Layout layout = new Layout(createHat(), productGrid, hint);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setWidth("-webkit-fill-available");
        Scroller scroller = new Scroller(createSidebar());
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        add(scroller, layout);
        closeSidebar();
    }

    private void createGrid() {
        DecimalFormat df = new DecimalFormat("#.##");
        productGrid.getStyle().setMargin("8px").setHeight("500px");
        productGrid.addColumn(Product::getName).setHeader("Название").setSortable(true).setResizable(true);
        productGrid.addColumn(product -> product.getPrice().toString() + " ₽").setHeader("Цена").setSortable(true);
        productGrid.addColumn(product -> product.getMass().toString() + " г").setHeader("Масса").setSortable(true);
        productGrid.addColumn(product -> df.format(product.getReviews().stream().mapToInt(Review::getGrade).sum() / (double) product.getReviews().size())).setHeader("Рейтинг").setSortable(true);
        productGrid.addColumn(
                new ComponentRenderer<>(MenuBar::new, (menuBar, product) -> {
                    menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
                    createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.SHARE_SQUARE.getSvgName() + ".svg", "Перейти на страницу", _ -> UI.getCurrent().navigate(ProductDetailsView.class, product.getName()+"&"+"Вершинина, 38"), null);
                    MenuItem deleateMenuItem = createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.TRASH_ALT.getSvgName() + ".svg", "Удалить", _ -> removeProductDialog(product), null);
                    deleateMenuItem.addThemeNames("error");
                })).setHeader("Управление");
        productGrid.setItems(products);
        refreshGrid();
        hint.setVisible(false);
        hint.setText("Товаров этого типа нет");
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

        ComboBox.ItemFilter<ProductTypeEntity> filter = (productType, filterString) -> (productType.getName()).toLowerCase().contains(filterString.toLowerCase());
        productTypeEntityComboBox.setWidth("fit-content");
        productTypeEntityComboBox.setLabel("Тип товара");
        productTypeEntityComboBox.setItems(filter, productTypeEntities);
        productTypeEntityComboBox.setItemLabelGenerator(ProductTypeEntity::getName);
        productTypeEntityComboBox.setAllowCustomValue(false);
        productTypeEntityComboBox.setValue(currentProductType);
        productTypeEntityComboBox.addValueChangeListener(event -> {
            currentProductType = event.getValue();
            products.clear();
            products.addAll(productService.findByProductTypeOrderByRankAsc(currentProductType));
            productGrid.setItems(products);
            productInEditing = null;
            refreshGrid();
            productGrid.select(products.getFirst());
        });

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
        menuBar.getStyle().setPaddingTop("35px");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.PLUS_CIRCLE_SOLID.getSvgName() + ".svg", "Создать новый тип продукта", _ -> openCreateProductTypeEntityDialog(), "Тип");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.TRASH_ALT.getSvgName() + ".svg", "Удалить этот тип продукта", _ -> removeProductTypeDialog(), "Удалить тип");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.PLUS_SOLID.getSvgName() + ".svg", "Создать новый объект продукта", _ -> {
            if (productInEditing == null) {
                productInEditing = new Product();
                productInEditing.setProductType(currentProductType);
                productInEditing.setName("Н/д");
                productInEditing.setDescription("Н/д");
                productInEditing.setMass(0);
                productInEditing.setPreviewUrl(null);
                productInEditing.setPrice(BigDecimal.ZERO);
                productInEditing.setProductOptions(new HashSet<ProductOption>());
                productInEditing.setReviews(new HashSet<>());
                productInEditing.setRank(products.size() + 1);
                productInEditing = productRepo.save(productInEditing);
                products.add(productInEditing);
                productGrid.setItems(products);
                refreshGrid();
            }
            productGrid.select(productInEditing);
            openSidebar();
        }, "Объект");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.BARS_SOLID.getSvgName() + ".svg", "Открыть форму продукта", _ -> toggleSidebar(), "Форма");

        layout.add(productTypeEntityComboBox, menuBar);

        return layout;
    }

    private void openCreateProductTypeEntityDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        TextField textField = new TextField("Название нового типа товара:");

        Button confirmButton = new Button("Создать", event -> {
            ProductTypeEntity productTypeEntity = new ProductTypeEntity();
            productTypeEntity.setName(textField.getValue());
            productTypeEntityRepo.save(productTypeEntity);
            currentProductType = productTypeEntity;
            products.clear();
            products.addAll(productService.findByProductTypeOrderByRankAsc(currentProductType));
            productTypeEntities.add(currentProductType);
            productTypeEntityComboBox.setItems(productTypeEntities);
            productGrid.setItems(products);
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
        if (!products.isEmpty()) {
            productGrid.setVisible(true);
            hint.setVisible(false);
            productGrid.getDataProvider().refreshAll();
        } else {
            productGrid.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void removeProductTypeDialog() {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        Div content = new Div();
        content.setText("Вы уверены, что хотите удалить этот элемент?");

        Button confirmButton = new Button("Удалить", event -> {
            productTypeEntities.remove(currentProductType);
            productTypeEntityRepo.delete(currentProductType);
            currentProductType = productTypeEntities.stream().findFirst().orElse(null);
            products.forEach(productService::delete);
            products.clear();
            productTypeEntityComboBox.setItems(productTypeEntities);
            products.addAll(productService.findByProductTypeOrderByRankAsc(currentProductType));
            productGrid.setItems(products);
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

    private void removeProductDialog(Product product) {
        if (product == null)
            return;

        Dialog confirmDialog = new Dialog();
        confirmDialog.setCloseOnEsc(false);
        confirmDialog.setCloseOnOutsideClick(false);

        Div content = new Div();
        content.setText("Вы уверены, что хотите удалить этот элемент?");

        Button confirmButton = new Button("Удалить", event -> {
            productGrid.select(products.getFirst());
            products.remove(product);
            refreshGrid();
            productService.delete(product);
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
        AtomicReference<Product> product = new AtomicReference<>();
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
        header.setWidth(18, Unit.REM);

        TextField nameField = new TextField();
        nameField.setLabel("Название");
        nameField.setPlaceholder("Название товара");

        IntegerField massField = new IntegerField();
        massField.setLabel("Масса г");
        massField.setPlaceholder("Масса в граммах");
        massField.setMin(1);

        //todo this value can be negative, fix it
        BigDecimalPriceField priceField = new BigDecimalPriceField();
        priceField.setWidth("auto");
        priceField.setLabel("Цена");
        priceField.setPlaceholder("Цена");

        TextArea descriptionField = new TextArea();
        descriptionField.setLabel("Описание");
        descriptionField.setPlaceholder("Описание товара");
        descriptionField.getStyle().setMarginTop("8px");
        int charLimit = 1000;
        descriptionField.setMaxLength(charLimit);
        descriptionField.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
        });

        MultiSelectComboBox<ProductOption> productOptionMultiSelectComboBox = new MultiSelectComboBox<>("Добавки");
        List<ProductOption> productOptions = productOptionRepo.findAll();
        productOptionMultiSelectComboBox.setItems(productOptions);
        productOptionMultiSelectComboBox.setAutoExpand(MultiSelectComboBox.AutoExpandMode.HORIZONTAL);

        fillProductsComponentLayout();

        Button updateProductButton = new Button("Сохранить");
        updateProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProductButton.addClickListener(_ -> {
            Product o = product.get();
            if (!products.isEmpty() && o != null) {
                productGrid.setVisible(true);
                hint.setVisible(false);
                products.remove(o);
                o.setName(nameField.getValue());
                o.setMass(massField.getValue());
                o.setPrice(priceField.getValue());
                o.setDescription(descriptionField.getValue());
                Set<ProductOption> productOptions1 = new HashSet<>(o.getProductOptions());
                productOptions1.removeAll(productOptionMultiSelectComboBox.getSelectedItems());
                o.setProductOptions(productOptions1);
                o.getProductOptions().forEach(productOption -> productOption.getProductSet().remove(o));
                productOptionRepo.saveAll(o.getProductOptions());
                o.setProductOptions(productOptionMultiSelectComboBox.getSelectedItems());
                o.getProductOptions().forEach(productOption -> productOption.getProductSet().add(o));
                productOptionRepo.saveAll(o.getProductOptions());
                products.add(o);
                o.setContents(productContents);
                productService.update(o);
                productGrid.getDataProvider().refreshItem(o);
            } else {
                productGrid.setVisible(false);
                hint.setVisible(true);
            }
            if (o.equals(productInEditing)) {
                productInEditing = null;
                productService.add(o);
            } else productService.update(o);
        });

        imageLayout = new Layout(getImage(product.get()));
        imageLayout.setWidth("70%");
        imageLayout.addClassName(LumoUtility.AlignSelf.CENTER);
        imageLayout.addClickListener(event -> {
            if (product.get() != null) attachUploadDialog(imageLayout, product.get());
        });

        Layout form = new Layout(imageLayout, nameField, priceField, massField, descriptionField, productOptionMultiSelectComboBox, productComponentsLayout, updateProductButton);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);
        form.setWidth(18, Unit.REM);
        productGrid.addItemDoubleClickListener(event -> openSidebar());
        productGrid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent()) product.set(event.getFirstSelectedItem().get());
            if (product.get().getProductOptions() != null || !product.get().getProductOptions().isEmpty()) productOptionMultiSelectComboBox.setValue(product.get().getProductOptions());
            else productOptionMultiSelectComboBox.select(new HashSet<>());
            massField.setValue(product.get().getMass());
            priceField.setValue(product.get().getPrice());
            nameField.setValue(product.get().getName());
            descriptionField.setValue(product.get().getDescription());
            imageLayout.removeAll();
            imageLayout.add(getImage(product.get()));
            productContents.clear();
            productContents.addAll(productContentsRepo.findAllByProduct(product.get()));
            createContentList(productContents);
        });
        this.sidebar = new Section(header, form);
        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0",
                "transition-all", "z-10");
        this.sidebar.setWidth(25, Unit.REM);
        return this.sidebar;
    }

    private Image getImage(Product product) {
        Image image;
        if (product != null && product.getPreviewUrl() == null) {
            image = new Image("line-awesome/svg/" + LineAwesomeIcon.FILE_IMAGE.getSvgName() + ".svg", "");
            image.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignContent.CENTER);
        } else if (product != null) {
            image = new Image(ImageResourceUtils.getImageResource(product.getPreviewUrl()), product.getName());
        } else {
            image = new Image("line-awesome/svg/" + LineAwesomeIcon.FILE_IMAGE.getSvgName() + ".svg", "");
            image.addClassNames(LumoUtility.JustifyContent.CENTER, LumoUtility.AlignContent.CENTER);
        }
        image.getStyle().set("min-width", "-webkit-fill-available");
        return image;
    }

    private void attachUploadDialog(Layout imageLayout, Product product) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Загрузка презентационного изображения");
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

            product.setPreviewUrl("src/main/resources/META-INF/resources/images/" + fileName);
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

    private void fillProductsComponentLayout(){
        TextField name = new TextField();
        name.setLabel("Название состава");
        name.setPlaceholder("Элемент состава");

        IntegerField mass = new IntegerField();
        mass.setLabel("Масса элемента");
        mass.setPlaceholder("Масса элемента");

        Button save = new Button("Добавить");
        save.addClickListener(event -> {
            ProductContent productContent = new ProductContent();
            productContent.setMass(mass.getValue());
            productContent.setName(name.getValue());
            productContents.add(productContent);
            createContentList(productContents);
        });

        Layout inputLayout = new Layout(name, mass, save);
        inputLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        inputLayout.setGap(Layout.Gap.SMALL);

        productComponentsLayout.add(inputLayout);
        productComponentsLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        productComponentsLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        componentsLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        componentsLayout.setGap(Layout.Gap.SMALL);
    }

    private void createContentList(List<ProductContent> productContents){
        UI.getCurrent().access(() -> productComponentsLayout.remove(componentsLayout));
        componentsLayout.removeAll();
        productContents.forEach(productContent -> {
            Span nameSpan = new Span(productContent.getName());
            Span massSpan = new Span(STR."\{String.valueOf(productContent.getMass())} г");
            massSpan.getStyle().set("box-sizing", "content-box").set("position", "absolute").set("margin-left", "10rem");
            SvgIcon icon = LineAwesomeIcon.TRASH_ALT.create();
            icon.getStyle().set("box-sizing", "content-box").set("position", "absolute").set("margin-left", "16rem");
            Layout layout = new Layout(nameSpan, massSpan, icon);
            layout.setFlexDirection(Layout.FlexDirection.ROW);
            layout.setJustifyContent(Layout.JustifyContent.BETWEEN);
            icon.addClickListener(event -> {
                UI.getCurrent().access(() -> componentsLayout.remove(layout));
                productContents.remove(productContent);
                if (productContent.getId()!=null)
                    productContentsRepo.delete(productContent);
            });
            componentsLayout.add(layout);
        });
        UI.getCurrent().access(() -> productComponentsLayout.add(componentsLayout));
    }

}

