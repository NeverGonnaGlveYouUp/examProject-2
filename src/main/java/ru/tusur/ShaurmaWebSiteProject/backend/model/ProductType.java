package ru.tusur.ShaurmaWebSiteProject.backend.model;

import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Getter
public enum ProductType {

    SHAURMA("Шаурма"),
    DONNER_KEBAB("Кебаб");

    final String ruName;

    private static final Map<String, ProductType> MAP;

    static {
        Map<String, ProductType> crimeCategoryMap = Arrays.stream(values())
                .collect(toMap(pt -> pt.ruName, e -> e));
        MAP = Collections.unmodifiableMap(crimeCategoryMap);
    }


    public static Optional<ProductType> of(final String name) {
        return Optional.ofNullable(MAP.get(name));
    }

    ProductType(String ruName){
        this.ruName = ruName;
    }


}
