package com.vsftam;

import com.vsftam.utils.DiscountDeal;
import com.vsftam.utils.ProductType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private ProductType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable =false)
    private Double price;

    @Column(nullable = true)
    @JdbcTypeCode(SqlTypes.JSON)
    private DiscountDeal discountDeal;

    public Product() {
        super();
    }

    public Product(ProductType type, String name, Double price) {
        this(type, name, price, null);
    }

    public Product(ProductType type, String name, Double price, DiscountDeal discountDeal) {
        super();
        this.type = type;
        this.name = name;
        this.price = price;
        this.discountDeal = discountDeal;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public DiscountDeal  getDiscountDeal() {
        return discountDeal;
    }

    public void setDiscountDeal(DiscountDeal discountDeal) {
        this.discountDeal = discountDeal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (id != other.id)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", type=" + type.name() + ", name=" + name + ", price=" + price + ", discountDeal=" + discountDeal +"]";
    }
}
