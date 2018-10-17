package com.github.service.accountservice.controller;

import com.github.service.accountservice.service.contracts.IProductService;
import com.google.gson.GsonBuilder;
import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.models.ProductDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @TestConfiguration
    static class ProductControllerTestContextConfiguration {
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
    private IProductService productService;

    private ModelMapper modelMapper = new ModelMapper();
    private Product product;
    private ProductCreate productCreate;
    private ProductDelete productDelete;
    private ProductDto productUpdate;

    @Before
    public void before(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        product = new Product("Name1", new BigDecimal(100), 10);
        product.setId(1);
        productCreate = new ProductCreate(product.getName(), product.getPrice(), product.getProductCount());
        productDelete = new ProductDelete(product.getId());
        productUpdate = new ProductDto(product.getId(), product.getName(), product.getPrice(), product.getProductCount());
    }

    @Test
    public void testCreateProduct_thenReturnJsonArray() throws Exception{

        HashMap<String, ProductCreate> dataMap = new HashMap<>();
        dataMap.put("product", productCreate);
        given(productService.createProduct(productCreate)).willReturn(modelMapper.map(product, ProductDto.class));
        String uri = "/product/create";
        String json = new GsonBuilder().create().toJson(dataMap);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
    }

    @Test
    public void testListProducts_thenReturnJsonArray() throws Exception{

        List<ProductDto> productList = Arrays.asList(modelMapper.map(product, ProductDto.class));
        given( productService.findAll()).willReturn(productList);
        String uri = "/product/list";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
        assertTrue(content.contains("\"id\":1"));
        assertTrue(content.contains("\"name\":\"Name1\""));
    }

    @Test
    public void testDeleteProduct_thenReturnJsonArray() throws Exception{

        HashMap<String, ProductDelete> dataMap = new HashMap<>();
        dataMap.put("product", productDelete);
        given(productService.deleteProduct(productDelete)).willReturn(modelMapper.map(product, ProductDto.class));
        String uri = "/product/delete";
        String json = new GsonBuilder().create().toJson(dataMap);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(200, status);
    }

    @Test
    public void testUpdateProduct_thenReturnJsonArray() throws Exception {

        HashMap<String, ProductDto> dataMap = new HashMap<>();
        dataMap.put("product", productUpdate);
        given(productService.updateProduct(productUpdate)).willReturn(modelMapper.map(product, ProductDto.class));

        String uri = "/product/update";

        String json = new GsonBuilder().create().toJson(dataMap);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }
}
