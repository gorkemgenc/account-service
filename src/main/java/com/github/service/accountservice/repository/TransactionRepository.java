package com.github.service.accountservice.repository;

import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(rollbackOn = AccountException.class)
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccountId(Integer accountId);
}