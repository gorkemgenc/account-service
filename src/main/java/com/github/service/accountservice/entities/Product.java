package com.github.service.accountservice.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Getter
@Setter
@Entity
@Table(name="product")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_name", unique = true)
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @Min(value = 0, message = "Price cannot be negative")
    @Column(name = "price",nullable = false)
    @NotNull(message = "Price must be provided")
    private BigDecimal price;

    @Min(value = 0, message = "Product count cannot be negative")
    @NotNull (message = "Product count must be provided")
    @Column(name = "product_count")
    private int productCount;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    public Product(){}

    public Product(String name, BigDecimal price, Integer productCount){
        this.name = name;
        this.price = price;
        this.productCount = productCount;
        createdDate = new Date();
        lastUpdated = new Date();
    }
}
