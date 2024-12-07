package ru.tusur.ShaurmaWebSiteProject.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.vaadin.lineawesome.LineAwesomeIcon;

@RequiredArgsConstructor
@Getter
public enum PaymentType {

//    CARD("Банковской картой при получении", "При помощи банковской карты Вы можете оплатить ваш заказ при получении товара. Данный способ расчета не влияет на стоимость товара - комиссия при оплате заказа картой не взимается.", LineAwesomeIcon.CREDIT_CARD_SOLID.getSvgName()),
//    CARD_ONLINE("Онлайн-оплата", "Онлайн-оплата осуществляется с использованием ЮKassa. Оплата происходит через Процессинговый центр ЮKassa.", LineAwesomeIcon.CREDIT_CARD_SOLID.getSvgName()),
    CASH("Наличными", "Оплата принимается в российских рублях в наших магазинах или курьеру при получении товара после проверки комплектности и внешнего вида. Предоставляется кассовый чек.", LineAwesomeIcon.COINS_SOLID.getSvgName()),
    SBP_ONLINE("Онлайн-оплата через СБП", "Система быстрых платежей (СБП) - сервис платежной системы Банка России, позволяющий физическим лицам производить оплату за товар/услуги с помощью любого банка-участника СБП. Безопасность переводов обеспечивается на стороне всех банков-участников СБП: банков, Банка России и НСПК с использованием современных систем защиты. СБП соответствует всем стандартам информационной безопасности.", "https://ir-2.ozone.ru/graphics/payments/types/fast_pay/icon_v2.svg"),
    SBP("СБП при получении", "Система быстрых платежей (СБП) - сервис платежной системы Банка России, позволяющий физическим лицам производить оплату за товар/услуги с помощью любого банка-участника СБП. Безопасность переводов обеспечивается на стороне всех банков-участников СБП: банков, Банка России и НСПК с использованием современных систем защиты. СБП соответствует всем стандартам информационной безопасности.", "https://ir-2.ozone.ru/graphics/payments/types/fast_pay/icon_v2.svg"),
    MASTERCARD_VISA("Visa MasterCard", "", "");
    private final String name;
    private final String details;
    private final String url;
}
