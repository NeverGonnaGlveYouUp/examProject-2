package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class Checkboxes extends CheckboxGroup<String>{

    private final String[] labels;
    private final String[] descriptions;

    public Checkboxes(String name, String[] labels, String[] descriptions, String... themeNames) {
        super(name);
        this.labels = Arrays.stream(labels).sorted().toArray(_ -> labels);
        this.descriptions = Arrays.stream(descriptions).sorted().toArray(_ -> descriptions);
//        this.labels = labels;
//        this.descriptions = descriptions;
        if (descriptions.length > 0){
            createCheckboxGroupWithDescriptions(themeNames);
        } else {
            createCheckboxGroup();
        }
    }

    private void createCheckboxGroupWithDescriptions(String... themeNames) {
        createCheckboxGroup(themeNames);
        this.setRenderer(new ComponentRenderer<>(this::renderLabelWithDescription));
    }

    private Component renderLabelWithDescription(String item) {
        Span primary = new Span(item);

        Span secondary = new Span(descriptions[ArrayUtils.indexOf(labels, item)]);
        secondary.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        Layout layout = new Layout(primary, secondary);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    private void createCheckboxGroup(String... themeNames) {
        this.addThemeNames(themeNames);
        this.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        this.setItems(labels);

        this.getChildren().forEach(component -> {
            for (String themeName : themeNames) {
                component.getElement().getThemeList().add(themeName);
            }
        });
    }

}
