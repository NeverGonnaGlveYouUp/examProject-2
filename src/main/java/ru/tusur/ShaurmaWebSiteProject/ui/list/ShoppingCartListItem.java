package ru.tusur.ShaurmaWebSiteProject.ui.list;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ShopCartService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.templates.ShoppingCartView;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;

import java.math.BigDecimal;
import java.util.Set;

public class ShoppingCartListItem extends ListItem {

    private final ShopCartService shopCartService;
    private final Image image;
    private final H3 title;
    private final Paragraph description;
    private final Paragraph price;
    private final Paragraph mass;

    public ShoppingCartListItem(OrderContent orderContent, ShopCartService shopCartService) {

        this.shopCartService = shopCartService;
        Product product = orderContent.getProduct();
        addClassNames(AlignItems.START, Display.FLEX, FlexDirection.ROW, FlexDirection.Breakpoint.Large.ROW, Gap.LARGE, Padding.Vertical.LARGE, Position.RELATIVE);

        this.image = new Image(ImageResourceUtils.getImageResource(product.getPreviewUrl()), product.getName());
        this.image.addClassNames(BorderRadius.LARGE);
        this.image.setWidthFull();


        this.title = new H3(product.getName());
        this.title.addClassNames(FontSize.MEDIUM, LineHeight.MEDIUM);

        this.description = new Paragraph(product.getDescription());
        this.description.addClassNames(FontSize.SMALL, TextColor.SECONDARY);

        int commonNum = orderContent.getNum();
        Set<ProductOption> commonProductOptions = product.getProductOptions();
        this.mass = new Paragraph(product.getMass() * commonNum + commonProductOptions.stream().mapToInt(ProductOption::getMass).sum() * commonNum + " г");
        this.mass.addClassNames(FontWeight.NORMAL, Margin.Vertical.NONE);
        this.mass.setWhiteSpace(WhiteSpace.NOWRAP);

        BigDecimal commonBDNum = BigDecimal.valueOf(commonNum);
        this.price = new Paragraph(product.getPrice().add(commonProductOptions.stream().map(ProductOption::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)).multiply(commonBDNum) + " ₽");
        this.price.addClassNames(FontWeight.SEMIBOLD, Margin.Vertical.NONE);
        this.price.setWhiteSpace(WhiteSpace.NOWRAP);

        Layout priceMass = new Layout(this.price, this.mass);
        priceMass.setGap(Layout.Gap.SMALL);
        priceMass.setFlexDirection(Layout.FlexDirection.ROW);

        Layout img = new Layout(this.image, priceMass);
        img.addClassNames(Flex.SHRINK_NONE);
        img.setFlexDirection(Layout.FlexDirection.COLUMN_REVERSE);
        img.setGap(Layout.Gap.SMALL);
        img.setAlignItems(Layout.AlignItems.START);
        img.setJustifyContent(Layout.JustifyContent.CENTER);
        img.setWidth(10, Unit.REM);

        IntegerField quantity = new IntegerField();
        quantity.setAriaLabel("Кол-во");
        quantity.setMax(9);
        quantity.setMin(0);
        quantity.setStepButtonsVisible(true);
        quantity.setValue(orderContent.getNum());
        quantity.setWidth(6, Unit.REM);
        quantity.addValueChangeListener(_ -> {
            String session = VaadinService.getCurrentRequest().getWrappedSession().getId();

            int oldNum = orderContent.getNum();
            orderContent.setNum(quantity.getValue());
            int num = orderContent.getNum();
            BigDecimal bigDOldNum = BigDecimal.valueOf(oldNum);
            BigDecimal bigDNewNum = BigDecimal.valueOf(num);

            Set<ProductOption> productOptions = orderContent.getProduct().getProductOptions();
            int commonOptionsMass = productOptions.stream().mapToInt(ProductOption::getMass).sum();
            int oldMass = (orderContent.getProduct().getMass() + commonOptionsMass) * oldNum;
            int newMass = (orderContent.getProduct().getMass() + commonOptionsMass) * num;
            int massDelta = newMass - oldMass;

            BigDecimal commonOptionsSum = productOptions.stream().map(ProductOption::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal commonProductAndOptionsSum = orderContent.getProduct().getPrice().add(commonOptionsSum);
            BigDecimal newPrice = commonProductAndOptionsSum.multiply(bigDNewNum);
            BigDecimal oldPrice = commonProductAndOptionsSum.multiply(bigDOldNum);
            BigDecimal priceDelta = newPrice.subtract(oldPrice);

            if (orderContent.getNum() <= 0) {
                shopCartService.removeOrderContent(session, orderContent);
                updateParentSummaryData(massDelta, priceDelta, true);
            }
            else {
                mass.setText(STR."\{newMass} г");
                price.setText(STR."\{newPrice} ₽");
                updateParentSummaryData(massDelta, priceDelta, false);
                shopCartService.changeOrderContentNum(session, orderContent);
            }
        });

        Button favourite = new Button(LineAwesomeIcon.HEART.create());
        favourite.addClassNames(TextColor.SECONDARY);
        favourite.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        favourite.setAriaLabel("Избранное");

        Button delete = new Button(LineAwesomeIcon.TRASH_SOLID.create());
        delete.addClassNames(TextColor.SECONDARY);
        delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        delete.setAriaLabel("Убрать");
        delete.addClickListener(_ -> {

            int oldNum = orderContent.getNum() * -1;
            orderContent.setNum(0);
            BigDecimal bigDOldNum = BigDecimal.valueOf(oldNum);

            Set<ProductOption> productOptions = orderContent.getProduct().getProductOptions();
            int commonOptionsMass = productOptions.stream().mapToInt(ProductOption::getMass).sum();
            int massDelta = (orderContent.getProduct().getMass() + commonOptionsMass) * oldNum;

            BigDecimal commonOptionsSum = productOptions.stream().map(ProductOption::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal priceDelta = orderContent.getProduct().getPrice().add(commonOptionsSum).multiply(bigDOldNum);


            shopCartService.removeOrderContent(VaadinService.getCurrentRequest().getWrappedSession().getId(), orderContent);
            updateParentSummaryData(massDelta, priceDelta, true);
        });


        Layout controls = new Layout(quantity, favourite, delete);
        controls.addClassNames(Margin.Top.AUTO);
        controls.setGap(Layout.Gap.MEDIUM);

        Layout info = new Layout(this.title, this.description, controls);
        info.addClassNames(Flex.GROW);
        info.setFlexDirection(Layout.FlexDirection.COLUMN);

        add(img, info);
    }

    private void updateParentSummaryData(int massDelta, BigDecimal priceDelta, boolean removeSelf) {
        if (removeSelf) this.removeFromParent();
        ComponentUtil.fireEvent(
                UI.getCurrent(),
                new ShoppingCartView.ChangeSummaryDataEvent(this, true,
                    new ShoppingCartView.SummaryDataDeltas(
                            massDelta,
                            priceDelta), removeSelf
                )
        );
    }
}