package com.vsftam.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Basket {

    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    Map<Product, Integer> content = new HashMap<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Map<Product, Integer> getContent() {
        return content;
    }

    public void addProduct(Product product) {
        Integer quantity = content.containsKey(product) ? content.get(product) : 0;
        content.put(product, ++quantity);
    }

    public void removeProduct(Product product) {
        if(content.containsKey(product)) {
            Integer quantity = content.get(product);
            if(quantity == 1)
                content.remove(product);
            else
                content.put(product, --quantity);
        }
    }
}
