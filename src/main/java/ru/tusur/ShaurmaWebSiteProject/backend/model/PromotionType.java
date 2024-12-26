package ru.tusur.ShaurmaWebSiteProject.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PromotionType {
    CONSTANT_DISCOUNT_BY_CODE("Константная скидка по коду"),
    PERCENT_DISCOUNT_BY_CODE("Процентная скидка по коду"),
    GIFT("Подарок"),
    FREE_DELIVERY_BY_CODE("Бесплатная доставка по коду"),
    FREE_DELIVERY("Бесплатная доставка");

    private final String description;

    @Override
    public String toString() {
        return description;
    }
}

