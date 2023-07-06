package com.vsftam.controller;

import com.vsftam.persistence.Product;
import com.vsftam.persistence.ProductRepository;
import com.vsftam.utils.DiscountDeal;
import com.vsftam.utils.ElectronicStoreUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductRepository productRepository;

    @GetMapping("/")
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    @GetMapping("/name/{productName}")
    public List<Product> findByTitle(@PathVariable String productName) {
        return productRepository.findByName(productName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        productRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@RequestBody Product product, @PathVariable long id) {
        if (product.getId() != id) {
            throw new RuntimeException();
        }
        productRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        return productRepository.save(product);
    }

    @PutMapping("/{id}/{discountDealName}")
    public Product setDiscountDealForProduct(@PathVariable long id, @PathVariable String discountDealName) {
        Product product = productRepository.findById(id).orElseThrow(RuntimeException::new);
        if(ElectronicStoreUtils.discountDeals.containsKey(discountDealName)) {
            DiscountDeal discountDeal =ElectronicStoreUtils.discountDeals.get(discountDealName);
            product.setDiscountDeal(discountDeal);
            return productRepository.save(product);
        }
        else
            return product;
    }
}
