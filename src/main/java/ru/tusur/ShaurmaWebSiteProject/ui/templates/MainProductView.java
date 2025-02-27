package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ReviewRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Badge;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.components.LazyContainer;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MyComponentList;
import ru.tusur.ShaurmaWebSiteProject.ui.list.ProductListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.LazyPlaceholder;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.BadgeVariant;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.StarsUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@AnonymousAllowed
@Route(value = "Главная", layout = MainLayout.class)
@PageTitle("PitaMaster")
public class MainProductView extends Main implements LazyPlaceholder {
    private final BranchProductRepo branchProductRepo;
    private final ReviewRepo reviewRepo;
    private final Random random = new Random();
    private final List<Product> products = new ArrayList<>();
    private final List<Branch> branches = new ArrayList<>();
    private final Set<ProductTypeEntity> productTypeEntities = new HashSet<>();
    private Branch currentBranch;
    private final Span branchPhone = new Span("-");
    private final Span branchOpenTime = new Span("-");
    private final MyComponentList componentList = new MyComponentList();

//    private com.vaadin.flow.component.html.Section sidebar;

    public MainProductView(BranchProductRepo branchProductRepo,
                           BranchRepo branchRepo,
                           ReviewRepo reviewRepo) {
        this.branchProductRepo = branchProductRepo;
        this.reviewRepo = reviewRepo;
        branches.addAll(branchRepo.findAllByHide(false));
        currentBranch = branches.getFirst();
        products.addAll(branchProductRepo.findAllProductByBranchAndHide(currentBranch, false).stream().map(BranchProduct::getProduct).toList());
        products.stream().map(Product::getProductType).forEach(productTypeEntities::add);
        createProductComponents(productTypeEntities);
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, LumoUtility.Overflow.HIDDEN);
        add(createContent());
//        add(createSidebar(), createContent());
//        closeSidebar();
    }

//    private com.vaadin.flow.component.html.Section createSidebar() {
//        H2 title = new H2("Фильтры");
//        title.addClassNames(LumoUtility.FontSize.MEDIUM);
//
//        Button close = new Button(LineAwesomeIcon.TIMES_SOLID.create(), e -> closeSidebar());
//        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        close.setAriaLabel("Закрыть");
//        close.setTooltipText("Закрыть");
//
//        Layout header = new Layout(title, close);
//        header.addClassNames(LumoUtility.Padding.End.MEDIUM, LumoUtility.Padding.Start.LARGE, LumoUtility.Padding.Vertical.SMALL);
//        header.setAlignItems(Layout.AlignItems.CENTER);
//        header.setJustifyContent(Layout.JustifyContent.BETWEEN);
//
//        PriceRange priceRange = new PriceRange("Price");
//
//        CheckboxGroup<String> rating = new CheckboxGroup<>("Rating");
//        rating.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
//        rating.setItems("1 star", "2 stars", "3 stars", "4 stars", "5 stars");
//        rating.setRenderer(new ComponentRenderer<>(item -> {
//            String count = Integer.toString(this.random.nextInt(100));
//
//            Badge badge = new Badge(count);
//            badge.addThemeVariants(BadgeVariant.CONTRAST, BadgeVariant.SMALL, BadgeVariant.PILL);
//
//            int stars = Integer.parseInt(item.split(" ")[0]);
//
//            Span span = new Span(StarsUtils.getStars(stars), badge);
//            span.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL);
//            span.getElement().setAttribute("aria-hidden", "true");
//
//            Span screenReader = new Span(item + ", " + count + " items");
//            screenReader.addClassNames(LumoUtility.Accessibility.SCREEN_READER_ONLY);
//
//            return new Span(span, screenReader);
//        }));
//
//        CheckboxGroup<String> availability = new CheckboxGroup<>("Availability");
//        availability.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
//        availability.setItems("В наличии", "Не в наличии");
//        setRenderer(availability);
//
//        Layout form = new Layout(branchSelectorComboBox, priceRange, rating, availability);
//        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
//        form.setFlexDirection(Layout.FlexDirection.COLUMN);
//
//        this.sidebar = new com.vaadin.flow.component.html.Section(header, form);
//        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
//                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0",
//                "transition-all", "z-10");
//        this.sidebar.setWidth(20, Unit.REM);
//        return this.sidebar;
//    }
//    private void setRenderer(CheckboxGroup<String> checkboxGroup) {
//        checkboxGroup.setRenderer(new ComponentRenderer<>(item -> {
//            Badge badge = new Badge(Integer.toString(this.random.nextInt(100)));
//            badge.addThemeVariants(BadgeVariant.CONTRAST, BadgeVariant.SMALL, BadgeVariant.PILL);
//
//            Span span = new Span(new Text(item), badge);
//            span.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL);
//            return span;
//        }));
//    }

    public Component createHat() {

        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        branchPhone.getStyle().setPaddingLeft("4px");
        branchPhone.getStyle().setPaddingRight("8px");
        branchPhone.setText(currentBranch.getPhoneNumber());
        branchOpenTime.getStyle().setPaddingLeft("4px");
        branchOpenTime.getStyle().setPaddingRight("8px");
        branchOpenTime.setText(STR."Открыто с \{sdf.format(currentBranch.getOpenFrom())} до \{sdf.format(currentBranch.getOpenTill())}");

        Div divBranchPhone = new Div(LineAwesomeIcon.PHONE_SOLID.create(), branchPhone);
        divBranchPhone.getStyle().set("width", "max-content");
        Div divBranchTime = new Div(LineAwesomeIcon.CLOCK.create(), branchOpenTime);
        divBranchTime.getStyle().set("width", "max-content");

        Layout branchDataLayout = new Layout(divBranchPhone, divBranchTime);
        branchDataLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        branchDataLayout.setColumnGap(Layout.Gap.SMALL);
        branchDataLayout.setRowGap(Layout.Gap.MEDIUM);
        branchDataLayout.addClassName(LumoUtility.Display.INLINE_BLOCK);
        branchDataLayout.getStyle().setPaddingTop("24px").setPaddingBottom("4px").setPaddingLeft("24px");
        branchDataLayout.setColumns(Layout.GridColumns.COLUMNS_2);

        ComboBox<Branch> branchSelectorComboBox = new ComboBox<>("Филиал");
        ComboBox.ItemFilter<Branch> filter = (branch, filterString) -> (branch.getAddress()).toLowerCase().contains(filterString.toLowerCase());
        branchSelectorComboBox.addClassNames(LumoUtility.Display.HIDDEN, "lg:inline-flex", LumoUtility.MinWidth.NONE);
        branchSelectorComboBox.setAriaLabel("Филиалы");
        branchSelectorComboBox.setWidth("fit-content");
        branchSelectorComboBox.setItems(filter, branches);
        branchSelectorComboBox.setItemLabelGenerator(Branch::getAddress);
        branchSelectorComboBox.setValue(currentBranch);
        branchSelectorComboBox.setPlaceholder("Филиалы");
        branchSelectorComboBox.addValueChangeListener(event -> {
            currentBranch = event.getValue();
            products.clear();
            products.addAll(branchProductRepo.findAllProductByBranch(currentBranch).stream().map(BranchProduct::getProduct).toList());
            products.stream().map(Product::getProductType).forEach(productTypeEntities::add);
            branchOpenTime.setText(STR."Открыто с \{sdf.format(currentBranch.getOpenFrom())} до \{sdf.format(currentBranch.getOpenTill())}");
            branchPhone.setText(currentBranch.getPhoneNumber());
            createProductComponents(productTypeEntities);
        });

//         TODO: a11y improvements, opened/closed states
//        Button filters = new Button("Filters", LineAwesomeIcon.SLIDERS_H_SOLID.create());
//        filters.addClickListener(e -> toggleSidebar());

//        Layout toolbar = new Layout(branchSelectorComboBox, filters);
        Layout toolbar = new Layout(branchSelectorComboBox, branchDataLayout);
        toolbar.addClassNames(LumoUtility.Border.BOTTOM, LumoUtility.Padding.Vertical.SMALL);
        toolbar.setAlignItems(Layout.AlignItems.CENTER);
        toolbar.getStyle().setMarginLeft("8px");
        toolbar.setGap(Layout.Gap.MEDIUM);
        return toolbar;
    }

    /// this is methods for sidebar, not used here currently
//
//    private void toggleSidebar() {
//        if (this.sidebar.isEnabled()) {
//            closeSidebar();
//        } else {
//            openSidebar();
//        }
//    }
//
//    private void openSidebar() {
//        this.sidebar.setEnabled(true);
//        this.sidebar.addClassNames(LumoUtility.Border.RIGHT);
//        this.sidebar.getStyle().remove("margin-inline-start");
//        this.sidebar.addClassNames("start-0");
//        this.sidebar.removeClassName("-start-full");
//    }
//
//    private void closeSidebar() {
//        this.sidebar.setEnabled(false);
//        this.sidebar.removeClassName(LumoUtility.Border.RIGHT);
//        this.sidebar.getStyle().set("margin-inline-start", "-20rem");
//        this.sidebar.addClassNames("-start-full");
//        this.sidebar.removeClassName("start-0");
//    }

//    private Button createIconButton(LineAwesomeIcon icon, String label) {
//        Button button = new Button(icon.create());
//        button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        button.setAriaLabel(label);
//        button.setTooltipText(label);
//        return button;
//    }

    private Layout createContent() {
        componentList.setAutoFill(220, Unit.PIXELS);
        componentList.setOverflow(Layout.Overflow.AUTO);

        Layout content = new Layout(createHat(), componentList);
        content.addClassNames(LumoUtility.Flex.GROW);
        content.setFlexDirection(Layout.FlexDirection.COLUMN);
        content.setOverflow(Layout.Overflow.HIDDEN);

        return content;
    }

    private void createProductComponents(Set<ProductTypeEntity> allProductTypeEntities) {
        componentList.removeAll();
        allProductTypeEntities.forEach(productType -> {
            List<Product> productsOfProductType = products.stream().filter(product -> product.getProductType().equals(productType)).toList();
            H3 title = new H3(productType.getName());
            componentList.add(title);
            for (Product product : productsOfProductType) {

                DecimalFormat df = new DecimalFormat("#.##");
                double ratingValue = reviewRepo.findAllByProductAndBranch(product, currentBranch).stream().mapToInt(Review::getGrade).sum() / (double) product.getReviews().size();
                Span span = new Span();
                span.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL);
                span.getElement().setAttribute("aria-hidden", "true");

                if (Double.isNaN(ratingValue)) ratingValue = 0d;
                Badge badge = new Badge();
                badge.addThemeVariants(BadgeVariant.CONTRAST, BadgeVariant.SMALL, BadgeVariant.PILL);
                badge.setText(df.format(ratingValue));
                span.add(StarsUtils.getStars(ratingValue), badge);

                componentList.add(
                        new LazyContainer<>(
                                lazyPlaceholder(),
                                (div, ui) -> {
                                    ui.access(() -> {
                                        div.removeAll();
                                        ProductListItem productListItem = new ProductListItem(
                                                product.getPreviewUrl(),
                                                product.getName(),
                                                product.getName(),
                                                product.getPrice().toString() + " ₽ " + product.getMass().toString() + " г",
                                                span
                                        );
                                        productListItem.addClickListener(event -> UI.getCurrent().navigate(ProductDetailsView.class, product.getName() + "&" + currentBranch.getAddress()));
                                        div.add(productListItem);
                                    });
                                })
                );
            }
        });
    }
}