package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.service.PromotionService;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ShopCartService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.*;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.ButtonTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.InputTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.RadioButtonTheme;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.Breakpoint;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.Size;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@AnonymousAllowed
@PageTitle("Оплата")
@Route(value = "Оплата", layout = MainLayout.class)
public class CheckoutView extends Main {

    private final ShopCartService shopCartService;
    private final PromotionService promotionService;
    private final Order order;

    private final KeyValuePair productsSumPriceBucket = new KeyValuePair("Сумма товаров", "-");
    private final KeyValuePair discountBucket = new KeyValuePair("Скидка", "-");
    private final KeyValuePair deliveryPriceBucket = new KeyValuePair("Доставка", "-");
    private final KeyValuePair orderSumPriceBucket = new KeyValuePair("Всего", "-");

    private BigDecimal productsSumPrice = BigDecimal.ZERO;
    private BigDecimal deliveryPrice = BigDecimal.ZERO;
    private BigDecimal orderSumPrice = BigDecimal.ZERO;
    private BigDecimal discountValue = BigDecimal.ZERO;


    public CheckoutView(ShopCartService shopCartService, PromotionService promotionService) {
        this.shopCartService = shopCartService;
        this.promotionService = promotionService;
        this.order = new Order();
        addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, FlexDirection.Breakpoint.Medium.ROW,
                Margin.Horizontal.AUTO, MaxWidth.SCREEN_LARGE);
        add(createForm(), createSummary());
    }

    public static LocalDate getNextBusinessDay(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            date = date.plusDays(2);
        } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.plusDays(1);
        }
        return date;
    }

    private Component createForm() {
        Layout layout = new Layout(createReceivingMethod(), createShippingInformation(), createPaymentInformation());
        layout.addClassNames(Padding.LARGE);
        layout.setBoxSizing(Layout.BoxSizing.BORDER);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    private Component createShippingInformation() {
        H2 title = new H2("Данные доставки");
        title.addClassNames(FontSize.XLARGE, Margin.Bottom.SMALL, Margin.Top.MEDIUM);

        TextField address = new TextField("Адресс");
//        address.addValueChangeListener(event -> {
//            deliveryPrice = promotionService.getDeliveryPrice();
//        });

        TextField phone = new TextField("Телефон");
        phone.setPrefixComponent(LineAwesomeIcon.PHONE_SOLID.create());

        EmailField email = new EmailField("Email");
        email.setPrefixComponent(LineAwesomeIcon.ENVELOPE.create());


        DatePicker.DatePickerI18n ruI18n = new DatePicker.DatePickerI18n();
        ruI18n.setMonthNames(List.of("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"));
        ruI18n.setWeekdays(List.of("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"));
        ruI18n.setWeekdaysShort(List.of("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"));
        ruI18n.setToday("Сегодня");
        ruI18n.setCancel("Отмена");

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setRequiredIndicatorVisible(true);
        dateTimePicker.setLabel("Дата и время доставки");
        dateTimePicker.setHelperText("Должно быть в пределах 7 дней с сегодняшнего дня");
        dateTimePicker.setMin(LocalDateTime.now());
        dateTimePicker.setMax(LocalDateTime.now().plusDays(7));
        dateTimePicker.setLocale(new Locale("ru", "RU"));
        dateTimePicker.setValue(LocalDateTime.now(ZoneId.systemDefault()));
        dateTimePicker.setDatePickerI18n(ruI18n);

        Layout layout = new Layout(title, address, phone, email, dateTimePicker);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumns(Layout.GridColumns.COLUMNS_4);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_2, phone, email);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, address, dateTimePicker);
        return layout;
    }

    private Component createReceivingMethod() {
        H2 title = new H2("Сопособ доставки");
        title.addClassNames(FontSize.XLARGE, Margin.Bottom.SMALL, Margin.Top.XLARGE);

        RadioButtonGroup<DeliveryType> receivingMethod = new RadioButtonGroup("Выберете сопособ доставки");
        receivingMethod.setItems(DeliveryType.values());
        receivingMethod.setRenderer(new ComponentRenderer<>(method -> renderDeliveryMethod(method)));
        receivingMethod.setValue(DeliveryType.PICK_UP);
        setRadioButtonGroupTheme(receivingMethod, RadioButtonTheme.TOGGLE, RadioButtonTheme.EQUAL_WIDTH);

        Layout layout = new Layout(title, receivingMethod);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    private Component renderDeliveryMethod(DeliveryType type) {
        Span name = new Span(type.getString());
        name.addClassNames(FontWeight.MEDIUM);

        Span cost = new Span();

        Span date = new Span();
        date.addClassNames(FontSize.SMALL, TextColor.SECONDARY);

        switch (type) {
            case COURIER:
            default:
                //todo put delivery price here
                cost.setText("-");
                break;

            case PICK_UP:
                cost.setText("Бесплатно");
                break;
        }

        Span primary = new Span(name, cost);
        primary.addClassNames(Display.FLEX, FlexWrap.WRAP, JustifyContent.BETWEEN);

        Span span = new Span(primary, date);
        span.addClassNames(Display.FLEX, Flex.GROW, FlexDirection.COLUMN,
                Gap.XSMALL, Padding.SMALL);
        return span;
    }

    private Component createPaymentInformation() {
        H2 title = new H2("Платежная информация");
        title.addClassNames(FontSize.XLARGE, Margin.Bottom.SMALL, Margin.Top.XLARGE);

        RadioButtonGroup<PaymentType> paymentMethod = new RadioButtonGroup("Способ оплаты");
        paymentMethod.setItems(PaymentType.values());
        paymentMethod.setRenderer(new ComponentRenderer<>(method -> renderPaymentMethod(method)));
        paymentMethod.setValue(PaymentType.CARD);
        setRadioButtonGroupTheme(paymentMethod, RadioButtonTheme.EQUAL_WIDTH, RadioButtonTheme.TOGGLE);

        TextField creditCard = new TextField("Номер карты");
        ExpirationDateField expiration = new ExpirationDateField("Дата окончания срока");
        TextField securityCode = new TextField("Код безопасности");

        Layout layout = new Layout(title, paymentMethod, creditCard, expiration, securityCode);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setDisplay(Breakpoint.LARGE, Layout.Display.GRID);
        layout.setColumns(Layout.GridColumns.COLUMNS_2);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        layout.setColumnSpan(Layout.ColumnSpan.COLUMN_SPAN_FULL, title, paymentMethod, creditCard);
        return layout;
    }

    private Layout renderPaymentMethod(PaymentType type) {
        Layout layout = new Layout();
        layout.setJustifyContent(Layout.JustifyContent.CENTER);
        layout.setAlignItems(Layout.AlignItems.CENTER);
        layout.setGap(Layout.Gap.SMALL);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setMarginBottom("auto").setPaddingTop("4px").setMarginLeft("auto").setMarginRight("auto");
        switch (type) {
            case CARD, CARD_ONLINE, CASH-> {
                SvgIcon icon = new SvgIcon("line-awesome/svg/" + type.getUrl() + ".svg");
                layout.add(icon);
            }
            case SBP_ONLINE, SBP -> {
                Image img = new Image(type.getUrl(), type.getName());
                img.setMaxHeight(Size.LARGE);
                img.getStyle().set("object-fit", "contain");
                img.setMaxWidth(100, Unit.PERCENTAGE);
                layout.add(img);
            }
        }
        Span span = new Span(type.getName());
        span.addClassName(TextAlignment.CENTER);
        layout.add(span);
        return layout;
    }

//    private Component createCreditCard() {
//        Image visa = new Image("https://upload.wikimedia.org/wikipedia/commons/d/d6/Visa_2021.svg", "Visa");
//        visa.setMaxHeight(Size.LARGE);
//        visa.setMaxWidth(100, Unit.PERCENTAGE);
//
//        Image mastercard = new Image("https://upload.wikimedia.org/wikipedia/commons/a/a4/Mastercard_2019_logo.svg", "Mastercard");
//        mastercard.setMaxHeight(Size.LARGE);
//        mastercard.setMaxWidth(100, Unit.PERCENTAGE);
//
//        Span images = new Span(visa, mastercard);
//        images.addClassNames(AlignItems.CENTER, BoxSizing.BORDER, Display.GRID,
//                Gap.SMALL, Grid.Column.COLUMNS_2, Padding.SMALL);
//        return images;
//    }

    private void setRadioButtonGroupTheme(RadioButtonGroup group, String... themeNames) {
        group.addThemeNames(themeNames);
        group.getChildren().forEach(component -> {
            for (String themeName : themeNames) {
                component.getElement().getThemeList().add(themeName);
            }
        });
    }

    private Component createSummary() {
        H2 title = new H2("Ваш заказ");
        title.addClassNames(FontSize.XLARGE);

        OrderedHashSet<OrderContent> allOrderContent = shopCartService.getAllOrderContent(VaadinService.getCurrentRequest().getWrappedSession().getId());
        order.setOrderContents(allOrderContent);
        allOrderContent.forEach(orderContent -> {
            BigDecimal productPrice = orderContent.getProduct().getPrice();
            BigDecimal bigDNum = BigDecimal.valueOf(orderContent.getNum());
            productsSumPrice = productsSumPrice.add(productPrice).add(orderContent.getProduct().getProductOptions().stream().map(ProductOption::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)).multiply(bigDNum);
            order.setMassSum(order.getMassSum() + (orderContent.getProduct().getMass() * orderContent.getNum()));
            //todo do delivery and discount
        });
        orderSumPrice = orderSumPrice.add(discountValue).add(productsSumPrice).add(deliveryPrice);
        order.setSum(orderSumPrice);
        setSummaryData();

        KeyValuePairs pairs = new KeyValuePairs(
                productsSumPriceBucket,
                deliveryPriceBucket,
                discountBucket,
                orderSumPriceBucket
        );
        pairs.addClassNames("divide-y");
        pairs.setKeyWidthFull();
        pairs.removeBackgroundColor();
        pairs.removeHorizontalPadding();

        TextField code = new TextField("Введите промо-код");
        code.addClassNames(Flex.GROW);
        code.addThemeName(InputTheme.OUTLINE);

        Button apply = new Button("Применить");
        apply.addClassNames(Background.BASE);
        apply.addThemeName(ButtonTheme.OUTLINE);

        InputGroup inputGroup = new InputGroup(code, apply);

        Notification notification = new Notification("\"Заказ оплачен\"", 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        Button confirmOrder = new Button("Оплатить", event -> {
            notification.open();
        });
        confirmOrder.addClassNames(AlignItems.CENTER, Background.PRIMARY,
                BorderRadius.MEDIUM, Display.FLEX, FontWeight.SEMIBOLD,
                Height.MEDIUM, JustifyContent.CENTER, TextColor.PRIMARY_CONTRAST);

        Layout layout = new Layout(title, pairs, inputGroup, confirmOrder);
        layout.addClassNames(Background.CONTRAST_5, BorderRadius.LARGE, Padding.LARGE);
        layout.setBoxSizing(Layout.BoxSizing.BORDER);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.setGap(Layout.Gap.MEDIUM);

        Section section = new Section(layout);
        section.addClassNames(BoxSizing.BORDER, Padding.LARGE);
        section.setMinWidth(24, Unit.REM);
        return section;
    }

    private void setSummaryData() {
        UI.getCurrent().access(() -> {
            discountBucket.setValue("-" + discountValue + " ₽");
            productsSumPriceBucket.setValue(productsSumPrice + " ₽");
            orderSumPriceBucket.setValue(orderSumPrice + " ₽");
        });
    }
}