package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Badge;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.components.LazyContainer;
import ru.tusur.ShaurmaWebSiteProject.ui.components.PriceRange;
import ru.tusur.ShaurmaWebSiteProject.ui.dialogs.NativeDialog;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MyComponentList;
import ru.tusur.ShaurmaWebSiteProject.ui.list.ProductListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.LazyPlaceholder;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.RadioButtonTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.BadgeVariant;

import java.util.List;
import java.util.Random;

@AnonymousAllowed
@Route(value = "Главная", layout = MainLayout.class)
@PageTitle("PitaMaster")
public class MainProductView extends Main implements LazyPlaceholder {
    private final SecurityService securityService;
    private final ProductService productService;
    private final ProductTypeEntityRepo productTypeEntityRepo;
    private final Random random = new Random();
    private com.vaadin.flow.component.html.Section sidebar;

    public MainProductView(ProductService productService,
                           ProductTypeEntityRepo productTypeEntityRepo,
                           SecurityService securityService) {
        this.securityService = securityService;
        this.productService = productService;
        this.productTypeEntityRepo = productTypeEntityRepo;

        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, LumoUtility.Overflow.HIDDEN);
        add(createSidebar(), createContent());
        closeSidebar();
    }

    private com.vaadin.flow.component.html.Section createSidebar() {
        H2 title = new H2("Фильтры");
        title.addClassNames(LumoUtility.FontSize.MEDIUM);

        Button close = new Button(LineAwesomeIcon.TIMES_SOLID.create(), e -> closeSidebar());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.setAriaLabel("Закрыть");
        close.setTooltipText("Закрыть");

        Layout header = new Layout(title, close);
        header.addClassNames(LumoUtility.Padding.End.MEDIUM, LumoUtility.Padding.Start.LARGE, LumoUtility.Padding.Vertical.SMALL);
        header.setAlignItems(Layout.AlignItems.CENTER);
        header.setJustifyContent(Layout.JustifyContent.BETWEEN);

        CheckboxGroup<String> brands = new CheckboxGroup<>("Филиалы");
        brands.setItems("LuxeLiving", "DecoHaven", "CasaCharm", "HomelyCraft", "ArtisanHaus"); //todo populate with branches
        brands.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        setRenderer(brands);

        PriceRange priceRange = new PriceRange("Price");

        CheckboxGroup<String> rating = new CheckboxGroup<>("Rating");
        rating.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        rating.setItems("1 star", "2 stars", "3 stars", "4 stars", "5 stars");
        rating.setRenderer(new ComponentRenderer<>(item -> {
            String count = Integer.toString(this.random.nextInt(100));

            Badge badge = new Badge(count);
            badge.addThemeVariants(BadgeVariant.CONTRAST, BadgeVariant.SMALL, BadgeVariant.PILL);

            int stars = Integer.parseInt(item.split(" ")[0]);

            Span span = new Span(getStars(stars), badge);
            span.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL);
            span.getElement().setAttribute("aria-hidden", "true");

            Span screenReader = new Span(item + ", " + count + " items");
            screenReader.addClassNames(LumoUtility.Accessibility.SCREEN_READER_ONLY);

            return new Span(span, screenReader);
        }));

        CheckboxGroup<String> availability = new CheckboxGroup<>("Availability");
        availability.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        availability.setItems("В наличии", "Не в наличии");
        setRenderer(availability);

        Layout form = new Layout(brands, priceRange, rating, availability);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);

        this.sidebar = new com.vaadin.flow.component.html.Section(header, form);
        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0",
                "transition-all", "z-10");
        this.sidebar.setWidth(20, Unit.REM);
        return this.sidebar;
    }

    private void setRenderer(CheckboxGroup<String> checkboxGroup) {
        checkboxGroup.setRenderer(new ComponentRenderer<>(item -> {
            Badge badge = new Badge(Integer.toString(this.random.nextInt(100)));
            badge.addThemeVariants(BadgeVariant.CONTRAST, BadgeVariant.SMALL, BadgeVariant.PILL);

            Span span = new Span(new Text(item), badge);
            span.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL);
            return span;
        }));
    }

    private Text getStars(int stars) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < stars) {
                builder.append("★");
            } else {
                builder.append("☆");
            }
        }
        return new Text(builder.toString());
    }

    public Component createToolbar() {
        TextField search = new TextField();
        search.addClassNames(LumoUtility.Flex.GROW, LumoUtility.MinWidth.NONE);
        search.setAriaLabel("Поиск");
        search.setPlaceholder("Поиск...");
        search.setPrefixComponent(LumoIcon.SEARCH.create());

        MultiSelectComboBox<Object> brands = new MultiSelectComboBox<>();
        brands.addClassNames(LumoUtility.Display.HIDDEN, "lg:inline-flex", LumoUtility.MinWidth.NONE);
        brands.setAriaLabel("Brands");
        brands.setItems(new String[]{"LuxeLiving"}, new String[]{"DecoHaven"}, new String[]{"CasaCharm"}, new String[]{"HomelyCraft"}, new String[]{"ArtisanHaus"});
        brands.setPlaceholder("Brands");

        Button price = new Button("Price");
        price.addClassNames(LumoUtility.Display.HIDDEN, "lg:inline-block");
        price.setIcon(new Icon("lumo", "angle-down"));
        price.setIconAfterText(true);

        PriceRange priceRange = new PriceRange("Price");
        priceRange.addClassNames(LumoUtility.Margin.SMALL, LumoUtility.Padding.Top.XSMALL);
        priceRange.setWidth(16, Unit.REM);

        NativeDialog priceDialog = new NativeDialog(priceRange);
        priceDialog.setRight(8, Unit.REM);
        priceDialog.setTop(7.5f, Unit.REM);
        price.addClickListener(e -> priceDialog.showModal());

        // TODO: a11y improvements, opened/closed states
        Button filters = new Button("Filters", LineAwesomeIcon.SLIDERS_H_SOLID.create());
        filters.addClickListener(e -> toggleSidebar());

        RadioButtonGroup<String> mode = new RadioButtonGroup<>();
        mode.setAriaLabel("View mode");
        mode.setItems("Grid", "List");
        mode.setRenderer(new ComponentRenderer<>(this::renderIconWithAriaLabel));
        mode.setValue("Grid");
        setRadioButtonGroupTheme(mode, RadioButtonTheme.TOGGLE);

        Layout toolbar = new Layout(search, brands, price, priceDialog, filters, mode);
        toolbar.addClassNames(LumoUtility.Border.BOTTOM, LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.SMALL);
        toolbar.setAlignItems(Layout.AlignItems.CENTER);
        toolbar.setGap(Layout.Gap.MEDIUM);
        return toolbar;
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
        // Desktop
        this.sidebar.getStyle().remove("margin-inline-start");
        // Mobile
        this.sidebar.addClassNames("start-0");
        this.sidebar.removeClassName("-start-full");
    }

    private void closeSidebar() {
        this.sidebar.setEnabled(false);
        this.sidebar.removeClassName(LumoUtility.Border.RIGHT);
        // Desktop
        this.sidebar.getStyle().set("margin-inline-start", "-20rem");
        // Mobile
        this.sidebar.addClassNames("-start-full");
        this.sidebar.removeClassName("start-0");
    }

    private Component renderIconWithAriaLabel(String item) {
        Component icon = item.equals("Grid") ?
                LineAwesomeIcon.TH_SOLID.create() :
                LineAwesomeIcon.LIST_SOLID.create();
        icon.getElement().setAttribute("aria-label", item);
        return icon;
    }

    private Button createIconButton(LineAwesomeIcon icon, String label) {
        Button button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        button.setAriaLabel(label);
        button.setTooltipText(label);
        return button;
    }

    private void setRadioButtonGroupTheme(RadioButtonGroup group, String... themeNames) {
        group.addThemeNames(themeNames);
        group.getChildren().forEach(component -> {
            for (String themeName : themeNames) {
                component.getElement().getThemeList().add(themeName);
            }
        });
    }

    private Layout createContent() {
        List<ProductTypeEntity> allProductTypeEntities = productTypeEntityRepo.findAll();
        MyComponentList list = new MyComponentList();
        Layout content = new Layout(createToolbar());
        content.addClassNames(LumoUtility.Flex.GROW);
        content.setFlexDirection(Layout.FlexDirection.COLUMN);
        content.setOverflow(Layout.Overflow.HIDDEN);
        list.setAutoFill(220, Unit.PIXELS);
        list.setOverflow(Layout.Overflow.AUTO);

        allProductTypeEntities.forEach(productType -> {
            H3 title = new H3(productType.getName());
            List<Product> products = productService.findByProductTypeOrderByRankAsc(productType);
            list.add(title);
            for (Product product : products) {
                list.add(
                        new LazyContainer<>(
                                lazyPlaceholder(),
                                (div, ui) -> {
                                    ui.access(() -> {
                                        div.removeAll();
                                        ProductListItem productListItem = new ProductListItem(
                                                product.getPreviewUrl(),
                                                product.getName(),
                                                product.getName(),
                                                product.getPrice().toString(),
                                                createIconButton(LineAwesomeIcon.HEART, "Избранное")
                                        );
                                        div.add(productListItem);
                                    });
                                })
                );
            }
        });
        content.add(list);
        return content;
    }
}