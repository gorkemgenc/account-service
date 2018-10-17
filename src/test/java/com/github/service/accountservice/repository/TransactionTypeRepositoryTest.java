package com.github.service.accountservice.repository;

import com.github.service.accountservice.entities.TransactionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    private TransactionType type1;

    @Before
    public void before(){
        type1 = new TransactionType("Name1");
        entityManager.persist(type1);
        entityManager.flush();
    }

    @Test
    public void testFindById(){

        Optional<TransactionType> optionalType = transactionTypeRepository.findById(type1.getId());
        assertTrue(optionalType.isPresent());
        TransactionType type = optionalType.get();
        assertEquals(type.getId(), type1.getId());
        assertTrue(type.getType().equals(type1.getType()));
    }

    @Test
    public void testFindById_NotFound() {

        Optional<TransactionType> optionalType = transactionTypeRepository.findById(100);
        assertTrue(!optionalType.isPresent());
    }

    @Test
    public void testList() {

        List<TransactionType> typeList = transactionTypeRepository.findAll();
        assertTrue(typeList.size() > 0);
        assertTrue(typeList.get(0).getType().equals("DEPOSIT"));
        assertTrue(typeList.get(1).getType().equals("WITHDRAW"));
        assertTrue(typeList.get(2).getType().equals("PURCHASE"));
    }
}
