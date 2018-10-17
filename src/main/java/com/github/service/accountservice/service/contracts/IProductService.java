package com.github.service.accountservice.service.contracts;

import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.models.ProductDto;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface IProductService {

    ProductDto createProduct(@NotNull ProductCreate product) throws AccountException;
    List<ProductDto> findAll() throws AccountException;
    ProductDto deleteProduct(@NotNull ProductDelete product) throws AccountException;
    ProductDto updateProduct(@NotNull ProductDto product) throws AccountException;
}
