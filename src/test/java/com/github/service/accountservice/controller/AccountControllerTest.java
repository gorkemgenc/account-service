package com.github.service.accountservice.controller;

import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.contracts.ITransactionService;
import com.google.gson.GsonBuilder;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Transaction;
import com.github.service.accountservice.entities.TransactionType;
import com.github.service.accountservice.service.models.AccountDto;
import com.github.service.accountservice.service.models.TransactionDto;
import com.github.service.accountservice.validator.IValidator;
import com.github.service.accountservice.validator.ValidatorImp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @TestConfiguration
    static class AccountControllerTestContextConfiguration {
        @Bean
        public IValidator validator() {
            return new ValidatorImp();
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    private IAccountService accountService;
    @MockBean
    private ITransactionService transactionService;

    private ModelMapper modelMapper = new ModelMapper();
    private Account account;
    private Transaction transaction;
    private TransactionType transactionType;

    @Before
    public void before() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        transactionType = new TransactionType("WITHDRAW");
        transactionType.setId(1);
        account = new Account();
        account.setId(1);
        transaction = new Transaction(transactionType,new BigDecimal(100), account);
    }

    @Test
    public void testGetTransactionsById_thenReturnJsonArray() throws Exception {

        List<TransactionDto> allTransactions = Arrays.asList(modelMapper.map(transaction, TransactionDto.class));
        given( transactionService.getTransactionsByAccountId(account.getId())).willReturn(allTransactions);
        String uri = "/account/listTransactions";
        Map<String, Integer> dataMap = new HashMap<>();
        dataMap.put("accountId",account.getId());
        String json = new GsonBuilder().create().toJson(dataMap);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertTrue(content.contains("\"accountId\":1"));
        assertTrue(content.contains("\"amount\":100"));

    }

    @Test
    public void testGetTransactionsByIdFail_whenNoAccount() throws Exception {

        List<TransactionDto> allTransactions = Arrays.asList(modelMapper.map(transaction, TransactionDto.class));
        given( transactionService.getTransactionsByAccountId(account.getId())).willReturn(allTransactions);
        String uri = "/account/listTransactions";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);

    }

    @Test
    public void testCreateAccount_thenReturnJson() throws Exception {

        given(accountService.createAccount()).willReturn(modelMapper.map(account, AccountDto.class));
        String uri = "/account/create";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertTrue(content.contains("\"id\":1"));
        assertTrue(content.contains("\"balance\":0"));
    }

    @Test
    public void testDepositAccount_thenReturnJson() throws Exception {

        given(accountService.updateAccountAmount(account.getId(), new BigDecimal(500), TransactionTypes.DEPOSIT)).willReturn(account);
        given(accountService.findById(Integer.valueOf(account.getId()))).willReturn(modelMapper.map(account, AccountDto.class));

        String uri = "/account/deposit";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("accountId","1");
        dataMap.put("amount","500");

        String json = new GsonBuilder().create().toJson(dataMap);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertTrue(content.contains("\"id\":1"));
        assertTrue(content.contains("\"balance\":0"));
    }

    @Test
    public void testWithdrawAccount_thenReturnJson() throws Exception {

        given(accountService.updateAccountAmount(account.getId(), new BigDecimal(500), TransactionTypes.WITHDRAW)).willReturn(account);
        given(accountService.findById(Integer.valueOf(account.getId()))).willReturn(modelMapper.map(account, AccountDto.class));

        String uri = "/account/withdraw";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("accountId","1");
        dataMap.put("amount","500");

        String json = new GsonBuilder().create().toJson(dataMap);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertTrue(content.contains("\"id\":1"));
        assertTrue(content.contains("\"balance\":0"));
    }
}
