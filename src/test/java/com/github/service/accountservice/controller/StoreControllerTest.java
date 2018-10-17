package com.github.service.accountservice.controller;

import com.github.service.accountservice.service.contracts.IProductService;
import com.google.gson.GsonBuilder;
import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.entities.Transaction;
import com.github.service.accountservice.entities.TransactionType;
import com.github.service.accountservice.service.contracts.IStoreService;
import com.github.service.accountservice.service.models.ProductDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(StoreController.class)
public class StoreControllerTest {

    @TestConfiguration
    static class StoreControllerTestContextConfiguration {
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
    private IStoreService storeService;
    @MockBean
    private IProductService productService;

    private ModelMapper modelMapper = new ModelMapper();
    private Product product;
    private Account account;
    private Transaction transaction;
    private TransactionType transactionType;

    @Before
    public void before(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        product = new Product("Name1", new BigDecimal(100), 10);
        product.setId(1);
        account = new Account();
        account.setId(1);
        transactionType = new TransactionType("PURCHASE");
        transaction = new Transaction(transactionType, new BigDecimal(100), account, product);
    }

    @Test
    public void testGetListAvailableProduct_thenReturnJsonArray() throws Exception{

        List<ProductDto> productList = Arrays.asList(modelMapper.map(product, ProductDto.class));
        given( storeService.listAllAvailableProducts()).willReturn(productList);
        String uri = "/store/list";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertTrue(content.contains("\"id\":1"));
        assertTrue(content.contains("\"name\":\"Name1\""));
    }

    @Test
    public void testBuyProduct_thenReturnJsonArray() throws Exception{

        given(storeService.buyProduct(account.getId(), product.getId())).willReturn(modelMapper.map(transaction, TransactionDto.class));
        String uri = "/store/buy";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("accountId",String.valueOf(account.getId()));
        dataMap.put("productId",String.valueOf(product.getId()));

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
}
