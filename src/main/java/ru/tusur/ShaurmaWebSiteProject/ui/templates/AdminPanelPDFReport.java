package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.itextpdf.text.DocumentException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;
import org.jetbrains.annotations.NotNull;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.PDFCreatorService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Route(value = "Панель администратора - PDF отчеты", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - PDF отчеты")
public class AdminPanelPDFReport extends Main {
    @org.jetbrains.annotations.NotNull
    private final PDFCreatorService pdfCreatorService;
    Layout layout = new Layout();


    public AdminPanelPDFReport(@NotNull PDFCreatorService pdfCreatorService) throws DocumentException, FileNotFoundException {
        this.pdfCreatorService = pdfCreatorService;
        layout.setGap(Layout.Gap.LARGE);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        add(layout);

        H4 title = new H4("Отчет по отзывам");
        DateTimePicker dateTimePicker = getTimePicker("Начало");
        DateTimePicker dateTimePicker1 = getTimePicker("Конец");
        var ref = new Object(){
                Anchor dataReportReviews = getLinkToFile(getResource(dateTimePicker.getValue(), dateTimePicker1.getValue()), "data_report_reviews");
        };
        Layout dateTimePickerLayout = new Layout(title, dateTimePicker, dateTimePicker1);
        dateTimePickerLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        Layout layout1 = new Layout(dateTimePickerLayout, ref.dataReportReviews);
        layout1.setGap(Layout.Gap.SMALL);
        layout1.setFlexDirection(Layout.FlexDirection.ROW);
        dateTimePicker.addValueChangeListener(event -> {
                UI.getCurrent().access(() -> {
                    try {
                        layout1.remove(ref.dataReportReviews);
                        ref.dataReportReviews = getLinkToFile(getResource(dateTimePicker.getValue(), dateTimePicker1.getValue()), "data_report_reviews");
                        layout1.add(ref.dataReportReviews);
                    } catch (DocumentException | FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        });
        dateTimePicker1.addValueChangeListener(event -> {
                UI.getCurrent().access(() -> {
                    try {
                        layout1.remove(ref.dataReportReviews);
                        ref.dataReportReviews = getLinkToFile(getResource(dateTimePicker.getValue(), dateTimePicker1.getValue()), "data_report_reviews");
                        layout1.add(ref.dataReportReviews);
                    } catch (DocumentException | FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        });


        H4 title1 = new H4("Отчет по заказам");
        DateTimePicker dateTimePicker2 = getTimePicker("Начало");
        DateTimePicker dateTimePicker3 = getTimePicker("Конец");
        var ref1 = new Object() {
            Anchor dataReportOrders = getLinkToFile(getResourceOrders(dateTimePicker2.getValue(), dateTimePicker3.getValue()), "data_report_orders");
        };
        Layout dateTimePickerLayout1 = new Layout(title1, dateTimePicker2, dateTimePicker3);
        dateTimePickerLayout1.setFlexDirection(Layout.FlexDirection.COLUMN);
        Layout layout2 = new Layout(dateTimePickerLayout1, ref1.dataReportOrders);
        layout2.setGap(Layout.Gap.SMALL);
        layout2.setFlexDirection(Layout.FlexDirection.ROW);
        dateTimePicker2.addValueChangeListener(event -> {
                UI.getCurrent().access(() -> {
                    try {
                        layout2.remove(ref1.dataReportOrders);
                        ref1.dataReportOrders = getLinkToFile(getResourceOrders(dateTimePicker2.getValue(), dateTimePicker3.getValue()), "data_report_orders");
                        layout2.add(ref1.dataReportOrders);
                    } catch (DocumentException | FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        });
        dateTimePicker3.addValueChangeListener(event -> {
                UI.getCurrent().access(() -> {
                    try {
                        layout2.remove(ref1.dataReportOrders);
                        ref1.dataReportOrders = getLinkToFile(getResourceOrders(dateTimePicker3.getValue(), dateTimePicker3.getValue()), "data_report_orders");
                        layout2.add(ref1.dataReportOrders);
                    } catch (DocumentException | FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        });



        H4 title2 = new H4("Отчет по доходу");
        DateTimePicker dateTimePicker4 = getTimePicker("Начало");
        DateTimePicker dateTimePicker5 = getTimePicker("Конец");
        var ref2 = new Object(){
            Anchor dataReportIncome = getLinkToFile(getResourceIncome(dateTimePicker4.getValue(), dateTimePicker5.getValue()), "data_report_income");
        };
        Layout dateTimePickerLayout2 = new Layout(title2, dateTimePicker4, dateTimePicker5);
        dateTimePickerLayout2.setFlexDirection(Layout.FlexDirection.COLUMN);
        Layout layout3 = new Layout(dateTimePickerLayout2, ref2.dataReportIncome);
        dateTimePicker4.addValueChangeListener(event -> {
                UI.getCurrent().access(() -> {
                    try {
                        layout3.remove(ref2.dataReportIncome);
                        ref2.dataReportIncome = getLinkToFile(getResourceIncome(dateTimePicker4.getValue(), dateTimePicker5.getValue()), "data_report_income");
                        layout3.add(ref2.dataReportIncome);
                    } catch (DocumentException | FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        });
        dateTimePicker5.addValueChangeListener(event -> {
            UI.getCurrent().access(() -> {
                try {
                    layout3.remove(ref2.dataReportIncome);
                    ref2.dataReportIncome = getLinkToFile(getResourceIncome(dateTimePicker4.getValue(), dateTimePicker5.getValue()), "data_report_income");
                    layout3.add(ref2.dataReportIncome);
                } catch (DocumentException | FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        layout3.setGap(Layout.Gap.SMALL);
        layout3.setFlexDirection(Layout.FlexDirection.ROW);
        layout.add(layout1, layout2, layout3);
    }

    private DateTimePicker getTimePicker(String s) {
        DatePicker.DatePickerI18n ruI18n = new DatePicker.DatePickerI18n();
        ruI18n.setMonthNames(List.of("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"));
        ruI18n.setWeekdays(List.of("Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"));
        ruI18n.setWeekdaysShort(List.of("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"));
        ruI18n.setToday("Сегодня");
        ruI18n.setCancel("Отмена");

        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setRequiredIndicatorVisible(true);
        dateTimePicker.getChildren().findFirst().map(component -> component.getStyle().setPaddingRight("14px"));
        dateTimePicker.setLabel(s);
        dateTimePicker.setLocale(new Locale("ru", "RU"));
        dateTimePicker.setValue(LocalDateTime.now(ZoneId.systemDefault()));
        dateTimePicker.setDatePickerI18n(ruI18n);
        dateTimePicker.setDatePlaceholder("ДД/ММ/ГГГГ");
        dateTimePicker.setTimePlaceholder("ЧЧ:ММ");
        return dateTimePicker;
    }

    private StreamResource getResourceOrders(LocalDateTime start, LocalDateTime end) throws DocumentException, FileNotFoundException {
        return pdfCreatorService.createPDFReportOrders(
                Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    private StreamResource getResource(LocalDateTime start, LocalDateTime end) throws DocumentException, FileNotFoundException {
        return pdfCreatorService.createPDFReportReviews(
                Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    private StreamResource getResourceIncome(LocalDateTime start, LocalDateTime end) throws DocumentException, FileNotFoundException {
        return pdfCreatorService.createPDFReportIncome(
                Date.from(start.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
        );
    }

    private Anchor getLinkToFile(StreamResource streamResource, String name) {
        Anchor anchor = new Anchor(streamResource, "Скачать");
        anchor.getStyle().setPaddingTop("38px");
        anchor.getElement().setAttribute("download", true);
        return anchor;
    }
}
