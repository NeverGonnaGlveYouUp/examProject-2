package ru.tusur.ShaurmaWebSiteProject.ui.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;

public class StarsUtils {


    public static Layout getStars(double stars) {
        Layout layout = new Layout();
        layout.setFlexDirection(Layout.FlexDirection.ROW);
        layout.setGap(Layout.Gap.XSMALL);
        for (int i = 1; i <= 5; i++) {
            if (i - stars < 1 && i - stars > 0) {
                layout.add(createHalfStar());
            } else if (i < stars) {
                layout.add(createStar());
            } else {
                layout.add(createEmptyStar());
            }
        }
        return layout;
    }

    public static Layout getStars(int stars) {
        Layout layout = new Layout();
        layout.setFlexDirection(Layout.FlexDirection.ROW);
        layout.setGap(Layout.Gap.XSMALL);
        for (int i = 0; i < 5; i++) {
            if (i < stars) {
                layout.add(createStar());
            } else {
                layout.add(createEmptyStar());
            }
        }
        return layout;
    }

    private static Component createStar() {
        SvgIcon star = LineAwesomeIcon.STAR_SOLID.create();
        star.addClassNames(LumoUtility.IconSize.SMALL);
        return star;
    }

    private static Component createHalfStar() {
        SvgIcon star = LineAwesomeIcon.STAR_HALF_SOLID.create();
        star.addClassNames(LumoUtility.IconSize.SMALL);
        return star;
    }

    private static Component createEmptyStar() {
        SvgIcon star = LineAwesomeIcon.STAR.create();
        star.addClassNames(LumoUtility.IconSize.SMALL);
        return star;
    }
}
