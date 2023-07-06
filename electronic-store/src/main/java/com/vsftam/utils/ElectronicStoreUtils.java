package com.vsftam.utils;

import java.util.HashMap;
import java.util.Map;

public class ElectronicStoreUtils {
        public static Map<String,DiscountDeal> discountDeals = new HashMap<>();
        static {
                TenPercentOffDiscount a = new TenPercentOffDiscount();
                BuyOneGetFiftyPercentOffSecondDiscount b = new BuyOneGetFiftyPercentOffSecondDiscount();
                discountDeals.put(a.name(), a);
                discountDeals.put(b.name(), b);
        }

}
