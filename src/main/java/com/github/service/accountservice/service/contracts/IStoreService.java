package com.github.service.accountservice.service.contracts;

import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.service.models.ProductDto;
import com.github.service.accountservice.service.models.TransactionDto;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface IStoreService {

    TransactionDto buyProduct(@NotNull Integer accountId,@NotNull Integer productId) throws AccountException;
    List<ProductDto> listAllAvailableProducts() throws AccountException;

}
