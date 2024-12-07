package ru.tusur.ShaurmaWebSiteProject.backend.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeliveryType {

    PICK_UP("Самовывоз"),
    COURIER("Доставка");

    private final String string;
}
