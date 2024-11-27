package ru.tusur.ShaurmaWebSiteProject.backend.service;


import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContent;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderContentRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ShopCartService {

    @Autowired
    OrderContentRepo orderContentRepo;

    private static final long SESSION_TTL = 3600000;
    private Map<String, OrderedHashSet<OrderContent>> shopCartPool = new HashMap<>();
    private Map<String, Long> shopCartTTLPool = new HashMap<>();

    @Scheduled(fixedRate = SESSION_TTL / 10, initialDelay = SESSION_TTL)
    public void scheduled() {
        shopCartTTLPool.forEach((string, aLong) -> {
            if (System.currentTimeMillis() - aLong > SESSION_TTL) {
                shopCartPool.remove(string);
                shopCartTTLPool.remove(string);
            }
        });
    }

    public OrderedHashSet<OrderContent> saveCart(@NonNull String session) {
        OrderedHashSet<OrderContent> set = shopCartPool.get(session);
        orderContentRepo.saveAll(set);
        shopCartPool.remove(session);
        shopCartTTLPool.remove(session);
        return set;
    }

    public void changeOrderContentNum(@NonNull String session, OrderContent orderContent) {
        shopCartPool
                .get(session)
                .stream()
                .filter(orderContentToIncrement -> orderContentToIncrement.equals(orderContent))
                .findFirst()
                .orElseThrow()
                .setNum(orderContent.getNum());
        updateCartTTL(session);
    }

    public void addOrderContent(@NonNull String session, OrderContent orderContent) {
        if (!shopCartPool.containsKey(session)) {
            shopCartPool.put(session, new OrderedHashSet<>());
        }
        shopCartTTLPool.put(session, System.currentTimeMillis());
        shopCartPool.get(session).add(orderContent);
    }

    public void removeOrderContent(@NonNull String session, OrderContent orderContent) {
        AtomicInteger index = new AtomicInteger(-1);
        OrderedHashSet<OrderContent> orderContents = shopCartPool.get(session);
        //todo remove that try catch
        try {
            orderContents.forEach(o -> {
                index.addAndGet(1);
                if (o.equals(orderContent)) orderContents.remove(index.get());
            });
        } catch (Exception _) {
        }

        updateCartTTL(session);
    }

    public OrderedHashSet<OrderContent> getAllOrderContent(@NonNull String session) {
        if (!shopCartPool.containsKey(session)) {
            shopCartPool.put(session, new OrderedHashSet<>());
            shopCartTTLPool.put(session, System.currentTimeMillis());
        }
        updateCartTTL(session);
        return shopCartPool.get(session);
    }

    private void updateCartTTL(@NonNull String session) {
        shopCartTTLPool.remove(session);
        shopCartTTLPool.put(session, System.currentTimeMillis());
    }

}
