package com.github.service.accountservice.repository;

import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional(rollbackOn = AccountException.class)
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Integer> {
}
