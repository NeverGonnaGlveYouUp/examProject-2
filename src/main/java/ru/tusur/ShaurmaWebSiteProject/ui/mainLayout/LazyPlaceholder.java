package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import com.vaadin.flow.component.html.Div;

public interface LazyPlaceholder {
    default Div lazyPlaceholder() {
        Div div = new Div();
        div.setText("Not Loaded");
        return div;
    }
}
