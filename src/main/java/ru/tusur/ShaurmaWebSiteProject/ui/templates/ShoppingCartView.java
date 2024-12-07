package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import lombok.Getter;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ShopCartService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.KeyValuePair;
import ru.tusur.ShaurmaWebSiteProject.ui.components.KeyValuePairs;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MyComponentList;
import ru.tusur.ShaurmaWebSiteProject.ui.list.ShoppingCartListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.math.BigDecimal;
import java.util.Set;

@AnonymousAllowed
@PageTitle("Карзина")
@Route(value = "Карзина", layout = MainLayout.class)
public class ShoppingCartView extends Main {

    private final ShopCartService shopCartService;
    private Registration registration;

    private BigDecimal productsSumPrice = BigDecimal.ZERO;
    private BigDecimal orderSumPrice = BigDecimal.ZERO;
    private int mass;

    private final KeyValuePair productsSumPriceBucket = new KeyValuePair("Сумма товаров", "-");
    private final KeyValuePair massBucket = new KeyValuePair("Масса", "-");
    private final MyComponentList list = new MyComponentList();
    private final Layout info = new Layout();

    public ShoppingCartView(ShopCartService shopCartService) {
        this.shopCartService = shopCartService;
        addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, FlexDirection.Breakpoint.Medium.ROW,
                Margin.Horizontal.AUTO, MaxWidth.SCREEN_LARGE);
        add(createShoppingCart(), createSummary());
    }

    private Component createShoppingCart() {
        this.addClassName(JustifyContent.BETWEEN);

        H2 title = new H2("Товары");
        title.addClassNames(FontSize.XLARGE, Margin.Top.XLARGE);

        list.addClassNames("divide-y");
        OrderedHashSet<OrderContent> allOrderContent = shopCartService.getAllOrderContent(VaadinService.getCurrentRequest().getWrappedSession().getId());
        if (allOrderContent.isEmpty()) {
            list.add(createCartItemPlaceholder());
            info.setVisible(false);
        } else {
            allOrderContent.forEach(orderContent -> {
                        Product product = orderContent.getProduct();
                        int num = orderContent.getNum();
                        Set<ProductOption> productOptions = product.getProductOptions();
                        BigDecimal bGNum = BigDecimal.valueOf(num);

                        mass += (product.getMass() + productOptions.stream().mapToInt(ProductOption::getMass).sum()) * num;
                        BigDecimal productOptionsSum = productOptions.stream().map(ProductOption::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

                        productsSumPrice = productsSumPrice.add(product.getPrice().add(productOptionsSum).multiply(bGNum));
                        list.add(new ShoppingCartListItem(orderContent, shopCartService));
                        info.setVisible(true);
                    }
            );
        }
        setSummaryData();

        Section section = new Section(title, list);
        section.addClassNames(BoxSizing.BORDER, Padding.Horizontal.LARGE);
        return section;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        registration =
                ComponentUtil.addListener(
                        attachEvent.getUI(),
                        ChangeSummaryDataEvent.class,
                        event -> {
                            changeSummaryData(event.getSummaryDataDeltas());
                            if (event.isRemoveSelf()) info.setVisible(false);
                            if (event.isRemoveSelf() && list.getChildren().findAny().isEmpty())
                                list.add(createCartItemPlaceholder());
                        }
                );
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        registration.remove();
    }

    @Getter
    public static class ChangeSummaryDataEvent extends ComponentEvent<ShoppingCartListItem> {
        private final SummaryDataDeltas summaryDataDeltas;
        private final boolean removeSelf;

        public ChangeSummaryDataEvent(ShoppingCartListItem source, boolean fromClient, SummaryDataDeltas summaryDataDeltas, boolean removeSelf) {
            super(source, fromClient);
            this.summaryDataDeltas = summaryDataDeltas;
            this.removeSelf = removeSelf;
        }
    }

    public record SummaryDataDeltas(int massDelta, BigDecimal priceDelta) {
    }

    private Component createSummary() {
        H2 title = new H2("Ваша корзина");
        title.addClassNames(FontSize.XLARGE);


        KeyValuePairs pairs = new KeyValuePairs(
                massBucket,
                productsSumPriceBucket
        );

        pairs.addClassNames("divide-y");
        pairs.setKeyWidthFull();
        pairs.removeBackgroundColor();
        pairs.removeHorizontalPadding();

        SvgIcon icon = LineAwesomeIcon.INFO_SOLID.create();
        Paragraph text = new Paragraph("Стоимость доставки будет рассчитана при оплате.");

        info.setFlexDirection(Layout.FlexDirection.ROW);
        info.setGap(Layout.Gap.MEDIUM);
        info.setAlignItems(Layout.AlignItems.CENTER);
        info.setJustifyContent(Layout.JustifyContent.CENTER);
        info.add(icon, text);

        RouterLink checkout = new RouterLink("Оплата", CheckoutView.class);
        checkout.addClassNames(AlignItems.CENTER, Background.PRIMARY, BorderRadius.MEDIUM, Display.FLEX,
                FontWeight.SEMIBOLD, Height.MEDIUM, JustifyContent.CENTER, TextColor.PRIMARY_CONTRAST);

        Layout layout = new Layout(title, pairs, info, checkout);
        layout.addClassNames(Background.CONTRAST_5, BorderRadius.LARGE, Padding.LARGE);
        layout.setBoxSizing(Layout.BoxSizing.BORDER);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setGap(Layout.Gap.MEDIUM);

        Section section = new Section(layout);
        section.addClassNames(BoxSizing.BORDER, Padding.LARGE);
        section.setMinWidth(24, Unit.REM);
        return section;
    }

    public void changeSummaryData(SummaryDataDeltas summaryDataDeltas) {
        mass += summaryDataDeltas.massDelta;
        productsSumPrice = productsSumPrice.add(summaryDataDeltas.priceDelta);
        setSummaryData();
    }

    private void setSummaryData() {
        UI.getCurrent().access(() -> {
            massBucket.setValue(mass + " г");
            productsSumPriceBucket.setValue(productsSumPrice + " ₽");
        });
    }

    private ListItem createCartItemPlaceholder() {
        ListItem listItem = new ListItem();
        listItem.addClassNames(AlignItems.CENTER, Display.FLEX, FlexDirection.COLUMN, FlexDirection.Breakpoint.Large.ROW, Gap.LARGE, Padding.Vertical.LARGE, Position.RELATIVE, JustifyContent.CENTER);

        SvgIcon icon = LineAwesomeIcon.CART_PLUS_SOLID.create();
        icon.addClickListener(_ -> UI.getCurrent().navigate(MainProductView.class));

        Span span = new Span("Корзина пуста...");

        listItem.add(icon, span);

        return listItem;
    }

}