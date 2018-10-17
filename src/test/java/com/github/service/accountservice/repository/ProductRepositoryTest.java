package com.github.service.accountservice.repository;

import com.github.service.accountservice.entities.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @Before
    public void before(){

        product1 = new Product("Name1", new BigDecimal(500), 100);
        product2 = new Product("Name2", new BigDecimal(1000), 200);
        product3 = new Product("Name3", new BigDecimal(2000), 0);

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.persist(product3);
        entityManager.flush();
    }

    @Test
    public void testCreateProduct(){

        long totalCount = productRepository.count();
        Product testProduct = new Product("Name4", new BigDecimal(5000), 100);
        productRepository.save(testProduct);
        long finalCount = productRepository.count();
        Optional<Product> optionalProduct = productRepository.findById(testProduct.getId());
        assertTrue(optionalProduct.isPresent());
        Product addedProduct = optionalProduct.get();
        assertEquals(totalCount+1, finalCount);
        assertTrue(addedProduct.getName().equals(testProduct.getName()));
        assertTrue(addedProduct.getPrice().equals(testProduct.getPrice()));
        assertEquals(addedProduct.getProductCount(),testProduct.getProductCount());
    }

    @Test
    public void testFindById(){

        Optional<Product> optionalProduct = productRepository.findById(product1.getId());
        assertTrue(optionalProduct.isPresent());
        Product addedProduct = optionalProduct.get();
        assertEquals(addedProduct.getId(), product1.getId());
        assertTrue(addedProduct.getName().equals(product1.getName()));
        assertTrue(addedProduct.getPrice().equals(product1.getPrice()));
        assertEquals(addedProduct.getProductCount(), product1.getProductCount());
        assertTrue(addedProduct.getLastUpdated().equals(product1.getLastUpdated()));
        assertTrue(addedProduct.getCreatedDate().equals(product1.getCreatedDate()));
    }

    @Test
    public void testFindById_NotFound() {

        Optional<Product> optionalProduct = productRepository.findById(100);
        assertTrue(!optionalProduct.isPresent());
    }

    @Test
    public void testList() {

        List<Product> productList = productRepository.findAll();
        assertTrue(productList.size() > 0);
        assertTrue(productList.size() == 3);
        assertEquals(productList.get(0).getId(), product1.getId());
        assertEquals(productList.get(1).getId(), product2.getId());
        assertEquals(productList.get(2).getId(), product3.getId());
    }

    @Test
    public void testDelete_Success(){

        Optional<Product> optionalProduct = productRepository.findById(product1.getId());
        assertTrue(optionalProduct.isPresent());
        Product product = optionalProduct.get();
        product.setProductCount(product.getProductCount()-1);
        Product updatedProduct = productRepository.save(product);
        assertEquals(updatedProduct.getProductCount(), 99);
    }

    @Test
    public void testDelete_FailWhenIdNotFound(){

        Optional<Product> optionalProduct = productRepository.findById(100);
        assertTrue(!optionalProduct.isPresent());
    }

    @Test
    public void testDelete_FailWhenCountIsZero(){

        Optional<Product> optionalProduct = productRepository.findById(product3.getId());
        assertTrue(optionalProduct.isPresent());
        Product product = optionalProduct.get();
        product.setProductCount(product.getProductCount()-1);
        productRepository.save(product);
        try{
            entityManager.flush();
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Product count cannot be negative"));
        }
    }

    @Test
    public void testUpdate_Success(){

        Optional<Product> optionalProduct = productRepository.findById(product1.getId());
        assertTrue(optionalProduct.isPresent());
        Product product = optionalProduct.get();
        product.setName("updatedName");
        product.setPrice(new BigDecimal(10));
        product.setProductCount(10);
        Product updatedProduct = productRepository.save(product);
        assertEquals(updatedProduct.getId(), product1.getId());
        assertTrue(updatedProduct.getName().equals("updatedName"));
        assertEquals(updatedProduct.getProductCount(), 10);
        assertTrue(updatedProduct.getPrice().equals(new BigDecimal(10)));
    }

    @Test
    public void testUpdate_FailWhenIdNotFound(){

        Optional<Product> optionalProduct = productRepository.findById(100);
        assertTrue(!optionalProduct.isPresent());
    }

    @Test
    public void testUpdate_FailWhenNameIsBlank(){

        Optional<Product> optionalProduct = productRepository.findById(product1.getId());
        assertTrue(optionalProduct.isPresent());
        Product product = optionalProduct.get();
        product.setName("");
        productRepository.save(product);
        try{
            entityManager.flush();
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
        }
    }

    @Test
    public void testUpdate_FailWhenPriceSmallerThanZero(){

        Optional<Product> optionalProduct = productRepository.findById(product1.getId());
        assertTrue(optionalProduct.isPresent());
        Product product = optionalProduct.get();
        product.setPrice(new BigDecimal(-100));
        productRepository.save(product);
        try{
            entityManager.flush();
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Price cannot be negative"));
        }
    }

    @Test
    public void testUpdate_FailWhenCountSmallerThanZero(){

        Optional<Product> optionalProduct = productRepository.findById(product1.getId());
        assertTrue(optionalProduct.isPresent());
        Product product = optionalProduct.get();
        product.setProductCount(-100);
        productRepository.save(product);
        try{
            entityManager.flush();
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Product count cannot be negative"));
        }
    }
}
