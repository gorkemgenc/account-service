package com.github.service.accountservice.validator;

import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.models.ProductDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Validated
@Component
public class ValidatorImp implements IValidator<String,String> {

    @Override
    public void validate(@NotNull Map<String, String> input, @NotNull List<String> required) throws AccountException {

        for (String parameter : required) {
            if (!input.containsKey(parameter) || (input.get(parameter) == null) || (input.get(parameter).isEmpty())) {
                String message = String.format(ErrorMessage.NO_MANDATORY_FIELD.getMessage(), parameter);
                throw new AccountException(400, message);
            }
        }
    }

    @Override
    public void createProductValidate(@NotNull Map<String, ProductCreate> input, @NotNull List<String> required) throws AccountException {

        for (String parameter : required) {
            if (!input.containsKey(parameter) || (input.get(parameter) == null)) {
                String message = String.format(ErrorMessage.NO_MANDATORY_FIELD.getMessage(), parameter);
                throw new AccountException(400, message);
            }
        }
    }

    @Override
    public void deleteProductValidate(@NotNull Map<String, ProductDelete> input, @NotNull List<String> required) throws AccountException {

        for (String parameter : required) {
            if (!input.containsKey(parameter) || (input.get(parameter) == null)) {
                String message = String.format(ErrorMessage.NO_MANDATORY_FIELD.getMessage(), parameter);
                throw new AccountException(400, message);
            }
        }
    }

    @Override
    public void updateProductValidate(@NotNull Map<String, ProductDto> input, @NotNull List<String> required) throws AccountException {

        for (String parameter : required) {
            if (!input.containsKey(parameter) || (input.get(parameter) == null)) {
                String message = String.format(ErrorMessage.NO_MANDATORY_FIELD.getMessage(), parameter);
                throw new AccountException(400, message);
            }
        }
    }

    @Override
    public void isTrue(@NotNull Boolean condition,@NotNull String errorMessage, int errorCode) throws AccountException{

        if(condition){
            throw new AccountException(errorCode, errorMessage);
        }
    }
}
