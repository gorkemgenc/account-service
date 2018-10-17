package com.github.service.accountservice.service.contracts;

import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.models.AccountDto;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public interface IAccountService {

    AccountDto findById(@NotNull int id) throws AccountException;
    AccountDto createAccount() throws AccountException;
    Account updateAccountAmount(@NotNull Integer accountIdInt, @NotNull BigDecimal amountDecimal, @NotNull TransactionTypes type) throws AccountException;
}