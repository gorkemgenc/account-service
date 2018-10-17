package com.github.service.accountservice.service;

import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.repository.AccountRepository;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.models.AccountDto;
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
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Validated
public class AccountServiceImp implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IValidator validator;

    private ModelMapper modelMapper = new ModelMapper();
    private Logger logger = LoggerFactory.getLogger(AccountServiceImp.class);

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public AccountDto findById(@NotNull int id) throws AccountException {

        logger.info("findById method was called");

        try {
            validator.isTrue((id < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Account Id"),
                    ErrorCode.BadRequest.getCode());

            Optional<Account> optionalAccount = accountRepository.findById(id);

            validator.isTrue(!optionalAccount.isPresent(), ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage(),
                    ErrorCode.BadRequest.getCode());

            return modelMapper.map(optionalAccount.get(), AccountDto.class);
        }catch (NumberFormatException ex){
            throw new AccountException(ErrorCode.NotFound.getCode(), ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public AccountDto createAccount() throws AccountException{

        logger.info("createAccount method was called");

        try{
            Account createdAccount = accountRepository.save(new Account());
            return modelMapper.map(createdAccount, AccountDto.class);
        }
        catch (NumberFormatException ex){
            throw new AccountException(ErrorCode.Unprocessable_entity.getCode(), ErrorMessage.ACCOUNT_NOT_CREATED_EXCEPTION.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public Account updateAccountAmount(@NotNull Integer accountIdInt, @NotNull BigDecimal amountDecimal, @NotNull TransactionTypes type) throws AccountException{

        logger.info("updateAccountAmount method was called");

        try {
            Optional<Account> existAccount = accountRepository.findById(accountIdInt);

            boolean validAmountCase = (type == TransactionTypes.DEPOSIT || type == TransactionTypes.WITHDRAW) ?
                    amountDecimal.compareTo(BigDecimal.ZERO) <= 0 :
                    amountDecimal.compareTo(BigDecimal.ZERO) < 0;

            validator.isTrue(!existAccount.isPresent(), ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage(),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue(validAmountCase, String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"),
                    ErrorCode.BadRequest.getCode());

            Account account = existAccount.get();

            if(type == TransactionTypes.DEPOSIT){
                account.setBalance(account.getBalance().add(amountDecimal));
            }
            else if(type == TransactionTypes.WITHDRAW){

                BigDecimal balance = account.getBalance();

                validator.isTrue((balance.subtract(amountDecimal).compareTo(BigDecimal.ZERO) < 0), ErrorMessage.NO_ENOUGH_BALANCE.getMessage(),
                        ErrorCode.BadRequest.getCode());

                account.setBalance(account.getBalance().subtract(amountDecimal));
            }
            else{
                account.setBalance(amountDecimal);
            }
            account.setUpdatedTime(new Date());
            return accountRepository.save(account);

        } catch(NumberFormatException e){
            throw new AccountException(ErrorCode.BadRequest.getCode(), String.format(ErrorMessage.NUMBER_FORMAT_MISMATCH.getMessage(),amountDecimal));
        }
    }
}
