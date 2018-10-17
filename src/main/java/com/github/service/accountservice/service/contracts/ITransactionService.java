package com.github.service.accountservice.service.contracts;

import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.models.TransactionDto;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public interface ITransactionService {

    List<TransactionDto> getTransactionsByAccountId(@NotNull Integer walletId) throws AccountException;
    TransactionDto createTransaction(@NotNull Integer accountIdInt,@NotNull BigDecimal amountDecimal,@NotNull TransactionTypes transactionType) throws AccountException;
}
