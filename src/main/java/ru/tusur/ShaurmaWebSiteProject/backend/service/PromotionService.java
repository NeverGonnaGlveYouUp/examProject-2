package ru.tusur.ShaurmaWebSiteProject.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.PromotionRepo;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static ru.tusur.ShaurmaWebSiteProject.backend.model.PromotionType.FREE_DELIVERY;
import static ru.tusur.ShaurmaWebSiteProject.backend.model.PromotionType.FREE_DELIVERY_BY_CODE;

@Service
@Slf4j
public class PromotionService {

    @Autowired
    PromotionRepo promotionRepo;

    @Autowired
    BranchRepo branchRepo;


    private final Map<Branch, Set<String>> branchAddressMap = new HashMap<>();

    //todo call this if branch is added or removed
    @EventListener(value = ApplicationStartedEvent.class)
    public void createBranchAddressMap() {
        branchAddressMap.clear();
        branchRepo.findAll().forEach(branch -> {
            branchAddressMap.put(branch, Arrays.stream(branch.getDeliveryStreets().split(";")).map(String::toLowerCase).map(s -> s.replaceAll("\\s+","")).collect(Collectors.toSet()));
        });
        ;
    }

    public BigDecimal getDeliveryPrice(Order order) {
        AtomicReference<BigDecimal> deliveryValue = new AtomicReference<>(new BigDecimal(BigInteger.ZERO));
        Set<OrderContent> orderContents = order.getOrderContents();
        if (order.getTargetAddress() != null){
            orderContents.forEach(orderContent -> {
                        for (String s : branchAddressMap.get(orderContent.getBranch())) {
                            if (!order.getTargetAddress().toUpperCase().contains(s.toUpperCase())) {
                                BigDecimal delivery = deliveryValue.get();
                                delivery = delivery.add(orderContent.getProduct().getPrice().multiply(BigDecimal.valueOf(0.1)));
                                deliveryValue.set(delivery);
                            } else {
                                deliveryValue.set(BigDecimal.valueOf(0));
                                break;
                            }
                        }
                    }
            );
            order.getPromotions()
                    .stream()
                    .map(Promotion::getPromotionType)
                    .filter(promotionType -> promotionType.equals(FREE_DELIVERY_BY_CODE) || promotionType.equals(FREE_DELIVERY))
                    .forEach(_ -> deliveryValue.set(BigDecimal.valueOf(0)));
        }
        return deliveryValue.get();
    }

    public Pair<BigDecimal, List<Promotion>> applyAndSavePromoCode(String pCode) {
        List<Promotion> promotions = promotionRepo.findByConditionAndHide(pCode, false);
        List<Promotion> promotionsToReturn = new ArrayList<>();
        AtomicReference<BigDecimal> discountValue = new AtomicReference<>(new BigDecimal(BigInteger.ZERO));
        promotions.forEach(promotion -> {
            if (promotion.getPromotionType() == PromotionType.CONSTANT_DISCOUNT_BY_CODE && pCode.equals(promotion.getCondition())) {
                discountValue.set(discountValue.get().subtract(promotion.getPromotionEffect()));
                promotionsToReturn.add(promotion);
            } else if (promotion.getPromotionType() == FREE_DELIVERY_BY_CODE && pCode.equals(promotion.getCondition())) {
                promotionsToReturn.add(promotion);
            }
        });
        return new Pair<>(discountValue.get(), promotionsToReturn);
    }

}
