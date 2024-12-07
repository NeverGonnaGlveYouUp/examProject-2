package ru.tusur.ShaurmaWebSiteProject.ui.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.BigDecimalField;

@Tag("my-big-decimal-field")
@JsModule("./my-big-decimal-field.js")
public class BigDecimalPriceField extends BigDecimalField {}