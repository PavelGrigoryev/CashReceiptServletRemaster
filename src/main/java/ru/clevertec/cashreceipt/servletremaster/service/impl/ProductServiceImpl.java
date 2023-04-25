package ru.clevertec.cashreceipt.servletremaster.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.dao.ProductDao;
import ru.clevertec.cashreceipt.servletremaster.dao.impl.ProductDAOImpl;
import ru.clevertec.cashreceipt.servletremaster.dto.ProductDto;
import ru.clevertec.cashreceipt.servletremaster.exception.NoSuchProductException;
import ru.clevertec.cashreceipt.servletremaster.mapper.Mapper;
import ru.clevertec.cashreceipt.servletremaster.mapper.impl.DtoToProductMapper;
import ru.clevertec.cashreceipt.servletremaster.mapper.impl.ProductToDtoMapper;
import ru.clevertec.cashreceipt.servletremaster.model.Product;
import ru.clevertec.cashreceipt.servletremaster.service.ProductService;
import ru.clevertec.cashreceipt.servletremaster.util.PageFormatChecker;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProductServiceImpl implements ProductService {

    private ProductDao productDao;
    private Mapper<Product, ProductDto> productToDtoMapper;
    private Mapper<ProductDto, Product> dtoToProductMapper;

    public ProductServiceImpl() {
        try {
            productDao = new ProductDAOImpl();
            productToDtoMapper = new ProductToDtoMapper();
            dtoToProductMapper = new DtoToProductMapper();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<ProductDto> findAll(Integer pageNumber, Integer pageSize) {
        int offset = PageFormatChecker.checkPageFormat(pageNumber, pageSize);
        List<ProductDto> products = new ArrayList<>();
        try {
            products = productDao.findAll(offset, pageSize).stream()
                    .map(productToDtoMapper)
                    .toList();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        log.info("findAll products size {}", products.size());
        return products;
    }

    @Override
    public ProductDto findById(Long id) {
        ProductDto product = new ProductDto();
        try {
            product = productDao.findById(id)
                    .map(productToDtoMapper)
                    .orElseThrow(() -> new NoSuchProductException("Product with ID " + id + " does not exist"));
            log.info("findById#{} {}", id, product);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return product;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        productDto.setTotal(productDto.getPrice().multiply(BigDecimal.valueOf(productDto.getQuantity())));
        Product product = dtoToProductMapper.apply(productDto);
        Product savedProduct = new Product();
        try {
            savedProduct = productDao.save(product);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        ProductDto dto = productToDtoMapper.apply(savedProduct);
        log.info("save {}", dto);
        return dto;
    }

    @Override
    public ProductDto update(Long id, Integer quantity) {
        ProductDto productDto = new ProductDto();
        try {
            productDto = productDao.findById(id)
                    .map(product -> {
                        if (!quantity.equals(product.getQuantity())) {
                            product.setQuantity(quantity);
                            product.setTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                            Product updatedProduct = new Product();
                            try {
                                updatedProduct = productDao.update(product);
                                log.info("update {}", updatedProduct);
                            } catch (SQLException e) {
                                log.error(e.getMessage(), e);
                            }
                            return updatedProduct;
                        } else {
                            log.info("no update {}", product);
                            return product;
                        }
                    })
                    .map(productToDtoMapper)
                    .orElseThrow(() -> new NoSuchProductException("Product with ID " + id + " does not exist"));
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return productDto;
    }

    @Override
    public ProductDto delete(Long id) {
        ProductDto product = new ProductDto();
        try {
            product = productDao.delete(id)
                    .map(productToDtoMapper)
                    .orElseThrow(() -> new NoSuchProductException("No product with ID " + id + " to delete"));
            log.info("delete {}", product);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return product;
    }

}
