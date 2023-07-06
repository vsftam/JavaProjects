package com.vsftam.utils;

import com.vsftam.persistence.Product;

public class TenPercentOffDiscount implements DiscountDeal {
    public String name() {
        return TenPercentOffDiscountName;
    }

    public Double calculateDiscount(Product product, Integer quantity) {
        return product.getPrice() * quantity * 0.1;
    }
}