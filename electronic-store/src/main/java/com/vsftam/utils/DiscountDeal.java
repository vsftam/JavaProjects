package com.vsftam.utils;

import com.vsftam.persistence.Product;

public interface DiscountDeal {

    String TenPercentOffDiscountName = "10% off";
    String BuyOneGetFiftyPercentOffSecondDiscountName = "Buy 1 get 50% off the second";

    String name();
    Double calculateDiscount(Product product, Integer quantity);
}

