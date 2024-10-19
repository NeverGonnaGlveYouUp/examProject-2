package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import ru.tusur.ShaurmaWebSiteProject.ui.themes.InputTheme;

import java.util.Optional;

public class PriceRange extends CustomField {

    public PriceRange(String label) {
        setLabel(label);

        TextField minimum = new TextField();
        minimum.addThemeName(InputTheme.OUTLINE);
        minimum.setAriaLabel("Минимальная цена");
        minimum.setPlaceholder("Минимум");
        minimum.setTooltipText("Минимальная цена");

        TextField maximum = new TextField();
        maximum.addThemeName(InputTheme.OUTLINE);
        maximum.setAriaLabel("Максимальная цена");
        maximum.setPlaceholder("Максимум");
        maximum.setTooltipText("Максимальная цена");

        add(new InputGroup(minimum, maximum));
    }

    @Override
    protected Object generateModelValue() {
        return null;
    }

    @Override
    protected void setPresentationValue(Object o) {

    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener valueChangeListener) {
        return null;
    }

    @Override
    public Optional getOptionalValue() {
        return super.getOptionalValue();
    }

    @Override
    public void clear() {
        super.clear();
    }
}