package com.github.service.accountservice.service;

import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.contracts.ITransactionService;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.entities.Transaction;
import com.github.service.accountservice.entities.TransactionType;
import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.repository.AccountRepository;
import com.github.service.accountservice.repository.TransactionRepository;
import com.github.service.accountservice.repository.TransactionTypeRepository;
import com.github.service.accountservice.service.models.TransactionDto;
import com.github.service.accountservice.validator.IValidator;
import com.github.service.accountservice.validator.ValidatorImp;
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
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class TransactionServiceTest {

    @TestConfiguration
    static class TransactionServiceImpTestContextConfiguration{


        @Bean
        public ITransactionService transactionService() {return new TransactionServiceImp();}

        @Bean
        public IAccountService accountService() {return new AccountServiceImp();}

        @Bean
        public IValidator<String, String> validator() {return new ValidatorImp(); }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IAccountService accountService;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private TransactionTypeRepository transactionTypeRepository;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;
    private TransactionType type1;
    private TransactionType type2;
    private TransactionType type3;
    private Account account1;
    private Account account2;
    private Product product1;

    @Before
    public void setUp(){

        account1 = new Account();
        account1.setId(1);
        account2 = new Account();
        account2.setId(1);
        account2.setBalance(new BigDecimal(1000));
        type1 = new TransactionType("DEPOSIT");
        type1.setId(1);
        type2 = new TransactionType("WITHDRAW");
        type2.setId(2);
        type3 = new TransactionType("PURCHASE");
        type3.setId(3);
        product1 = new Product("Name1", new BigDecimal(50), 100);

        transaction1 = new Transaction(type1, new BigDecimal(100), account1);
        transaction1.setId(1);
        transaction2 = new Transaction(type2, new BigDecimal(100), account1);
        transaction2.setId(2);
        transaction3 = new Transaction(type3, new BigDecimal(100), account1, product1);
        transaction2.setId(3);

        Mockito.when(transactionTypeRepository.getOne(type1.getId())).thenReturn(type1);
        Mockito.when(transactionTypeRepository.getOne(type2.getId())).thenReturn(type2);
        Mockito.when(transactionTypeRepository.getOne(type3.getId())).thenReturn(type3);

        Mockito.when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.save(account1)).thenReturn(account1);

        Mockito.when(accountRepository.findById(account2.getId())).thenReturn(Optional.of(account2));
        Mockito.when(accountRepository.save(account2)).thenReturn(account2);
    }

   @Test
    public void testCreateTransaction_SuccessIfTypeDEPOSIT() throws AccountException {

       Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction1);
       TransactionDto transaction = transactionService.createTransaction(account1.getId(), new BigDecimal(100), TransactionTypes.DEPOSIT);
       assertTrue(transaction.getAmount().equals(transaction1.getAmount()));
       assertTrue(transaction.getAccountId().equals(transaction1.getAccount().getId()));
   }

    @Test
    public void testCreateTransaction_SuccessIfTypeWITHDRAW() throws AccountException{

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction1);
        TransactionDto transaction = transactionService.createTransaction(account1.getId(), new BigDecimal(100), TransactionTypes.WITHDRAW);
        assertTrue(transaction.getAmount().equals(transaction1.getAmount()));
        assertTrue(transaction.getAccountId().equals(transaction1.getAccount().getId()));
    }

    @Test
    public void testCreateTransaction_SuccessIfTypePURCHASE() throws AccountException{

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction1);
        TransactionDto transaction = transactionService.createTransaction(account1.getId(), new BigDecimal(100), TransactionTypes.PURCHASE);
        assertTrue(transaction.getAmount().equals(transaction1.getAmount()));
        assertTrue(transaction.getAccountId().equals(transaction1.getAccount().getId()));
    }

   @Test
    public void testCreateTransaction_FailWhenAccountNotFound() throws AccountException{

       try{
           TransactionDto transaction = transactionService.createTransaction(100, new BigDecimal(100), TransactionTypes.PURCHASE);
           fail();
       }
       catch(AccountException ex){
           assertEquals(ex.getMessage(),ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
           assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
       }
   }

   @Test
    public void testCreateTransaction_FailWhenAmountIsNull() throws AccountException, ConstraintViolationException{

       try{
           TransactionDto transaction = transactionService.createTransaction(account1.getId(), null, TransactionTypes.PURCHASE);
           fail();
       }
       catch(ConstraintViolationException ex){
           assertTrue(ex.getMessage().contains("must not be null"));
       }
   }

   @Test
    public void testCreateTransaction_FailWhenAmountSmallerThanZero() throws AccountException{

       try{
           TransactionDto transaction = transactionService.createTransaction(account1.getId(), new BigDecimal(-100), TransactionTypes.PURCHASE);
           fail();
       }
       catch(AccountException ex){
           assertEquals(ex.getMessage(), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"));
           assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
       }
   }
}
