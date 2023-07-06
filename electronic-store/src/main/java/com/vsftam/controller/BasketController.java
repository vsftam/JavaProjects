package com.vsftam.controller;

import com.vsftam.persistence.Basket;
import com.vsftam.persistence.Product;
import com.vsftam.utils.DiscountDeal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/baskets")
public class BasketController {

    @PutMapping("/add/{id}")
    public Basket addProduct(@RequestBody Basket basket, @RequestBody Product product, @PathVariable long id) {
        if (basket.getId() != id) {
            throw new RuntimeException();
        }

        basket.addProduct(product);
        return basket;
    }

    @PutMapping("/remove/{id}")
    public Basket removeProduct(@RequestBody Basket basket, @RequestBody Product product, @PathVariable long id) {
        if (basket.getId() != id) {
            throw new RuntimeException();
        }

        basket.removeProduct(product);
        return basket;
    }

    @GetMapping("receipt/{id}")
    public String getReceipt(@PathVariable Long id, @RequestBody Basket basket) {
        if (basket.getId() != id) {
            throw new RuntimeException();
        }
        StringBuffer receipt = new StringBuffer("Here is the receipt\n\n");

        for(Map.Entry<Product, Integer> entry: basket.getContent().entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            DiscountDeal discountDeal = product.getDiscountDeal();

            receipt.append(product.getType().name() +": "+ product.getName() + " - Qty(" + quantity + ") ");
            Double discount = 0.0;
            if( discountDeal != null ) {
                discount = discountDeal.calculateDiscount(product, quantity);
                receipt.append("Deal (" + discountDeal.name() + " with " +discount + " discount) ");
            }

            Double price = product.getPrice() * quantity - discount;
            receipt.append(" - Price: "+ price +"\n");
        }
        return receipt.toString();
    }
}
