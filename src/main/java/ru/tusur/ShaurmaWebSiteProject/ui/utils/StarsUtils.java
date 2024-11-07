package ru.tusur.ShaurmaWebSiteProject.ui.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class StarsUtils {
    public static Div getStars(double stars) {
        Div div = new Div();
        for (int i = 1; i <= 5; i++) {
            if (i - stars < 1 && i - stars > 0) {
                div.add(createHalfStar());
            } else if (i < stars) {
                div.add(createStar());
            } else {
                div.add(createEmptyStar());
            }
        }
        return div;
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
