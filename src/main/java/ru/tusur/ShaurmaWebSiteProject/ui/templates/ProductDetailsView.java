package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.LikesRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Breadcrumb;
import ru.tusur.ShaurmaWebSiteProject.ui.components.BreadcrumbItem;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Checkboxes;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.list.ProductReviewListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.HomeView;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.CheckboxTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.RadioButtonTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.StarsUtils;

import java.text.DecimalFormat;
import java.util.Set;

import static ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils.getImageResource;

@AnonymousAllowed
@Route(value = "Подробно", layout = MainLayout.class)
public class ProductDetailsView extends Main implements HasUrlParameter<String>, HasDynamicTitle {
    private String productName;
    private final ProductRepo productRepo;
    private final LikesRepo likesRepo;
    private Product product;
    private final UserDetails userDetails;

    public ProductDetailsView(ProductRepo productRepo, SecurityService securityService, LikesRepo likesRepo) {
        this.productRepo = productRepo;
        this.likesRepo = likesRepo;
        this.userDetails = securityService.getAuthenticatedUser();
        addClassNames(Display.FLEX, FlexWrap.WRAP_REVERSE, JustifyContent.CENTER);
    }

    public Component createImage() {
        Layout images = new Layout(new Image(getImageResource(product.getPreviewUrl()), productName));
        images.addClassNames(Padding.LARGE);
        images.setAlignSelf(Layout.AlignSelf.END);
        images.setBoxSizing(Layout.BoxSizing.BORDER);
        images.setFlexDirection(Layout.FlexDirection.COLUMN);
        images.setGap(Layout.Gap.MEDIUM);
        images.setMaxWidth(24, Unit.REM);
        return images;
    }

    public Component createInformation() {
        RouterLink aDefault = new RouterLink(productName, ProductDetailsView.class, "default");
        aDefault.getElement().removeAttribute("href");
        Breadcrumb breadcrumb = new Breadcrumb(
                new BreadcrumbItem("Home", HomeView.class),
                new BreadcrumbItem("Главная", MainProductView.class),
                new BreadcrumbItem(aDefault)
        );
        breadcrumb.addClassNames(Margin.Bottom.XSMALL);

        DecimalFormat df = new DecimalFormat("#.##");
        double ratingValue = product.getReviews().stream().mapToInt(Review::getGrade).sum() / (double) product.getReviews().size();

        Span starsText = new Span( df.format(ratingValue) + " | Количество отзывов: " + product.getReviews().size());
        starsText.addClassNames(FontSize.SMALL, Margin.Start.XSMALL);

        Layout rating = new Layout(StarsUtils.getStars(ratingValue));
        rating.addClassNames(TextColor.PRIMARY);

        Button review = new Button("Оставить отзыв", e -> {
            UI.getCurrent().navigate("Подробно/" + getPageTitle() + "#Отзывы");
        });
        review.addClassNames(Margin.Vertical.NONE);
        review.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        if(userDetails==null){
            review.setText("Отзывы");
        }

        Layout reviewLayout = new Layout(rating, starsText, review);
        reviewLayout.addClassNames(Margin.Bottom.XSMALL, Margin.Top.MEDIUM);
        reviewLayout.setAlignItems(Layout.AlignItems.CENTER);
        reviewLayout.setGap(Layout.Gap.SMALL);

        H2 title = new H2(productName);
        title.addClassNames(FontSize.XLARGE, Margin.Bottom.XSMALL, Margin.Top.MEDIUM);

        Span price = new Span(product.getPrice() + " ₽");
        price.addClassNames(FontWeight.BOLD, Margin.Bottom.XSMALL, Margin.Top.NONE);

        Paragraph description = new Paragraph(product.getDescription());
        description.addClassNames(Margin.Bottom.LARGE, Margin.Top.MEDIUM);

        Set<ProductOption> productOptions = product.getProductOptions();
        String[] labels = productOptions.stream().map(ProductOption::getName).toArray(String[]::new);
        String[] descriptions = productOptions.stream().map(productOption -> productOption.getMass() + "г +" + productOption.getPrice() + " ₽").toArray(String[]::new);
        CheckboxGroup<String> options = new Checkboxes("Добавки", labels, descriptions, CheckboxTheme.DIVIDERS, CheckboxTheme.ALIGN_RIGHT);

        IntegerField quantity = new IntegerField("Количество");
        quantity.setStepButtonsVisible(true);
        quantity.setMin(1);
        quantity.setValue(1);

        Button add = new Button("В корзину");
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Layout quantityLayout = new Layout(quantity, add);
        quantityLayout.setAlignItems(Layout.AlignItems.BASELINE);
        quantityLayout.setColumns(Layout.GridColumns.COLUMNS_2);
        quantityLayout.setDisplay(Layout.Display.GRID);
        quantityLayout.setGap(Layout.Gap.SMALL);

        H3 reviewsTitle = new H3("Отзывы"); //TODO add anchor like #
        reviewsTitle.addClassNames(FontSize.SMALL, Margin.Top.SMALL);
        reviewsTitle.setId(reviewsTitle.getText().replace(" ", "-").toLowerCase());
        Layout reviews = new Layout();
        reviews.addClassNames(Border.BOTTOM, Margin.Bottom.MEDIUM, Margin.Top.LARGE);
        reviews.setFlexDirection(Layout.FlexDirection.COLUMN);
        reviews.add(reviewsTitle);
        product.getReviews().forEach(reviewObj -> reviews.add(new ProductReviewListItem(userDetails, reviewObj, likesRepo)));

        Layout layout = new Layout(breadcrumb, title, price, reviewLayout, description, quantityLayout, options, reviews);
        layout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_SMALL, Padding.LARGE);
        layout.setBoxSizing(Layout.BoxSizing.BORDER);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    private Component renderLabelWithDescription(String title, String desc) {
        Span primary = new Span(title);

        Span secondary = new Span(desc);
        secondary.addClassNames(FontSize.SMALL, TextColor.SECONDARY);

        Layout layout = new Layout(primary, secondary);
        layout.addClassNames(Padding.SMALL);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setGap(Layout.Gap.XSMALL);
        return layout;
    }

    private void setRadioButtonGroupTheme(RadioButtonGroup group, String... themeNames) {
        group.addThemeNames(themeNames);
        group.getChildren().forEach(component -> {
            for (String themeName : themeNames) {
                component.getElement().getThemeList().add(themeName);
            }
        });
    }

    private Details createDetails(String title, String description) {
        Span summary = new Span(title);
        summary.addClassNames(FontSize.SMALL);

        Span content = new Span(description);
        content.addClassNames(FontSize.SMALL, TextColor.SECONDARY);

        Details details = new Details(summary, content);
        details.addClassNames(Border.TOP, Margin.Vertical.NONE, Padding.Vertical.MEDIUM);
        details.addThemeVariants(DetailsVariant.REVERSE);
        return details;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.productName = parameter;
        this.product = productRepo.findByName(parameter).orElseThrow(NotFoundException::new);
        add(createInformation(), createImage());
    }

    @Override
    public String getPageTitle() {
        return this.productName;
    }
}