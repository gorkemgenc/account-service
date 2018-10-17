package com.github.service.accountservice.service;

import com.github.service.accountservice.entities.Account;
import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.entities.Transaction;
import com.github.service.accountservice.entities.TransactionType;
import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.enums.TransactionTypes;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.repository.AccountRepository;
import com.github.service.accountservice.repository.ProductRepository;
import com.github.service.accountservice.repository.TransactionRepository;
import com.github.service.accountservice.repository.TransactionTypeRepository;
import com.github.service.accountservice.service.contracts.IAccountService;
import com.github.service.accountservice.service.contracts.IProductService;
import com.github.service.accountservice.service.contracts.IStoreService;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.models.ProductDto;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class StoreServiceImp implements IStoreService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    IAccountService accountService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TransactionTypeRepository transactionTypeRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    IProductService productService;

    @Autowired
    private IValidator validator;

    private ModelMapper modelMapper = new ModelMapper();
    private Logger logger = LoggerFactory.getLogger(StoreServiceImp.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public TransactionDto buyProduct(@NotNull Integer accountId, @NotNull Integer productId) throws AccountException {

        logger.info("buyProduct method was called");

        try{
            validator.isTrue((accountId < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Account Id"),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue((productId < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Product Id"),
                    ErrorCode.BadRequest.getCode());

            Optional<Account> optionalAccount =  accountRepository.findById(accountId);
            validator.isTrue(!optionalAccount.isPresent(), ErrorMessage.ACCOUNT_NOT_FOUND_EXCEPTION.getMessage(),
                    ErrorCode.BadRequest.getCode());

            Account account = optionalAccount.get();
            Optional<Product> optionalProduct =  productRepository.findById(productId);
            validator.isTrue(!optionalProduct.isPresent(), ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage(),
                    ErrorCode.BadRequest.getCode());

            Product product = optionalProduct.get();

            BigDecimal currentBalance = account.getBalance();
            BigDecimal productPrice = product.getPrice();

            validator.isTrue((currentBalance.subtract(productPrice).compareTo(BigDecimal.ZERO) < 0),
                    ErrorMessage.CURRENT_BALANCE_SHOULD_BE_GREATER_THAN_PRICE.getMessage(), ErrorCode.BadRequest.getCode());

            TransactionType transactionType = transactionTypeRepository.getOne(TransactionTypes.PURCHASE.getCode());

            logger.info("updateAccountAmount method is starting in buyProduct method");

            Account updatedAccount = accountService.updateAccountAmount(accountId, currentBalance.subtract(productPrice), TransactionTypes.PURCHASE);
            ProductDto updatedProduct = productService.deleteProduct(new ProductDelete(productId));
            Product existsProduct = productRepository.getOne(updatedProduct.getId());
            Transaction transaction = new Transaction(transactionType, productPrice, updatedAccount, existsProduct);
            transactionRepository.save(transaction);
            return modelMapper.map(transaction, TransactionDto.class);
        }
        catch (NumberFormatException ex){
            throw  new AccountException(ErrorCode.Unprocessable_entity.getCode(), ErrorMessage.METHOD_NOT_WORKED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public List<ProductDto> listAllAvailableProducts() throws AccountException {

        logger.info("listAllAvailableProducts method was called");

        return productRepository.findAllAvailableByOrderByIdAsc().
                stream().
                map(w -> modelMapper.map(w, ProductDto.class))
                .collect(Collectors.toList());
    }
}
