package ru.tusur.ShaurmaWebSiteProject.backend.service;

import jakarta.annotation.PostConstruct;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.PromotionRepo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    PromotionRepo promotionRepo;

    @Autowired
    BranchRepo branchRepo;

    private Map<Branch, Set<String>> branchAddressMap = new HashMap<>();

    //todo call this if branch is added or removed
    @PostConstruct
    public void createBranchAddressMap(){
        branchAddressMap.clear();
        branchRepo.findAll().forEach(branch -> {
            branchAddressMap.put(branch, Arrays.stream(branch.getDeliveryStreets().split(";")).collect(Collectors.toSet()));
        });
    }

    public BigDecimal getDeliveryPrice(Order order){
        AtomicReference<BigDecimal> deliveryValue = new AtomicReference<>(new BigDecimal(BigInteger.ZERO));
        Set<OrderContent> orderContents = order.getOrderContents();
        orderContents.forEach(orderContent -> {
            if (!branchAddressMap.get(orderContent.getBranch()).contains(order.getTargetAddress())){
                BigDecimal delivery = deliveryValue.get();
                delivery = delivery.add(orderContent.getProduct().getPrice().multiply(BigDecimal.valueOf(0.1)));
                deliveryValue.set(delivery);
            }
        });
        return deliveryValue.get();
    }

    public Pair<BigDecimal, Boolean> applyPromoCode(String pCode) {
        List<Promotion> promotions = promotionRepo.findByCondition(pCode);
        AtomicReference<BigDecimal> discountValue = new AtomicReference<>(new BigDecimal(BigInteger.ZERO));
        AtomicReference<Boolean> deliveryIsFree = new AtomicReference<>(Boolean.FALSE);
        promotions.forEach(promotion -> {
            BigDecimal discount = discountValue.get();
            Boolean delivery = deliveryIsFree.get();
            if(promotion.getPromotionType() == PromotionType.CONSTANT_DISCOUNT_BY_CODE && pCode.equals(promotion.getCondition())){
                discount = discount.add(promotion.getPromotionEffect());
            } else if (promotion.getPromotionType() == PromotionType.FREE_DELIVERY_BY_CODE && pCode.equals(promotion.getCondition())) {
                delivery = Boolean.TRUE;
            }
            discountValue.set(discount);
            deliveryIsFree.set(delivery);
        });
        return new Pair<>(discountValue.get(), deliveryIsFree.get());
    }

}
