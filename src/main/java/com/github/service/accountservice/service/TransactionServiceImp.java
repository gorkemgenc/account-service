package com.github.service.accountservice.service;

import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Transaction;
import com.github.service.accountservice.entities.TransactionType;
import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.repository.TransactionRepository;
import com.github.service.accountservice.repository.TransactionTypeRepository;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.contracts.ITransactionService;
import com.github.service.accountservice.service.models.TransactionDto;
import com.github.service.accountservice.validator.IValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class TransactionServiceImp implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Autowired
    private IValidator validator;

    private ModelMapper modelMapper = new ModelMapper();
    private Logger logger = LoggerFactory.getLogger(TransactionServiceImp.class);

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public List<TransactionDto> getTransactionsByAccountId(@NotNull Integer accountId) throws AccountException {

        logger.info("getTransactionsByAccountId method was called");

        try{
            validator.isTrue((accountId < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Account Id"),
                    ErrorCode.BadRequest.getCode());

            List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
            return transactions.stream().map(w -> modelMapper.map(w, TransactionDto.class)).collect(Collectors.toList());
        }
        catch (NumberFormatException ex){
            throw new AccountException(ErrorCode.NotFound.getCode(), ErrorMessage.METHOD_NOT_WORKED.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public TransactionDto createTransaction(@NotNull Integer accountIdInt, @NotNull BigDecimal amountDecimal, @NotNull TransactionTypes type) throws AccountException{

        logger.info("createTransaction method was called");

        try{
            validator.isTrue((accountIdInt < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Account Id"),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue((amountDecimal.compareTo(BigDecimal.ZERO) <= 0),
                    String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"), ErrorCode.BadRequest.getCode());

            TransactionType transactionType = transactionTypeRepository.getOne(type.getCode());
            Account account = accountService.updateAccountAmount(accountIdInt, amountDecimal, type);
            Transaction transaction = new Transaction(transactionType, amountDecimal, account);
            transactionRepository.save(transaction);
            return modelMapper.map(transaction, TransactionDto.class);
        }
        catch (NumberFormatException ex){
            throw new AccountException(ErrorCode.Unprocessable_entity.getCode(), ErrorMessage.TRANSACTION_NOT_CREATED_EXCEPTION.getMessage());
        }
    }
}
