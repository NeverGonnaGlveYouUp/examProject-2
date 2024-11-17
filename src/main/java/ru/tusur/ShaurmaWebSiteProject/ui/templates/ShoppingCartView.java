package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ShopCartService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.InputGroup;
import ru.tusur.ShaurmaWebSiteProject.ui.components.KeyValuePair;
import ru.tusur.ShaurmaWebSiteProject.ui.components.KeyValuePairs;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MyComponentList;
import ru.tusur.ShaurmaWebSiteProject.ui.list.ShoppingCartListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.ButtonTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.InputTheme;

@AnonymousAllowed
@PageTitle("Карзина")
@Route(value = "Карзина", layout = MainLayout.class)
public class ShoppingCartView extends Main {

    private final ShopCartService shopCartService;

    public ShoppingCartView(ShopCartService shopCartService) {
        this.shopCartService = shopCartService;
        addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, FlexDirection.Breakpoint.Medium.ROW,
                Margin.Horizontal.AUTO, MaxWidth.SCREEN_LARGE);
        add(createShoppingCart(), createSummary());
    }

    private Component createShoppingCart() {
        H2 title = new H2("Товары");
        title.addClassNames(FontSize.XLARGE, Margin.Top.XLARGE);

        MyComponentList list = new MyComponentList();
        list.addClassNames("divide-y");
        shopCartService
                .getAllOrderContent(VaadinService.getCurrentRequest().getWrappedSession().getId())
                .forEach(orderContent ->
                        list.add(
                                new ShoppingCartListItem(
                                        orderContent.getProduct().getPreviewUrl(),
                                        orderContent.getProduct().getName(),
                                        orderContent.getProduct().getName(),
                                        orderContent.getProduct().getDescription(),
                                        orderContent.getProduct().getPrice().toString()
                                )
                        )
                );


        Section section = new Section(title, list);
        section.addClassNames(BoxSizing.BORDER, Padding.Horizontal.LARGE);
        return section;
    }

    private Component createSummary() {
        H2 title = new H2("Ваша корзина");
        title.addClassNames(FontSize.XLARGE);


        KeyValuePair mass = new KeyValuePair("Масса", "140 г");
        KeyValuePair productSum = new KeyValuePair("Сумма товаров", "550,00 €");
        KeyValuePair delivery = new KeyValuePair("Доставка", "0,00 €");
        KeyValuePair sum = new KeyValuePair("Всего", "550,00 €");
        KeyValuePairs pairs = new KeyValuePairs(
                mass,
                productSum,
                delivery,
                sum
        );

        pairs.addClassNames("divide-y");
        pairs.setKeyWidthFull();
        pairs.removeBackgroundColor();
        pairs.removeHorizontalPadding();

        TextField code = new TextField("Введите промо код");
        code.addClassNames(Flex.GROW);
        code.addThemeName(InputTheme.OUTLINE);

        Button apply = new Button("Применить");
        apply.addClassNames(Background.BASE);
        apply.addThemeName(ButtonTheme.OUTLINE);

        InputGroup inputGroup = new InputGroup(code, apply);

        RouterLink checkout = new RouterLink("Оплата", CheckoutView.class);
        checkout.addClassNames(AlignItems.CENTER, Background.PRIMARY, BorderRadius.MEDIUM, Display.FLEX,
                FontWeight.SEMIBOLD, Height.MEDIUM, JustifyContent.CENTER, TextColor.PRIMARY_CONTRAST);

        Layout layout = new Layout(title, pairs, inputGroup, checkout);
        layout.addClassNames(Background.CONTRAST_5, BorderRadius.LARGE, Padding.LARGE);
        layout.setBoxSizing(Layout.BoxSizing.BORDER);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setGap(Layout.Gap.MEDIUM);

        Section section = new Section(layout);
        section.addClassNames(BoxSizing.BORDER, Padding.LARGE);
        section.setMinWidth(24, Unit.REM);
        return section;
    }

}