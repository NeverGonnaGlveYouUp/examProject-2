package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.dom.Style;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;

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
}
