package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.io.FilenameUtils;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductOptionRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ReviewRepo;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.LazyPlaceholder;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@Route(value = "product", layout = MainLayout.class)
@AnonymousAllowed
public class InDetailsProductRepresentation extends Div implements HasComponents, LazyPlaceholder, HasDynamicTitle, HasUrlParameter<String> {
    private Product product = null;
    private final ProductRepo productRepo;
    private final ProductOptionRepo productOptionRepo;
    private final ReviewRepo reviewRepo;
    private final Div image = new Div();
    private final H1 name = new H1();
    private final Span mass = new Span();
    private final Span price = new Span();
    private final Button addButton = new Button("Добавить", event -> {
    }); //todo add event processing
    private final HorizontalLayout priceAddButton = new HorizontalLayout(price, addButton);
    private final HorizontalLayout starRating = new HorizontalLayout();
    private final HorizontalLayout nameMass = new HorizontalLayout(name, mass);
    private final VerticalLayout progressBarReviewSection = new VerticalLayout();
    private final VerticalLayout gradesReviewSection = new VerticalLayout();
    private final VerticalLayout contentReviewSection = new VerticalLayout();
    private final HorizontalLayout reviewSection = new HorizontalLayout(gradesReviewSection, contentReviewSection);
    private final HorizontalLayout reviewSectionStarRating = new HorizontalLayout();
    private final VerticalLayout nameMassAndStarRating = new VerticalLayout(nameMass, starRating);
    private final VerticalLayout mainDataLayout = new VerticalLayout(nameMassAndStarRating, priceAddButton);
    private String title = "";

    public InDetailsProductRepresentation(ProductRepo productRepo,
                                          ProductOptionRepo productOptionRepo,
                                          ReviewRepo reviewRepo) {
        this.productRepo = productRepo;
        this.productOptionRepo = productOptionRepo;
        this.reviewRepo = reviewRepo;
    }

    private void initComponent(ProductOptionRepo productOptionRepo, ReviewRepo reviewRepo) {
        this.addClassNames(LumoUtility.AlignSelf.CENTER, LumoUtility.Width.AUTO);
        VerticalLayout verticalLayout = new VerticalLayout(new HorizontalLayout(image, mainDataLayout), new Hr(), reviewSection);
        this.getStyle()
                .setPaddingTop("24px")
                .setDisplay(Style.Display.FLEX)
                .setAlignItems(Style.AlignItems.CENTER)
                .setAlignSelf(Style.AlignSelf.CENTER)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setWidth("auto");
        verticalLayout.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setAlignItems(Style.AlignItems.CENTER)
                .setAlignSelf(Style.AlignSelf.CENTER)
                .setWidth("auto");
        add(verticalLayout);
        getStyle().setDisplay(Style.Display.FLEX);
        priceAddButton.setPadding(true);
        image.getStyle().setDisplay(Style.Display.FLEX)
                .setPaddingTop("32px");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        mainDataLayout.addClassName(LumoUtility.Flex.AUTO);

        List<Review> reviews = reviewRepo.findAllByProduct(product);
        float rate = reviews.stream().mapToInt(Review::getGrade).sum() / (float) reviews.size();
        int intRate = (int) rate;
        for (int i = 0; i < 5; i++) {
            Integer grade = 5 - i;
            float part = reviews.stream().filter(review -> review.getGrade().equals(grade)).count() / (float) reviews.size();
            ProgressBar progressBar = new ProgressBar();
            progressBar.setValue(1);
            progressBarReviewSection.add(
                    new HorizontalLayout(
                            new Span(String.valueOf(grade)),
                            progressBar
                    )
            );
            intRate -= 1;
            if (intRate > 0) {
                starRating.add(new Icon(VaadinIcon.STAR));
                reviewSectionStarRating.add(new Icon(VaadinIcon.STAR));
            } else if (intRate == 0) {
                starRating.add(new Icon(VaadinIcon.STAR_HALF_LEFT_O));
                reviewSectionStarRating.add(new Icon(VaadinIcon.STAR_HALF_LEFT_O));
            } else {
                starRating.add(new Icon(VaadinIcon.STAR_O));
                reviewSectionStarRating.add(new Icon(VaadinIcon.STAR_O));
            }
        }
        starRating.add(new Span(String.valueOf(reviews.size())));
        contentReviewSection.add(new H1("Отзывов нет ¯\\_(ツ)_/¯"));
        contentReviewSection.setWidth("100%");
        gradesReviewSection.add(new HorizontalLayout(
                        new H1(String.valueOf(rate)),
                        new VerticalLayout(
                                reviewSectionStarRating,
                                new Span(reviews.size() + " отзывов")
                        )
                ),
                progressBarReviewSection,
                new VerticalLayout(
                        new H3("Есть что рассказать?"),
                        new Span("Оцените товар, ваш опыт будет полезен"),
                        new Button("Добавить отзыв", event -> {
                        }) //todo add event processing
                )
        );

        List<ProductOption> productOptions = productOptionRepo.findAllProductOptionByProductSet(product);
        if (!productOptions.isEmpty()) {
            mainDataLayout.add(new Hr());
            CheckboxGroup<String> options = new CheckboxGroup<>("Дополнительно");
            options.setItems(String.valueOf(productOptions.stream().map(productOption ->
                    Optional.ofNullable(productOption.getName()).orElse("Н/д") + " (" +
                            Optional.ofNullable(productOption.getMass()).map(integer -> integer + " г").orElse("Н/д") + ") " +
                            Optional.ofNullable(productOption.getPrice()).map(bigDecimal -> bigDecimal + " ₽").orElse("Н/д"))));
            options.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
            mainDataLayout.add(options);
        }

        name.setText(Optional.ofNullable(product.getName()).orElse("Н/д"));
        mass.setText(Optional.ofNullable(product.getMass()).map(integer -> integer + " г").orElse("Н/д"));
        mass.getStyle().setFontSize("24px");
        price.setText(Optional.ofNullable(product.getPrice()).map(bigDecimal -> bigDecimal + " ₽").orElse("Н/д"));
        price.getStyle().setFontSize("32px")
                .setFontWeight(Style.FontWeight.BOLDER);
        price.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.FontWeight.EXTRABOLD);
        image.add(
                new LazyContainer<>(lazyPlaceholder(), (div, ui) -> {
                    ui.access(() -> {
                        div.removeAll();
                        Image image = new Image(new StreamResource(FilenameUtils.getName(product.getPreviewUrl()), (InputStreamFactory) () -> {
                            try {
                                return new DataInputStream(new FileInputStream(product.getPreviewUrl()));
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }), product.getName());
                        div.add(image);
                    });
                })
        );
    }


    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        title = parameter;
        this.product = productRepo.findByName(parameter).orElseThrow(() -> new NotFoundException("product not found by name " + title));
        initComponent(productOptionRepo, reviewRepo);
    }
}
