package com.github.service.accountservice.repository;

import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional(rollbackOn = AccountException.class)
public interface AccountRepository extends JpaRepository<Account, Integer> {

    List<Account> findAllByOrderByIdAsc();
}
