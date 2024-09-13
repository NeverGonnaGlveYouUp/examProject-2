package ru.tusur.ShaurmaWebSiteProject.backend.model;

public enum OrderState {
    ACCEPTED("Заказ принят в очередь"),
    ASSEMBLING("Собираем заказ"),
    DELIVERING("Курьер в пути"),
    EXPECTING_PICK_UP("Заказ готов к выдаче"),
    PICKED_UP("Заказ выдан"),
    DELIVERED("Заказ доставлен"),
    CANCELLED_BY_USER("Вы отменили заказ"),
    CANCELLED_BY_BRANCH("Филиал не может выполнить заказ");

    private String content;

    private OrderState(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
