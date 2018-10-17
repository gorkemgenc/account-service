package com.github.service.accountservice.service;

import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.repository.TransactionTypeRepository;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.contracts.IProductService;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.repository.AccountRepository;
import com.github.service.accountservice.repository.ProductRepository;
import com.github.service.accountservice.repository.TransactionRepository;
import com.github.service.accountservice.service.contracts.IStoreService;
import com.github.service.accountservice.service.models.ProductDto;
import com.github.service.accountservice.service.models.TransactionDto;
import com.github.service.accountservice.validator.IValidator;
import com.github.service.accountservice.validator.ValidatorImp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
public class StoreServiceTest {

    @TestConfiguration
    static class StoreServiceImpTestContextConfiguration{


        @Bean
        public IStoreService storeService() {return new StoreServiceImp();}

        @Bean
        public IAccountService accountService() { return new AccountServiceImp();}

        @Bean
        public IProductService productService() { return new ProductServiceImp(); }

        @Bean
        public IValidator validator() {return new ValidatorImp(); }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Autowired
    private IStoreService storeService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IProductService productService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    TransactionTypeRepository transactionTypeRepository;

    @MockBean
    TransactionRepository transactionRepository;

    @MockBean
    private ProductRepository productRepository;

    private Product product1;
    private Product product2;
    private Product product3;
    private Account account1;
    private Account account2;

    private ModelMapper modelMapper = new ModelMapper();

    @Before
    public void setUp(){
        account1 = new Account();
        account1.setId(1);
        account1.setBalance(new BigDecimal(100));
        account2 = new Account();
        account2.setId(2);

        product1 = new Product("Name1", new BigDecimal(100), 100);
        product1.setId(1);
        product2 = new Product("Name2", new BigDecimal(50), 50);
        product2.setId(2);
        product3 = new Product("Name3", new BigDecimal(100), 0);
        product3.setId(3);

        Mockito.when(accountRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(account1, account2));
        Mockito.when(productRepository.findAllAvailableByOrderByIdAsc()).thenReturn(Arrays.asList(product1, product2));
        Mockito.when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        Mockito.when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findById(100)).thenReturn(Optional.empty());

        Mockito.when(accountRepository.save(account1)).thenReturn(account1);
        Mockito.when(accountRepository.save(account2)).thenReturn(account2);

        Mockito.when(productRepository.save(product1)).thenReturn(product1);
        Mockito.when(productRepository.getOne(product1.getId())).thenReturn(product1);
    }

    @Test
    public void testFindAllAvailable_Success() throws AccountException {

        List<ProductDto> availableProducts = storeService.listAllAvailableProducts();
        assertNotNull(availableProducts);
        assertTrue(availableProducts.get(0).getCount().equals(product1.getProductCount()));
        assertTrue(availableProducts.get(1).getCount().equals(product2.getProductCount()));
        assertTrue(availableProducts.get(0).getName().equals(product1.getName()));
        assertTrue(availableProducts.get(1).getName().equals(product2.getName()));
        assertTrue(availableProducts.get(0).getPrice().equals(product1.getPrice()));
        assertTrue(availableProducts.get(1).getPrice().equals(product2.getPrice()));
        assertTrue(availableProducts.get(0).getId().equals(product1.getId()));
        assertTrue(availableProducts.get(1).getId().equals(product2.getId()));
    }

    @Test
    public void testBuy_Success() throws AccountException{

        int totalCount = product1.getProductCount();
        TransactionDto transaction = storeService.buyProduct(account1.getId(),product1.getId());
        assertNotNull(transaction);
        int finalCount = product1.getProductCount();
        assertEquals(totalCount-1, finalCount);
        assertEquals(transaction.getAccountId(),account1.getId());
        assertTrue(transaction.getProductId().equals(product1.getId()));
        assertTrue(transaction.getAmount().equals(product1.getPrice()));
    }

    @Test
    public void testBuy_FailWhenAccountNotFound() throws AccountException{

        try{
            storeService.buyProduct(100,product1.getId());
            fail();
        }
        catch(AccountException ex){
            Assert.assertEquals(ex.getMessage(), ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
            Assert.assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testBuy_FailWhenProductNotFound() throws AccountException{

        try{
            storeService.buyProduct(account1.getId(),100);
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(), ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage());
            assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testBuy_FailWhenProductCountIsZero() throws AccountException{

        try{
            storeService.buyProduct(account1.getId(),product3.getId());
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage());
            assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
        }
    }
}
