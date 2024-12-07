package ru.tusur.ShaurmaWebSiteProject.ui.utils;

public class Pair<F, S> {
    public F getA() {
        return A;
    }

    public S getB() {
        return B;
    }

    public final F A;
    public final S B;

    public Pair(F a, S b) {
        this.A = a;
        this.B = b;
    }
}