package ru.tusur.ShaurmaWebSiteProject.ui.list;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Order;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContentToProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderContentToProductOptionRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ShopCartService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderHistoryListItem extends com.vaadin.flow.component.html.ListItem {

    private final Order order;
    private final int orderCount;
    private final OrderRepo orderRepo;
    private final ShopCartService shopCartService;


    public OrderHistoryListItem(Order order, int orderCount, OrderRepo orderRepo, ShopCartService shopCartService) {
        this.order = order;
        this.orderCount = orderCount;
        this.orderRepo = orderRepo;
        this.shopCartService = shopCartService;
        this.add(createContent());
    }

    private Component createContent() {
        Layout layoutHat = new Layout();
        layoutHat.setFlexDirection(Layout.FlexDirection.ROW);
        layoutHat.setJustifyContent(Layout.JustifyContent.BETWEEN);

        H2 h2 = new H2("Заказ №" + orderCount);
        h2.getStyle().setMarginTop("12px").setMarginBottom("4px");

        Span titlePrice = new Span(order.getSum() + " ₽");
        titlePrice.addClassNames(LumoUtility.FontSize.SMALL);

        Span titleMass = new Span(order.getMassSum() + " г");
        titleMass.addClassNames(LumoUtility.FontSize.SMALL);

        Layout layoutHat2 = new Layout(titlePrice, titleMass);
        layoutHat2.setGap(Layout.Gap.MEDIUM);
        layoutHat2.setAlignSelf(Layout.AlignSelf.END);
        layoutHat2.getStyle().setMarginTop("8px").setMarginBottom("4px");
        layoutHat.add(h2, layoutHat2);

        Layout layout = new Layout(layoutHat);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        order.getOrderContents().forEach(orderContent -> layout.add(createDetails(orderContent)));

        H3 h3 = new H3("Данные получения заказа");
        h3.getStyle().setMarginTop("6px").setMarginBottom("2px");

        Span deliveryStatus = new Span(order.getOrderState().getContent());
        deliveryStatus.addClassNames(LumoUtility.FontSize.SMALL);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        Span deliveryDateC = new Span("Заказ от: " + simpleDateFormat.format(order.getOrderCreationDate()));
        deliveryDateC.addClassNames(LumoUtility.FontSize.SMALL);

        Span deliveryDateState = new Span("Последнее обновление: " + simpleDateFormat.format(order.getOrderStateDate()));
        deliveryDateState.addClassNames(LumoUtility.FontSize.SMALL);

        Button reorder = new Button("В корзину");
        reorder.addClickListener(event -> {
             order.getOrderContents().forEach(orderContent -> {
                 OrderContentToProductOption orderContentToProductOption = new OrderContentToProductOption();
                 orderContentToProductOption.setProductOptionSet(orderContent.getProduct().getProductOptions());
                 orderContentToProductOption.setOrderContent(orderContent);
                 shopCartService.addOrderContent(VaadinService.getCurrentRequest().getWrappedSession().getId(), orderContent, orderContentToProductOption);
             });
        });
        reorder.addClassNames(LumoUtility.TextColor.SECONDARY);
        reorder.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button favourite = new Button();
        favourite.addClassNames(LumoUtility.TextColor.SECONDARY);
        favourite.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        favourite.setAriaLabel("Избранное");
        if (!order.isFeatured()) {
            favourite.setIcon(LineAwesomeIcon.HEART.create());
        } else {
            SvgIcon icon = LineAwesomeIcon.HEART_SOLID.create();
            icon.setColor("red");
            favourite.setIcon(icon);
        }
        favourite.addClickListener(event -> {
            order.setFeatured(!order.isFeatured());
            if (!order.isFeatured()) {
                favourite.setIcon(LineAwesomeIcon.HEART.create());
            } else {
                SvgIcon icon = LineAwesomeIcon.HEART_SOLID.create();
                icon.setColor("red");
                favourite.setIcon(icon);
            }
            orderRepo.save(order);
            reorder.setEnabled(!order.isFeatured());
        });

        Layout layout1 = new Layout(favourite, reorder);
        layout1.setGap(Layout.Gap.SMALL);
        layout1.setFlexDirection(Layout.FlexDirection.ROW);

        Layout deliveryData = new Layout(h3, deliveryStatus, deliveryDateC, deliveryDateState, layout1);
        deliveryData.setFlexDirection(Layout.FlexDirection.ROW);
        deliveryData.getStyle().set("display", "inline-grid");
        layout.add(deliveryData);

        return layout;
    }

    private Details createDetails(OrderContent orderContent) {
        Layout summary = new Layout();
        summary.setFlexDirection(Layout.FlexDirection.ROW);
        summary.getStyle().set("display", "inline-grid");

        Span span = new Span(orderContent.getProduct().getName());
        span.addClassNames(LumoUtility.FontSize.SMALL);

        Span span1 = new Span("Кол-во: " + orderContent.getNum());
        span1.getStyle().set("margin-left", "15%");
        span1.addClassNames(LumoUtility.FontSize.SMALL);

        Layout layout = new Layout(span, span1);
        layout.setFlexDirection(Layout.FlexDirection.ROW);

        BigDecimal price = orderContent.getProduct().getPrice();
        BigDecimal finalPrice = price;
        price = finalPrice.multiply(BigDecimal.valueOf(orderContent.getNum()));

        Span span3 = new Span("Сумма: ");
        span3.addClassNames(LumoUtility.FontSize.SMALL);

        Span span4 = new Span(price + " ₽");
        span4.getStyle().set("margin-left", "60%");
        span4.addClassNames(LumoUtility.FontSize.SMALL);

        Layout layout1 = new Layout(span3, span4);
        layout1.setFlexDirection(Layout.FlexDirection.ROW);

        Span span5 = new Span("Масса: ");
        span5.addClassNames(LumoUtility.FontSize.SMALL);

        Span span6 = new Span(orderContent.getProduct().getMass() * orderContent.getNum() + " г");
        span6.getStyle().set("margin-left", "70%");
        span6.addClassNames(LumoUtility.FontSize.SMALL);

        Layout layout2 = new Layout(span5, span6);
        layout2.setFlexDirection(Layout.FlexDirection.ROW);

        summary.add(layout, layout1, layout2);

        Layout optionsLayout = new Layout();
        optionsLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        optionsLayout.setJustifyContent(Layout.JustifyContent.BETWEEN);
        orderContent.getOrderContentToProductOption().getProductOptionSet().forEach(productOption -> {
            Span contentName = new Span(productOption.getName());
            contentName.addClassNames(LumoUtility.FontSize.SMALL);

            Span contentMass = new Span(productOption.getMass() + " г");
            contentMass.getStyle().set("margin-inline-start", "10%");
            contentMass.addClassNames(LumoUtility.FontSize.SMALL);

            Span contentPriceSpan = new Span(productOption.getPrice() + " ₽");
            contentPriceSpan.getStyle().set("margin-inline-start", "auto");
            contentPriceSpan.addClassNames(LumoUtility.FontSize.SMALL);
            Layout layout4 = new Layout(contentName, contentPriceSpan, contentMass);
            layout4.setFlexDirection(Layout.FlexDirection.ROW);
            optionsLayout.add(layout4);
        });

        Details details = new Details(summary, optionsLayout);
        details.addClassNames(LumoUtility.Border.TOP, LumoUtility.Margin.Vertical.NONE, LumoUtility.Padding.Vertical.MEDIUM);
        details.addThemeVariants(DetailsVariant.REVERSE);
        return details;
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
}
