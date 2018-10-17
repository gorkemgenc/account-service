package com.github.service.accountservice.service;

import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.repository.AccountRepository;
import com.github.service.accountservice.service.models.AccountDto;
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
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class AccountServiceTest {

    @TestConfiguration
    static class AccountServiceImpTestContextConfiguration{

        @Bean
        public IAccountService accountService(){
            return new AccountServiceImp();
        }

        @Bean
        public IValidator validator(){
            return new ValidatorImp();
        }

        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Autowired
    private IAccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    private Account account1;
    private Account account2;

    @Before
    public void setUp(){

        account1 = new Account();
        account1.setId(1);
        account2 = new Account();
        account2.setId(2);

        Mockito.when(accountRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(account1, account2));

        Mockito.when(accountRepository.findById(account1.getId())).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findById(100)).thenReturn(Optional.empty());

        Mockito.when(accountRepository.save(account1)).thenReturn(account1);
        Mockito.when(accountRepository.save(account2)).thenReturn(account2);
    }

    @Test
    public void testFindById_Success() throws AccountException {

        AccountDto account = accountService.findById(account1.getId());
        assertNotNull(account);
        assertTrue(account.getBalance().equals(account1.getBalance()));
        assertEquals(account.getId(), account1.getId());
    }

    @Test
    public void testFindById_Null() throws AccountException{

        try{
            AccountDto  accountDto = accountService.findById(100);
            assertNull(accountDto);
            fail();
        }catch(AccountException ex){
            Assert.assertEquals(ex.getMessage(), ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
            Assert.assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testCreateAccount_Success() throws AccountException {

        Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenReturn(account1);
        AccountDto account = accountService.createAccount();
        assertEquals(account1.getId(), account.getId());
        assertTrue(account1.getBalance().equals(account.getBalance()));
    }

    @Test
    public void testUpdateAccountAmount_isDepositSuccess() throws AccountException {

        Account account = accountService.updateAccountAmount(account1.getId(), new BigDecimal(100), TransactionTypes.DEPOSIT);
        assertEquals(account.getId(),account1.getId());
        assertEquals(account.getBalance(),new BigDecimal(100));
    }

    @Test
    public void testUpdateAccountAmount_isWithdrawSuccess() throws AccountException {

        Account account = accountService.updateAccountAmount(account1.getId(), new BigDecimal(100), TransactionTypes.DEPOSIT);
        assertEquals(account.getId(),account1.getId());
        assertEquals(account.getBalance(),new BigDecimal(100));

        account = accountService.updateAccountAmount(account1.getId(), new BigDecimal(50), TransactionTypes.WITHDRAW);
        assertEquals(account.getId(),account1.getId());
        assertEquals(account.getBalance(),new BigDecimal(50));
    }

    @Test
    public void testUpdateAccountAmount_isDepositFailWhenNumberNegative() throws  AccountException{
        try{
            accountService.updateAccountAmount(account1.getId(), new BigDecimal(-100), TransactionTypes.DEPOSIT);
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"));
            assertEquals(ex.getErrorCode(), ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testUpdateAccountAmount_isWithdrawFailWhenNumberNegative() throws AccountException{

        try{
            accountService.updateAccountAmount(account1.getId(), new BigDecimal(-100), TransactionTypes.WITHDRAW);
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"));
            assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
        }
    }

    @Test
    public void testUpdateAccountAmount_isWithdrawFailWhenBalanceNotEnough() throws AccountException{

        try{
            accountService.updateAccountAmount(account1.getId(), new BigDecimal(100), TransactionTypes.WITHDRAW);
            fail();
        }
        catch(AccountException ex){
            assertEquals(ex.getMessage(),ErrorMessage.NO_ENOUGH_BALANCE.getMessage());
            assertEquals(ex.getErrorCode(),ErrorCode.BadRequest.getCode());
        }
    }
}
