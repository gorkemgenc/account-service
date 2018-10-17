package com.github.service.accountservice.controller;

import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.RateLimiterException;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.contracts.ITransactionService;
import com.github.service.accountservice.aspect.RateLimit;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.models.AccountDto;
import com.github.service.accountservice.service.models.TransactionDto;
import com.github.service.accountservice.validator.IValidator;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IAccountService accountService;
    @Autowired
    private ITransactionService transactionService;
    @Autowired
    private IValidator validator;

    @PostMapping("/create")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(response = AccountDto.class, value = "Creates new account and return this account.")
    public ResponseEntity<?> create() throws RateLimiterException, AccountException{

        logger.info("WalletController create method calls for creating wallet");

        AccountDto createdWallet = accountService.createAccount();
        logger.info("WalletController create method was completed successfully and wallet was created");
        Map<String, Object> walletMap = new HashMap<>();
        walletMap.put("account", createdWallet);
        return new ResponseEntity<>(walletMap, HttpStatus.OK);
    }

    @PostMapping("/deposit")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(response = AccountDto.class, value = "Deposit operation of related account.")
    public ResponseEntity<?> deposit(@RequestBody Map<String, String> dataMap) throws RateLimiterException, AccountException{

        logger.info("AccountController deposit method was called.");

        validator.validate(dataMap, Arrays.asList("accountId", "amount"));
        String accountId = dataMap.get("accountId");
        String amount = dataMap.get("amount");

        transactionService.createTransaction(Integer.parseInt(accountId), new BigDecimal(amount), TransactionTypes.DEPOSIT);
        AccountDto accountDto = accountService.findById(Integer.valueOf(accountId));
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("account", accountDto);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(response = AccountDto.class, value = "Withdraw operation of related account")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, String> dataMap) throws RateLimiterException, AccountException{

        logger.info("AccountController withdraw method was called.");

        validator.validate(dataMap, Arrays.asList("accountId", "amount"));
        String accountId = dataMap.get("accountId");
        String amount = dataMap.get("amount");

        transactionService.createTransaction(Integer.parseInt(accountId), new BigDecimal(amount), TransactionTypes.WITHDRAW);
        logger.info("Transaction was created successfully");
        AccountDto accountDto = accountService.findById(Integer.valueOf(accountId));
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("account", accountDto);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }

    @PostMapping("/listTransactions")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    @ApiOperation(response = TransactionDto.class, value = "List all transactions of related account")
    public ResponseEntity<?> transactions(@RequestBody Map<String, String> dataMap) throws RateLimiterException, AccountException{

        logger.info("AccountController transactions method was called.");
        validator.validate(dataMap, Arrays.asList("accountId"));
        String accountId = dataMap.get("accountId");

        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(Integer.valueOf(accountId));
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("transactions", transactions);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }
}
