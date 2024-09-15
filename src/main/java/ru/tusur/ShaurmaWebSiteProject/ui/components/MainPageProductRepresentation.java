package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.i18n.UploadExamplesI18N;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

@Tag("div")
public class MainPageProductRepresentation extends Component implements HasComponents {

    @Getter
    private Product product;
    private VerticalLayout verticalLayout;
    private HorizontalLayout priceGroup;
    private Div priceGroupWrapper;
    private Div imageComponent;
    private Div priceComponent;
    private Div massComponent;
//    private final Div discountComponent;
    private Div nameComponent;
    private Div descriptionComponent;
    private Div submitButtonComponent;


    public MainPageProductRepresentation() {
        initComponent();
        this.add(verticalLayout);
    }

    public MainPageProductRepresentation(Product product){
        this.product = product;
        initComponent();
        this.add(verticalLayout);
    }

    private void initComponent(){
        verticalLayout = new VerticalLayout();
        imageComponent = new Div();
        priceGroup = new HorizontalLayout();
        priceGroupWrapper = new Div();
        priceComponent = new Div();
//        discountComponent = new Div();
        massComponent = new Div();
        nameComponent = new Div();
        descriptionComponent = new Div();
        submitButtonComponent = new Div();

//        imageComponent.setMaxWidth("185px");
        imageComponent.setMaxHeight("128px");
        imageComponent.setSizeFull();
        imageComponent.addClassName("mainPageProductElementPlaceholder");
        imageComponent.addClassName("containImg");
        imageComponent.getStyle().setMarginBottom("8px");

        priceGroup.addClassName("boldSpan");
//        priceGroup.addAndExpand(priceComponent, discountComponent);
        priceGroup.addAndExpand(priceComponent);

        priceGroupWrapper.setMaxHeight("24px");
        priceGroupWrapper.setSizeFull();
        priceGroupWrapper.getStyle().setMarginBottom("4px");
        priceGroupWrapper.getStyle().setMargin("0px");
        priceGroupWrapper.add(priceGroup);

        nameComponent.setMaxHeight("16px");
        nameComponent.getStyle().setMarginBottom("16px");
        nameComponent.setSizeFull();

        massComponent.setMaxHeight("12px");
        massComponent.setSizeFull();


        verticalLayout.addAndExpand(imageComponent, priceGroupWrapper, nameComponent, massComponent);
        verticalLayout.getStyle().setMargin("4px");
        verticalLayout.getStyle().setPadding("4px");
        verticalLayout.setSpacing(false);
        verticalLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        verticalLayout.setAlignSelf(FlexComponent.Alignment.CENTER);
        verticalLayout.addClassName("mainPageProductBodyPlaceholder");
        verticalLayout.setWidth("195px");
        verticalLayout.setHeight("294px");
        verticalLayout.getStyle()
                .setJustifyContent(Style.JustifyContent.NORMAL)
                .setPosition(Style.Position.RELATIVE);

    }

    public void populateComponents(@NotNull Product product) {
        this.product = product;

        nameComponent.removeAll();
        Span span2 = new Span("Название");
        span2.getElement().getThemeList().add("badge small primary");
        nameComponent.add(span2);

        if(!StringUtils.isEmpty(product.getName())){
            nameComponent.removeAll();
            nameComponent.add(new Span(product.getName()));
        }

        priceComponent.removeAll();
        Span span1 = new Span("Цена/скидка");
        span1.getElement().getThemeList().add("badge small primary");
        priceComponent.add(new Span(span1));

        if (Optional.ofNullable(product.getPrice()).isPresent()){
            priceComponent.removeAll();
            priceComponent.add(new Span(product.getPrice().setScale(2, RoundingMode.UP).toString() + " ₽"));
        }

//        discountComponent.removeAll();
//
//        if (Optional.ofNullable(product.getDiscount()).isPresent()) {
//            discountComponent.removeAll();
//            Span span = new Span("-" + product.getDiscount() + "%");
//            span.getElement().getThemeList().add("badge small");
//            discountComponent.add(span);
//        }

        Span plaseholderSpan = new Span("Название");
        plaseholderSpan.getElement().getThemeList().add("badge small primary");
        massComponent.removeAll();
        plaseholderSpan.setText("Масса");
        massComponent.add(plaseholderSpan);
        if(Optional.ofNullable(product.getMass()).isPresent()){
            Span massSpan = new Span();
            massComponent.removeAll();
            if(product.getMass() >= 1000){
                massSpan.setText(String.valueOf(product.getMass()/1000f) + " кг");
            } else {
                massSpan.setText(String.valueOf(product.getMass()) + " г");
            }
            massComponent.add(massSpan);
        }

        imageComponent.removeAll();
        imageComponent.add(new Icon(VaadinIcon.FILE_PICTURE));

        if (!StringUtils.isEmpty(product.getPreviewUrl())) {
            imageComponent.removeAll();
            imageComponent.add(new Image(new StreamResource(FilenameUtils.getName(this.product.getPreviewUrl()), (InputStreamFactory) () -> {
                try {
                    return new DataInputStream(new FileInputStream(product.getPreviewUrl()));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }), "My Streamed Image"));
        }
    }

    public void createContextMenus() {
        MainPageProductRecord mainPageProductRecord = new MainPageProductRecord(
                imageComponent,
                massComponent,
                nameComponent,
                priceGroupWrapper);
        var fields = new ArrayList<Field>();
        for (var component : MainPageProductRecord.class.getRecordComponents()) {
            try {
                Field field = mainPageProductRecord.getClass().getDeclaredField(component.getName());
                field.setAccessible(true);
                fields.add(field);
            } catch (NoSuchFieldException e) {
                // for simplicity, error handling is skipped
            }
        }
        for (Field field : fields) {
            try {
                add(new ProductPropertyContextMenu((Div) field.get(mainPageProductRecord)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class ProductPropertyContextMenu extends ContextMenu {
        public ProductPropertyContextMenu(Div target) {
            super(target);
            Span plaseholderSpan = new Span();
            plaseholderSpan.getElement().getThemeList().add("badge small primary");
            setOpenOnClick(true);

            if (target.equals(imageComponent)) {
                Dialog dialog = attachUploadDialog();
                addItem("Загрузить изображение", menuItemClickEvent -> dialog.open());

                addItem("Очистить", menuItemClickEvent -> {
                    imageComponent.removeAll();
                    product.setPreviewUrl("");
                    imageComponent.add(new Icon(VaadinIcon.FILE_PICTURE));
                });
            }

            if (target.equals(priceGroupWrapper)) {
                Dialog dialog = attachSetupPriceDialog();
                addItem("Задать цену/скидку", menuItemClickEvent -> dialog.open());
                addItem("Очистить", menuItemClickEvent -> {
//                    product.setDiscount(null);
                    product.setPrice(null);
                    priceComponent.removeAll();
//                    discountComponent.removeAll();
//                    plaseholderSpan.setText("Цена/скидка");
                    plaseholderSpan.setText("Цена");
                    priceComponent.add(plaseholderSpan);
                });
            }

            if (target.equals(nameComponent)) {
                Dialog dialog = attachSetupNameDialog();
                addItem("Задать название", menuItemClickEvent -> dialog.open());
                addItem("Очистить", menuItemClickEvent -> {
                    product.setName(null);
                    nameComponent.removeAll();
                    plaseholderSpan.setText("Название");
                    nameComponent.add(plaseholderSpan);
                });
            }

            if (target.equals(massComponent)) {
                Dialog dialog = attachSetupMassDialog();
                addItem("Задать массу", menuItemClickEvent -> dialog.open());
                addItem("Очистить", menuItemClickEvent -> {
                    product.setMass(null);
                    massComponent.removeAll();
                    plaseholderSpan.setText("Масса");
                    massComponent.add(plaseholderSpan);
                });
            }
        }

        private @NotNull Dialog attachSetupMassDialog(){
            Dialog dialog = new Dialog();

            IntegerField massField = new IntegerField();
            massField.setMin(0);
            massField.setLabel("Масса");
            massField.setSuffixComponent(new Span("г"));
            massField.addValueChangeListener(numberFieldDoubleComponentValueChangeEvent -> {
                Integer mass = massField.getValue();
                if(mass != null){
                    if(mass.compareTo(massField.getMin()) < 0){
                        massField.setErrorMessage("Масса не может быть меньще нуля.");
                        massField.setInvalid(true);
                    } else if (mass.compareTo(massField.getMin()) == 0){
                        massField.setErrorMessage("Масса не может быть равна нулю.");
                        massField.setInvalid(true);
                    } else {
                        massField.setInvalid(false);
                    }
                }
            });

            Button submit = new Button("Сохранить");
            submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            submit.addClickListener(buttonClickEvent -> {
                Integer mass = massField.getValue();
                if(mass != null){
                    if(mass.compareTo(massField.getMin()) < 0){
                        massField.setErrorMessage("Масса не может быть меньще нуля.");
                        massField.setInvalid(true);
                        return;
                    } else if (mass.compareTo(massField.getMin()) == 0){
                        massField.setErrorMessage("Масса не может быть равна нулю.");
                        massField.setInvalid(true);
                        return;
                    } else {
                        massField.setInvalid(false);
                    }
                }
                else {
                    massField.setErrorMessage("Введите массу");
                    massField.setInvalid(true);
                    return;
                }

                product.setMass(massField.getValue());

                massComponent.removeAll();
                Span massSpan = new Span();
                if(product.getMass() >= 1000){
                    massSpan.setText(String.valueOf(product.getMass()/1000f) + " кг");
                } else {
                    massSpan.setText(String.valueOf(product.getMass()) + " г");
                }
                massComponent.add(new Span(String.valueOf(massSpan)));

                dialog.close();
            });

            Button cancel = new Button(new Icon("lumo", "cross"),
                    (e) -> dialog.close());

            FormLayout formLayout = new FormLayout();
            formLayout.add(massField);

            dialog.getFooter().add(submit);
            dialog.getHeader().add(cancel);
            dialog.setHeaderTitle("Установка массы");
            dialog.add(formLayout);

            return dialog;
        }

        private @NotNull Dialog attachSetupNameDialog(){
            Dialog dialog = new Dialog();

            TextField nameField = new TextField();
            nameField.setLabel("Название");
            nameField.setErrorMessage("Введите название");


            Button submit = new Button("Сохранить");
            submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            submit.addClickListener(buttonClickEvent -> {
                if(nameField.getOptionalValue().isEmpty()){
                    nameField.setInvalid(true);
                    return;
                }

                product.setName(nameField.getValue());

                nameComponent.removeAll();
                nameComponent.add(new Div(nameField.getValue()));

                dialog.close();
            });


            Button cancel = new Button(new Icon("lumo", "cross"),
                    (e) -> dialog.close());

            FormLayout formLayout = new FormLayout();
            formLayout.add(nameField);

            dialog.addAttachListener(attachEvent -> {
                if(Optional.ofNullable(product.getName()).isPresent()) nameField.setValue(product.getName());
            });
            dialog.add(formLayout);
            dialog.getHeader().add(cancel);
            dialog.getFooter().add(submit);
            dialog.setHeaderTitle("Установка названия");

            return dialog;

        }

        private @NotNull Dialog attachSetupPriceDialog() {
            Dialog dialog = new Dialog();

            MyBigDecimalField priceField = new MyBigDecimalField();
            priceField.setLabel("Цена");
            priceField.setSuffixComponent(new Span("RUB"));
            priceField.addValueChangeListener(numberFieldDoubleComponentValueChangeEvent -> {
                if(Optional.ofNullable(product.getPrice()).isPresent() && priceField.getValue() == null) priceField.setValue(product.getPrice());
                BigDecimal price = priceField.getValue();
                if(price != null){
                    if(price.compareTo(new BigDecimal("0")) == 0){
                        priceField.setErrorMessage("Цена не может быть равна нулю");
                        priceField.setInvalid(true);
                    } else if (price.compareTo(new BigDecimal("0")) < 0) {
                        priceField.setErrorMessage("Цена не может быть меньще нуля");
                        priceField.setInvalid(true);
                    } else {
                        priceField.setInvalid(false);
                    }
                } else {
                    priceField.setErrorMessage("Введите цену");
                    priceField.setInvalid(true);
                }
            });

//            IntegerField discountField = new IntegerField();
//            discountField.setLabel("Скидка");
//            discountField.setSuffixComponent(new Span("%"));
//            discountField.setPrefixComponent(new Span("-"));
//            discountField.setHelperText("*Заполните поле, если требуется разместить скидку, в противном случае оставьте пустым.");
//            discountField.setMin(0);
//            discountField.setMax(100);
//            discountField.addValueChangeListener(integerFieldIntegerComponentValueChangeEvent -> {
//                if(Optional.ofNullable(product.getDiscount()).isPresent() && discountField.getValue() == null) discountField.setValue(product.getDiscount());
//                Integer discount = discountField.getValue();
//                if(discount != null){
//                    if(discount.compareTo(discountField.getMin()) == 0){
//                        discountField.setErrorMessage("Скидка не может быть равна нулю.");
//                        discountField.setInvalid(true);
//                    } else if (discount.compareTo(discountField.getMin()) < 0) {
//                        discountField.setErrorMessage("Скидка не может быть меньще нуля.");
//                        discountField.setInvalid(true);
//                    } else if (discount.compareTo(discountField.getMax()) == 0) {
//                        discountField.setErrorMessage("Скидка не может быть равна ста процентам.");
//                        discountField.setInvalid(true);
//                    } else if (discount.compareTo(discountField.getMax()) > 0) {
//                        discountField.setErrorMessage("Скидка не может быть больще ста процентов.");
//                        discountField.setInvalid(true);
//                    } else {
//                        discountField.setInvalid(false);
//                    }
//                }
//            });

            Button submit = new Button("Сохранить");
            submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            submit.addClickListener(buttonClickEvent -> {
                BigDecimal price = priceField.getValue();
                if(price != null){
                    if(price.compareTo(new BigDecimal("0")) == 0){
                        priceField.setErrorMessage("Цена не может быть равна нулю");
                        priceField.setInvalid(true);
                        return;
                    } else if (price.compareTo(new BigDecimal("0")) < 0) {
                        priceField.setErrorMessage("Цена не может быть меньще нуля");
                        priceField.setInvalid(true);
                        return;
                    } else {
                        priceField.setInvalid(false);
                    }
                } else {
                    priceField.setErrorMessage("Введите цену");
                    priceField.setInvalid(true);
                    return;
                }

//                Integer discount = discountField.getValue();
//                if(discount != null){
//                    if(discount.compareTo(discountField.getMin()) == 0){
//                        discountField.setErrorMessage("Скидка не может быть равна нулю.");
//                        discountField.setInvalid(true);
//                        return;
//                    } else if (discount.compareTo(discountField.getMin()) < 0) {
//                        discountField.setErrorMessage("Скидка не может быть меньще нуля.");
//                        discountField.setInvalid(true);
//                        return;
//                    } else if (discount.compareTo(discountField.getMax()) == 0) {
//                        discountField.setErrorMessage("Скидка не может быть равна ста процентам.");
//                        discountField.setInvalid(true);
//                        return;
//                    } else if (discount.compareTo(discountField.getMax()) > 0) {
//                        discountField.setErrorMessage("Скидка не может быть больще ста процентов.");
//                        discountField.setInvalid(true);
//                        return;
//                    } else {
//                        discountField.setInvalid(false);
//                    }
//                }

                product.setPrice(priceField.getValue());
//                product.setDiscount(discountField.getValue());

                priceComponent.removeAll();
                priceComponent.add(new Span(priceField.getValue().setScale(2, RoundingMode.UP).toString() + " ₽"));
//                if (discountField.getOptionalValue().isPresent()) {
//                    Span span = new Span("-" + discountField.getValue().toString() + "%");
//                    span.getElement().getThemeList().add("badge small");
//                    discountComponent.removeAll();
//                    discountComponent.add(span);
//                }

                dialog.close();
            });

            Button cancel = new Button(new Icon("lumo", "cross"),
                    (e) -> dialog.close());

            FormLayout formLayout = new FormLayout();
//            formLayout.add(priceField, discountField);
            formLayout.add(priceField);

            dialog.setHeaderTitle("Установка цены/скидки");
            dialog.getHeader().add(cancel);
            dialog.getFooter().add(submit);
            dialog.add(formLayout);

            return dialog;
        }

        private @NotNull Dialog attachUploadDialog() {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Загрузка презентационного изображения");
            VerticalLayout verticalLayout1 = new VerticalLayout();
            MemoryBuffer buffer = new MemoryBuffer();
            Upload upload = new Upload(buffer);
            upload.addSucceededListener(event -> {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream();
                try {
                    Files.write(Path.of("src/main/resources/META-INF/resources/images/" + fileName), buffer.getInputStream().readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                StreamResource imageResource = new StreamResource(fileName, (InputStreamFactory) () -> inputStream);

                product.setPreviewUrl("src/main/resources/META-INF/resources/images/" + fileName);
                imageComponent.removeAll();
                imageComponent.add(new Image(imageResource, "My Streamed Image"));
                dialog.close();
            });
            UploadExamplesI18N i18n = new UploadExamplesI18N();
            H4 title = new H4("Загрузить изображение");
            Paragraph hint = new Paragraph("Размер файла должен быть меньше или равен 5 МБ. Принимаются только файлы \".png\", \".jpeg\" и \".jpg\"...");
            upload.setI18n(i18n);
            upload.setWidthFull();
            upload.setAcceptedFileTypes(".png", ".jpeg", ".jpg");
            upload.setMaxFileSize(5 * 1024 * 1024);
            upload.setMaxFiles(1);
            verticalLayout1.setWidthFull();
            verticalLayout1.add(title, hint, upload);
            dialog.add(verticalLayout1);
            return dialog;
        }

    }

    @Tag("my-big-decimal-field")
    @JsModule("./my-big-decimal-field.js")
    static class MyBigDecimalField extends BigDecimalField { }

}
