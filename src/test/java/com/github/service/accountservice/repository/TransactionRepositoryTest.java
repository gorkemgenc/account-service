package com.github.service.accountservice.repository;

import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Transaction;
import com.github.service.accountservice.entities.TransactionType;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProductRepository productRepository;

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
    public void before(){

        type1 = transactionTypeRepository.getOne(1);
        type2 = transactionTypeRepository.getOne(2);
        type3 = transactionTypeRepository.getOne(3);

        account1 = new Account();
        account2 = new Account();
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.flush();

        product1 = new Product("Name1", new BigDecimal(100), 200);
        entityManager.persist(product1);
        entityManager.flush();

        transaction1 = new Transaction(type1, new BigDecimal(50), account1);
        transaction2 = new Transaction(type2, new BigDecimal(50), account2, product1);
        transaction3 = new Transaction(type3, new BigDecimal(100), account2);
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.persist(transaction3);
        entityManager.flush();
    }

    @Test
    public void testFindByAccountId(){

        List<Transaction> transactions = transactionRepository.findByAccountId(account2.getId());
        assertTrue(transactions.size() > 0);
        assertTrue(transactions.get(0).getProduct().getName().equals(product1.getName()));
        assertTrue(transactions.get(0).getProduct().getPrice().equals(product1.getPrice()));
        assertEquals(transactions.get(0).getProduct().getProductCount(), product1.getProductCount());
        assertEquals(transactions.get(0).getAccount().getId(), account2.getId());
        assertTrue(transactions.get(0).getAmount().equals(new BigDecimal(50)));
        assertTrue(transactions.get(1).getAmount().equals(new BigDecimal(100)));
    }

    @Test
    public void testSave_SuccessWithoutProduct(){

        Transaction transaction = new Transaction(type1, new BigDecimal(200), account2);
        Transaction addedTransaction = transactionRepository.save(transaction);
        assertEquals(addedTransaction.getId(), transaction.getId());
        assertEquals(addedTransaction.getAccount().getId(), transaction.getAccount().getId());
        assertTrue(addedTransaction.getAmount().equals(transaction.getAmount()));
        assertEquals(addedTransaction.getType().getId(), transaction.getType().getId());
    }

    @Test
    public void testSave_SuccessWithProduct(){

        Transaction transaction = new Transaction(type1, new BigDecimal(200), account2, product1);
        Transaction addedTransaction = transactionRepository.save(transaction);
        assertEquals(addedTransaction.getId(), transaction.getId());
        assertEquals(addedTransaction.getAccount().getId(), transaction.getAccount().getId());
        assertTrue(addedTransaction.getAmount().equals(transaction.getAmount()));
        assertEquals(addedTransaction.getProduct().getId(), transaction.getProduct().getId());
        assertEquals(addedTransaction.getProduct().getProductCount(), transaction.getProduct().getProductCount());
        assertTrue(addedTransaction.getProduct().getPrice().equals(transaction.getProduct().getPrice()));
        assertEquals(addedTransaction.getType().getId(), transaction.getType().getId());
    }

    @Test
    public void testSave_FailWhenAccountNotExists(){

        Transaction transaction = new Transaction(type1, new BigDecimal(200), null, product1);
        try{
            transactionRepository.save(transaction);
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
        }
    }

    @Test
    public void testSave_FailWhenAmountIsNegative(){

        Transaction transaction = new Transaction(type1, new BigDecimal(-200), account2);
        try{
            transactionRepository.save(transaction);
            fail();
        }
        catch(ConstraintViolationException ex){
            assertFalse(ex.getConstraintViolations().isEmpty());
        }
    }
}
