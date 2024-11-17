package ru.tusur.ShaurmaWebSiteProject.backend.service;


import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ShopCartService {

    //TODO implement save or cart to db

    private static final long SESSION_TTL = 10000L;
    Map<String, OrderedHashSet<OrderContent>> shopCartPool = new ConcurrentHashMap<>();
    Map<String, Long> shopCartTTLPool = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = SESSION_TTL, initialDelay = SESSION_TTL)
    public void scheduled() {
        System.out.println("scheduled executed2 " + shopCartPool.size() + " " + shopCartTTLPool.size());
        shopCartTTLPool.forEach((string, aLong) -> {
            System.out.println("scheduled executed1");
            if (System.currentTimeMillis() - aLong > SESSION_TTL) {
                shopCartPool.remove(string);
                shopCartTTLPool.remove(string);
                System.out.println("scheduled executed");
            }
        });
    }

    public void changeOrderContentNum(@NonNull String session, OrderContent orderContent) {
        shopCartPool
                .get(session)
                .stream()
                .filter(orderContentToIncrement -> orderContentToIncrement.equals(orderContent))
                .findFirst()
                .orElseThrow()
                .setNum(orderContent.getNum());
    }

    public void addOrderContent(@NonNull String session, OrderContent orderContent) {
        if (!shopCartPool.containsKey(session)) {
            shopCartPool.put(session, new OrderedHashSet<>());
        }
        shopCartTTLPool.put(session, System.currentTimeMillis());
        shopCartPool.get(session).add(orderContent);
    }

    public void removeOrderContent(@NonNull String session, OrderContent orderContent) {
        shopCartPool
                .get(session)
                .remove(orderContent);
    }

    public OrderedHashSet<OrderContent> getAllOrderContent(@NonNull String session) {
        if (!shopCartPool.containsKey(session)) {
            shopCartPool.put(session, new OrderedHashSet<OrderContent>());
            shopCartTTLPool.put(session, System.currentTimeMillis());
        }
        return shopCartPool.get(session);
    }

}
