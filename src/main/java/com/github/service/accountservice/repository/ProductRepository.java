package com.github.service.accountservice.repository;

import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.exceptions.AccountException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(rollbackOn = AccountException.class)
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByOrderByIdAsc();

    @Query("SELECT p FROM Product p WHERE p.productCount>0")
    List<Product> findAllAvailableByOrderByIdAsc();
}
