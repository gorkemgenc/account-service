package com.github.service.accountservice.validator;

import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.models.ProductDto;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public interface IValidator<T1, T2> {

    void validate(@NotNull Map<T1, T2> input, @NotNull List<T1> required) throws AccountException;
    void createProductValidate(@NotNull Map<String, ProductCreate> input, @NotNull List<String> required) throws AccountException;
    void deleteProductValidate(@NotNull Map<String, ProductDelete> input, @NotNull List<String> required) throws AccountException;
    void updateProductValidate(@NotNull Map<String, ProductDto> input, @NotNull List<String> required) throws AccountException;
    void isTrue(@NotNull Boolean condition, @NotNull String errorMessage, int errorCode) throws AccountException;
}
