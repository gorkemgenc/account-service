package com.github.service.accountservice.service;

import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.contracts.IProductService;
import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.repository.ProductRepository;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.models.ProductDto;
import com.github.service.accountservice.validator.IValidator;
import com.github.service.accountservice.validator.ValidatorImp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @TestConfiguration
    static class ProductServiceImpTestContextConfiguration {

        @Bean
        public IProductService productService() {
            return new ProductServiceImp();
        }

        @Bean
        public IValidator validator() {
            return new ValidatorImp();
        }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }
    @Autowired
    private IProductService productService;

    @MockBean
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;

    @Before
    public void setUp(){

        product1 = new Product("Name1", new BigDecimal(100), 10);
        product1.setId(1);
        product2 = new Product("Name2", new BigDecimal(200), 0);
        product2.setId(2);
        product3 = new Product("Name3", new BigDecimal(200), 0);
        product3.setId(3);

        Mockito.when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        Mockito.when(productRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(product1, product2));

        Mockito.when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        Mockito.when(productRepository.findById(100)).thenReturn(Optional.empty());

        Mockito.when(productRepository.save(product1)).thenReturn(product1);
        Mockito.when(productRepository.save(product2)).thenReturn(product2);
        Mockito.when(productRepository.save(product3)).thenReturn(product3);
    }

    @Test
    public void testFindAll_Success() throws AccountException {

        List<ProductDto> productList = productService.findAll();
        assertNotNull(productList);
        assertTrue(productList.get(0).getCount().equals(product1.getProductCount()));
        assertTrue(productList.get(1).getCount().equals(product2.getProductCount()));
        assertTrue(productList.get(0).getName().equals(product1.getName()));
        assertTrue(productList.get(1).getName().equals(product2.getName()));
        assertTrue(productList.get(0).getPrice().equals(product1.getPrice()));
        assertTrue(productList.get(1).getPrice().equals(product2.getPrice()));
        assertTrue(productList.get(0).getId().equals(product1.getId()));
        assertTrue(productList.get(1).getId().equals(product2.getId()));
    }

    @Test
    public void testCreateProduct_Success() throws AccountException {

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(product3);
        ProductCreate create = new ProductCreate(product3.getName(), product3.getPrice(), product3.getProductCount());
        ProductDto product = productService.createProduct(create);
        assertTrue(product.getId().equals(product3.getId()));
        assertTrue(product.getCount().equals(product3.getProductCount()));
        assertTrue(product.getName().equals(product3.getName()));
    }

    @Test
    public void testDeleteProduct_Success() throws AccountException{

        ProductDto product = productService.deleteProduct(new ProductDelete(product1.getId()));
        assertTrue(product.getId().equals(product1.getId()));
        assertTrue(product.getCount().equals(product1.getProductCount()));
    }

    @Test
    public void testDeleteProduct_FailIfNotProductFound() throws AccountException{

        try{
            productService.deleteProduct(new ProductDelete(100));
            fail();
        }
        catch(AccountException ex){
            Assert.assertEquals(ex.getMessage(), ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage());
            Assert.assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testDeleteProduct_FailWhenCountZero() throws AccountException{

        try{
            productService.deleteProduct(new ProductDelete(100));
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(), ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage());
            assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testUpdateProduct_Success() throws AccountException{

        ProductDto product = productService.updateProduct(new ProductDto(product1.getId(), "UpdateName", new BigDecimal(200), 150));
        assertTrue(product.getId().equals(product1.getId()));
        assertTrue(product.getCount().equals(150));
        assertTrue(product.getName().equals("UpdateName"));
        assertTrue(product.getPrice().equals(new BigDecimal(200)));
    }

    @Test
    public void testUpdateProduct_FailWhenIdNotFound() throws AccountException{

        try{
            productService.updateProduct(new ProductDto(500, "UpdateName", new BigDecimal(200), 150));
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage());
            assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testUpdateProduct_FailWhenPriceIsNegative() throws AccountException{

        try{
            productService.updateProduct(new ProductDto(product1.getId(), "UpdateName", new BigDecimal(-200), 150));
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"));
            assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testUpdateProduct_FailWhenCountIsNegative() throws AccountException{

        try{
            productService.updateProduct(new ProductDto(product1.getId(), "UpdateName", new BigDecimal(200), -50));
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),String.format(ErrorMessage.SHOULD_NOT_BE_SMALLER_THAN_ZERO.getMessage(), "Product Count"));
            assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
        }
    }
}
