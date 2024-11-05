package ru.tusur.ShaurmaWebSiteProject.backend.model;

import lombok.Getter;

@Getter
public enum LikeState {
    LIKE(1),
    NEUTRAL(0),
    DISLIKE(-1);
    private int anInt;

    LikeState(int i) {}
}
