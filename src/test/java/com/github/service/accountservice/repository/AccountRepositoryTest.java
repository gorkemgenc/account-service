package com.github.service.accountservice.repository;

import com.github.service.accountservice.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AccountRepository accountRepository;

    private Account account1;
    private Account account2;

    @Before
    public void before(){

        account1 = new Account();
        account2 = new Account();

        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.flush();
    }

    @Test
    public void testCreateWallet(){

        long totalCount = accountRepository.count();
        Account testAccount = new Account();
        accountRepository.save(testAccount);
        long finalCount = accountRepository.count();
        assertEquals(totalCount+1, finalCount);
    }

    @Test
    public void testFindById(){

        Optional<Account> account = accountRepository.findById(account1.getId());
        assertTrue(account.isPresent());
        Account addedAccount = account.get();
        assertTrue(addedAccount.getBalance().equals(account1.getBalance()));
        assertTrue(addedAccount.getId().equals(account1.getId()));
    }

    @Test
    public void testFindById_NotFound() {

        Optional<Account> optionalAccount = accountRepository.findById(100);
        assertTrue(!optionalAccount.isPresent());
    }

    @Test
    public void testUpdateBalance_Success(){
        Optional<Account> optionalAccount = accountRepository.findById(account1.getId());
        assertTrue(optionalAccount.isPresent());
        BigDecimal depositAmount = new BigDecimal(100000);
        Account account = optionalAccount.get();
        account.setBalance(depositAmount);
        Account updatedAccount = accountRepository.save(account);
        assertTrue(updatedAccount.getBalance().equals(depositAmount));
    }

    @Test
    public void testUpdateBalance_FailWithNegativeAmount(){

        Optional<Account> optionalAccount = accountRepository.findById(account1.getId());
        assertTrue(optionalAccount.isPresent());
        BigDecimal depositAmount = new BigDecimal(-500);
        Account account = optionalAccount.get();
        account.setBalance(depositAmount);
        accountRepository.save(account);
        try{
            entityManager.flush();
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
            assertTrue(ex.getConstraintViolations().iterator().next().getMessage().contains("Account balance cannot be negative"));
        }
    }
}
