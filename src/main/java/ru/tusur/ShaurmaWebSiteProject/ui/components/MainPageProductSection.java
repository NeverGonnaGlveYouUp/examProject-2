package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;

import java.util.LinkedList;

@Tag("div")
public class MainPageProductSection extends Component implements HasComponents {

    private LinkedList<Product> productList;
    private H3 title;
    private Div div;

    public MainPageProductSection(LinkedList<Product> productList) {
        this.productList = productList;
        title.setText(productList.getFirst().getProductType().getName());
        //TODO: add "add button" and anchor on MainPageProductRepresentation body to its page
        this.productList.forEach(product -> add(new MainPageProductRepresentation(product)));
        add(title, div);
    }
}
