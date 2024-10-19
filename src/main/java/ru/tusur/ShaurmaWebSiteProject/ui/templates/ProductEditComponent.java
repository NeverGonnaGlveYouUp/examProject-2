package ru.tusur.ShaurmaWebSiteProject.ui.templates;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Getter;
import lombok.Setter;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.Dialogs;

@Setter
@Getter
public class ProductEditComponent extends Div implements Dialogs {
    private Dialog productEditDialog;
    private Tab productAsOnMainPage;
    private Tab productInDetails;
    private MainPageProductRepresentation mainPageProductRepresentation;
    private MainPageProductRepresentation productInDetailsRepresentation;
    private VerticalLayout dialogMainBody;
    public Product product;
    public ProductService productService;
    private Button delete;
//    private Button clear;
    private Button save;
    public Dialog confirmDeletionDialog;

    public ProductEditComponent(ProductService productService) {
        this.productService = productService;
        mainPageProductRepresentation = new MainPageProductRepresentation();
        mainPageProductRepresentation.createContextMenus();
        productInDetailsRepresentation = new MainPageProductRepresentation();
        productInDetailsRepresentation.createContextMenus();
        productEditDialog = new Dialog(createDialogLayout(mainPageProductRepresentation));
        productAsOnMainPage = new Tab(new Span("Как на главной"));
        productInDetails = new Tab(new Span("Подробно"));
        Tabs tabs = new Tabs(productAsOnMainPage, productInDetails);
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        Button cancel = new Button(new Icon("lumo", "cross"), (e) -> productEditDialog.close());

        save = new Button("Сохранить");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        save.addClickListener(buttonClickEvent -> {
            if(tabs.getSelectedTab().equals(productAsOnMainPage) && this.isAttached()){
                this.product = mainPageProductRepresentation.getProduct();
            } else if (tabs.getSelectedTab().equals(productInDetails) && this.isAttached()) {
                this.product = productInDetailsRepresentation.getProduct();
            }
            productService.update(product);
            productEditDialog.close();
        });

//
//        clear = new Button("Очистить");
//        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
//        clear.addClickListener(buttonClickEvent -> {
//            Product product1 = productService.findById(product.getId()).orElseThrow(() -> new NotFoundException("product by id not found"));
//            product = product1;
//            productInDetailsRepresentation.populateComponents(product);
//            mainPageProductRepresentation.populateComponents(product);
////            productEditDialog.close();
//        });



        Button closeButton = new Button("Закрыть");
        closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        closeButton.addClickListener(e -> {
            productEditDialog.close();
        });

        delete = new Button("Удалить");
        delete.addClickListener(buttonClickEvent -> {
            confirmDeletionDialog.open();
        });
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        delete.getStyle().set("margin-inline-end", "auto");
        delete.getStyle().setAlignSelf(Style.AlignSelf.FLEX_START);

        HorizontalLayout buttonLayout = new HorizontalLayout(delete, save);
        buttonLayout.getStyle().set("flex-wrap", "wrap");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        buttonLayout.setPadding(false);
        buttonLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        buttonLayout.setAlignSelf(FlexComponent.Alignment.START);

        cancel.getStyle().setPadding("16px");
        HorizontalLayout horizontalLayout = new HorizontalLayout(new H2("Редактирование товара"), cancel);
        VerticalLayout topLayout = new VerticalLayout(horizontalLayout, tabs);
        topLayout.addClassName(LumoUtility.AlignContent.CENTER);
        topLayout.addClassName(LumoUtility.AlignItems.STRETCH);

        productEditDialog.setCloseOnOutsideClick(false);
        productEditDialog.setCloseOnEsc(false);
        productEditDialog.setResizable(true);
        productEditDialog.setDraggable(true);
        productEditDialog.getHeader().add(topLayout);
        productEditDialog.getFooter().add(buttonLayout);

        tabs.addSelectedChangeListener(selectedChangeEvent -> setContent(selectedChangeEvent.getSelectedTab()));
    }

    public void open(Product product){
        this.product = product;
        confirmDeletionDialog = confirmDeletionDialog(product, productService);
        confirmDeletionDialog.setId("confirmDeletionDialog");
        productEditDialog.open();
        mainPageProductRepresentation.populateComponents(product);
        productInDetailsRepresentation.populateComponents(product);
    }

    private void setContent(Tab selectedTab) {
        productEditDialog.removeAll();
        VerticalLayout verticalLayout = new VerticalLayout();
        if (selectedTab.equals(productAsOnMainPage)) {
            this.product = productInDetailsRepresentation.getProduct();
            mainPageProductRepresentation.populateComponents(product);
            verticalLayout.add(mainPageProductRepresentation);
        } else if (selectedTab.equals(productInDetails)) {
            this.product = mainPageProductRepresentation.getProduct();
            productInDetailsRepresentation.populateComponents(product);
            verticalLayout.add(productInDetailsRepresentation);
        }
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        productEditDialog.add(verticalLayout);
    }

    private static VerticalLayout createDialogLayout(MainPageProductRepresentation mainPageProductRepresentation) {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(true);
        verticalLayout.setSpacing(true);
        verticalLayout.getStyle().set("max-width", "100%");
        verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        verticalLayout.add(mainPageProductRepresentation);
        return verticalLayout;
    }

}
