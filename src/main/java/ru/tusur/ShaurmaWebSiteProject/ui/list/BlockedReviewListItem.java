package ru.tusur.ShaurmaWebSiteProject.ui.list;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;

public class BlockedReviewListItem extends com.vaadin.flow.component.html.ListItem{
    public BlockedReviewListItem(String reason) {
        this.getStyle().set("list-style-type", "none");
        Layout layout = new Layout();
        layout.setWidth("webkit-fill-available");
        layout.setJustifyContent(Layout.JustifyContent.CENTER);
        layout.setAlignItems(Layout.AlignItems.CENTER);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setPadding(LumoUtility.Padding.LARGE);
        SvgIcon icon = LineAwesomeIcon.EXCLAMATION_CIRCLE_SOLID.create();
        Span span = new Span(reason);
        layout.add(icon, span);
        this.add(layout);
    }
}
