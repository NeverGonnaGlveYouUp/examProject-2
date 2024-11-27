package ru.tusur.ShaurmaWebSiteProject.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PromotionType {
    CONSTANT_DISCOUNT_BY_CODE(),
    PERCENT_DISCOUNT_BY_CODE(),
    GIFT(),
    FREE_DELIVERY_BY_CODE(),
    FREE_DELIVERY();
}

