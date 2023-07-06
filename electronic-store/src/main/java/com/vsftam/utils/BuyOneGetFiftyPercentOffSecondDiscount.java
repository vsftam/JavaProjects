package com.vsftam.utils;

import com.vsftam.persistence.Product;

public class BuyOneGetFiftyPercentOffSecondDiscount implements DiscountDeal {

    public String name() {
        return BuyOneGetFiftyPercentOffSecondDiscountName;
    }

    @Override
    public Double calculateDiscount(Product product, Integer quantity) {
        int discountsToApply = quantity / 2;
        return product.getPrice() * discountsToApply * 0.5;
    }
}
