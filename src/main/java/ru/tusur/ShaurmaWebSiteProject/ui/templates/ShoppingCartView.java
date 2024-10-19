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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
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
@PageTitle("Shopping cart")
@Route(value = "shopping-cart", layout = MainLayout.class)
public class ShoppingCartView extends Main {

    public ShoppingCartView() {
        addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, FlexDirection.Breakpoint.Medium.ROW,
                Margin.Horizontal.AUTO, MaxWidth.SCREEN_LARGE);
        add(createShoppingCart(), createSummary());
    }

    private Component createShoppingCart() {
        H2 title = new H2("Items (3)");
        title.addClassNames(FontSize.XLARGE, Margin.Top.XLARGE);

        MyComponentList list = new MyComponentList(
                new ShoppingCartListItem(
                        "https://images.unsplash.com/photo-1610136649349-0f646f318053?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=320&q=80",
                        "Black framed sunglasses on white table",
                        "Sunglasses 001",
                        "Introducing the ZephyrBlaze HyperShade sunglasses, a cosmic fusion of neon splashes and techno-textured frames. These avant-garde shades defy gravity, turning sunbeams into pixelated rainbows.",
                        "550,00 €"
                )
        );
        list.addClassNames("divide-y");

        Section section = new Section(title, list);
        section.addClassNames(BoxSizing.BORDER, Padding.Horizontal.LARGE);
        return section;
    }

    private Component createSummary() {
        H2 title = new H2("Order summary");
        title.addClassNames(FontSize.XLARGE);

        KeyValuePairs pairs = new KeyValuePairs(
                new KeyValuePair("Subtotal", "550,00 €"),
                new KeyValuePair("Delivery", "0,00 €"),
                new KeyValuePair("Total", "550,00 €")
        );
        pairs.addClassNames("divide-y");
        pairs.setKeyWidthFull();
        pairs.removeBackgroundColor();
        pairs.removeHorizontalPadding();

        TextField code = new TextField("Enter a promo code");
        code.addClassNames(Flex.GROW);
        code.addThemeName(InputTheme.OUTLINE);

        Button apply = new Button("Apply");
        apply.addClassNames(Background.BASE);
        apply.addThemeName(ButtonTheme.OUTLINE);

        InputGroup inputGroup = new InputGroup(code, apply);

        RouterLink checkout = new RouterLink("Checkout", CheckoutView.class);
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