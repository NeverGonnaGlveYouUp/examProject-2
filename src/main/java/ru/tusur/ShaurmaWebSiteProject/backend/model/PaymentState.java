package ru.tusur.ShaurmaWebSiteProject.backend.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentState {

    PAYMENT_DONE("Оплачен"),
    EXPECTING_PAYMENT("Оплата ожидается"),
    NO_PAYMENT("Не оплачен");

    private final String string;
}
