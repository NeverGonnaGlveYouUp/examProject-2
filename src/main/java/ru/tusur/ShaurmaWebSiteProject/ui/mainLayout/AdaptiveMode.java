package ru.tusur.ShaurmaWebSiteProject.ui.mainLayout;

import lombok.Getter;

@Getter
public enum AdaptiveMode{
    MOBILE("95%", "2.5"),
    DESKTOP("85%", "15%");

    private final String margin;
    private final String mainBody;

    AdaptiveMode(String mainBody, String margin){
        this.mainBody = mainBody;
        this.margin = margin;
    }
}
