package com.github.service.accountservice.service;

import com.github.service.accountservice.entities.Product;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.repository.ProductRepository;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.contracts.IProductService;
import com.github.service.accountservice.service.models.ProductDto;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class ProductServiceImp implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private IValidator validator;

    private ModelMapper modelMapper = new ModelMapper();
    private Logger logger = LoggerFactory.getLogger(ProductServiceImp.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public ProductDto createProduct(@NotNull ProductCreate product) throws AccountException {

        logger.info("CreateProduct method was called");

        try{
            List<Product> productListWithSameName = productRepository.findAll().stream().
                    filter(p -> p.getName().equals(product.getName())).
                    collect(Collectors.toList());

            validator.isTrue(!productListWithSameName.isEmpty() && productListWithSameName.size() > 0,
                    ErrorMessage.NAME_SHOULD_BE_DIFFERENCE.getMessage(), ErrorCode.BadRequest.getCode());

            String name = product.getName();
            BigDecimal priceDecimal = product.getPrice();
            Integer countInt = product.getCount();

            validator.isTrue((name.trim().isEmpty() || name.trim().length() <= 0), ErrorMessage.NAME_SHOULD_BE_FILLED.getMessage(),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue((priceDecimal.compareTo(BigDecimal.ZERO) <=0),
                    String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Product Price"), ErrorCode.BadRequest.getCode());

            validator.isTrue((countInt < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Product Count"),
                    ErrorCode.BadRequest.getCode());

            Product newProduct = new Product(name, priceDecimal, countInt);
            Product createdProduct = productRepository.save(newProduct);
            return modelMapper.map(createdProduct, ProductDto.class);
        }
        catch (NumberFormatException ex){
            throw new AccountException(ErrorCode.Unprocessable_entity.getCode(), ErrorMessage.PRODUCT_NOT_CREATED_EXCEPTION.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public List<ProductDto> findAll() throws AccountException {

        logger.info("FindAll method was called for getting all product");
        return productRepository.findAllByOrderByIdAsc().stream().map(w -> modelMapper.map(w, ProductDto.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public ProductDto deleteProduct(@NotNull ProductDelete product) throws AccountException {

        logger.info("deleteProduct method was called");

        try{
            Integer id = product.getId();

            validator.isTrue(id < 1, String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Product Id"),
                    ErrorCode.BadRequest.getCode());

            Optional<Product> currentProduct = productRepository.findById(id);

            validator.isTrue(!currentProduct.isPresent(), ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage(),
                    ErrorCode.BadRequest.getCode());

            int currentCount = currentProduct.get().getProductCount();

            validator.isTrue(currentCount <= 0, String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Product count"),
                    ErrorCode.BadRequest.getCode());

            currentProduct.get().setProductCount(currentCount-1);
            currentProduct.get().setLastUpdated(new Date());
            Product updatedProduct = productRepository.save(currentProduct.get());
            return modelMapper.map(updatedProduct, ProductDto.class);
        }
        catch (NumberFormatException ex){
            throw  new AccountException(ErrorCode.Unprocessable_entity.getCode(), ErrorMessage.DELETE_PRODUCT_METHOD_NOT_WORKED.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public ProductDto updateProduct(@NotNull ProductDto product) throws AccountException {

        logger.info("UpdateProduct method was called");

        try{
            Integer idInt = product.getId();
            String name = product.getName();
            BigDecimal priceDecimal = product.getPrice();
            Integer countInt = product.getCount();

            validator.isTrue((idInt < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Product Id"),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue((name.trim().isEmpty() || name.trim().length() == 0), ErrorMessage.NAME_SHOULD_BE_FILLED.getMessage(),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue((priceDecimal.compareTo(BigDecimal.ZERO) < 0), String.format(ErrorMessage.SHOULD_GREATER_THAN_ZERO.getMessage(), "Amount"),
                    ErrorCode.BadRequest.getCode());

            validator.isTrue((countInt < 0), String.format(ErrorMessage.SHOULD_NOT_BE_SMALLER_THAN_ZERO.getMessage(), "Product Count"),
                    ErrorCode.BadRequest.getCode());

            Optional<Product> currentOptionalProduct = productRepository.findById(idInt);
            validator.isTrue(!currentOptionalProduct.isPresent(), ErrorMessage.PRODUCT_IS_NOT_VALID.getMessage(),
                    ErrorCode.BadRequest.getCode());

            Product currentProduct = currentOptionalProduct.get();

            List<Product> productListWithSameName = productRepository.findAll().stream().
                    filter(p -> p.getName().equals(name) && p.getId() != idInt).
                    collect(Collectors.toList());

            validator.isTrue(!productListWithSameName.isEmpty() && productListWithSameName.size() > 0,
                    ErrorMessage.NAME_SHOULD_BE_DIFFERENCE.getMessage(), ErrorCode.BadRequest.getCode());

            currentProduct.setName(name);
            currentProduct.setPrice(priceDecimal);
            currentProduct.setProductCount(countInt);
            currentProduct.setLastUpdated(new Date());
            Product updatedProduct = productRepository.save(currentProduct);
            return modelMapper.map(updatedProduct, ProductDto.class);
        }
        catch (NumberFormatException ex){
            throw  new AccountException(ErrorCode.Unprocessable_entity.getCode(), ErrorMessage.METHOD_NOT_WORKED.getMessage());
        }
    }
}
