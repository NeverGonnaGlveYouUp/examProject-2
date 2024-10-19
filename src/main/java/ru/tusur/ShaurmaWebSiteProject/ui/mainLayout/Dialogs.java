package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.Style;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.templates.AdminPanelGrid;
import java.util.Set;
import java.util.stream.Collectors;

public interface Dialogs {

    default Dialog confirmDeletionDialog(Product product, ProductService productService) {
        Dialog dialog = new Dialog();
        Button cancel = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        Button accept = new Button(new Span("Удалить"), event -> {
            productService.delete(product);
            dialog.close();
            dialog.setHeaderTitle("DELL");
        });
        accept.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        accept.getStyle().set("margin-inline-end", "auto");
        accept.getStyle().setAlignSelf(Style.AlignSelf.FLEX_START);
        accept.setId("acceptButton");
        dialog.setHeaderTitle("Подтверждение удаления");
        dialog.getHeader().add(cancel);
        dialog.getFooter().add(accept);
        dialog.add(new Span("Вы уверены что хотите удалить товар с именем \"" + product.getName() + "\"?"));
        return dialog;
    }

    default Dialog selectProductTypeDialog(AdminPanelGrid grid, Set<ProductTypeEntity> productTypeEntitiesToOpen) {
        Dialog dialog = new Dialog();
        Button cancel = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        VerticalLayout verticalLayout = new VerticalLayout();
        productTypeEntitiesToOpen.forEach(productType -> {
            Span span = new Span(productType.getName());
            span.addClickListener(event -> {

                Set<String> openTabs = grid.getTabSheet().getChildren()
                        .filter(component -> component instanceof Tabs)
                        .map(component -> (Tabs) component)
                        .flatMap(Component::getChildren)
                        .map(component -> (Tab) component)
                        .map(Tab::getLabel)
                        .collect(Collectors.toSet());

                if(openTabs.contains(productType.getName())) return; //todo notification

                Tab tab = grid.getTabSheet().add(
                        productType.getName(),
                        grid.createGridForTab(productType)
                );

                Button close = new Button("X", clickEvent -> grid.getTabSheet().remove(tab));

                close.getElement().getThemeList().add("badge small contrast");
                close.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
                close.setVisible(false);
                tab.getElement().addEventListener("mouseover", mouseover -> close.setVisible(true));
                tab.getElement().addEventListener("mouseout", mouseover -> close.setVisible(false));
                tab.addComponentAtIndex(1, close);

//                dialog.close();
            });
            verticalLayout.add(span);
        });
        Scroller scroller = new Scroller(verticalLayout);
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        scroller.getStyle()
                .set("border-bottom", "1px solid var(--lumo-contrast-20pct)")
                .set("padding", "var(--lumo-space-m)");
        dialog.add(scroller);
        dialog.getHeader().add(cancel);
        dialog.setHeaderTitle("Выберете какой тип продуктов открыть.");
        return dialog;
    }

    //todo unsaved changes
}
