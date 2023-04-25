package ru.clevertec.cashreceipt.servletremaster.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.dto.ProductDto;
import ru.clevertec.cashreceipt.servletremaster.exception.PDFFileNotFoundException;
import ru.clevertec.cashreceipt.servletremaster.service.CashReceiptInformationService;
import ru.clevertec.cashreceipt.servletremaster.service.CashReceiptLogicService;
import ru.clevertec.cashreceipt.servletremaster.service.DiscountCardService;
import ru.clevertec.cashreceipt.servletremaster.service.PdfUploadFileService;
import ru.clevertec.cashreceipt.servletremaster.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class CashReceiptLogicServiceImpl implements CashReceiptLogicService {

    private final ProductService productService;
    private final DiscountCardService discountCardService;
    private final CashReceiptInformationService cashReceiptInformationService;
    private final PdfUploadFileService pdfUploadFileService;

    public CashReceiptLogicServiceImpl() {
        productService = new ProductServiceImpl();
        discountCardService = new DiscountCardServiceImpl();
        cashReceiptInformationService = new CashReceiptInformationServiceImpl();
        pdfUploadFileService = new PdfUploadFileServiceImpl();
    }

    @Override
    public String createCashReceipt(String idAndQuantity, String discountCardNumber) {
        StringBuilder cashReceiptHeader = cashReceiptInformationService
                .createCashReceiptHeader(LocalDate.now(), LocalTime.now());
        StringBuilder promoDiscountBuilder = new StringBuilder();
        final BigDecimal[] promoDiscount = {new BigDecimal("0")};

        String cashReceipt = getProducts(idAndQuantity)
                .stream()
                .peek(productDto -> cashReceiptHeader
                        .append(cashReceiptInformationService.createCashReceiptBody(productDto)))
                .peek(productDto -> promotionFilter(promoDiscountBuilder, promoDiscount, productDto))
                .map(ProductDto::getTotal)
                .reduce(BigDecimal::add)
                .map(totalSum -> getCashReceiptResults(
                        discountCardNumber,
                        cashReceiptHeader,
                        promoDiscountBuilder,
                        promoDiscount,
                        totalSum
                ))
                .map(StringBuilder::toString)
                .stream()
                .peek(pdfUploadFileService::uploadFilePdf)
                .findFirst()
                .orElseThrow(() -> new PDFFileNotFoundException("The value is not in the stream"));

        log.info(cashReceipt);
        return cashReceipt;
    }

    protected List<ProductDto> getProducts(String idAndQuantity) {
        return idAndQuantity.lines()
                .map(s -> s.split(" "))
                .flatMap(Arrays::stream)
                .map(s -> s.split("-"))
                .map(strings -> productService.update(Long.valueOf(strings[0]), Integer.valueOf(strings[1])))
                .toList();
    }

    protected void promotionFilter(StringBuilder promoDiscountBuilder, BigDecimal[] promoDiscount, ProductDto productDto) {
        if (Boolean.TRUE.equals(productDto.getPromotion()) && productDto.getQuantity() > 5) {
            BigDecimal promo = getPromotionDiscount(productDto);
            promoDiscountBuilder.append(cashReceiptInformationService
                    .createCashReceiptPromoDiscount(productDto.getName(), promo));
            promoDiscount[0] = promoDiscount[0].add(promo);
        }
    }

    protected StringBuilder getCashReceiptResults(String discountCardNumber,
                                                  StringBuilder cashReceiptHeader,
                                                  StringBuilder promoDiscountBuilder,
                                                  BigDecimal[] promoDiscount,
                                                  BigDecimal totalSum) {
        return Stream.of(discountCardService.findByDiscountCardNumber(discountCardNumber))
                .map(discountCardDto -> {
                    BigDecimal discount = getDiscount(totalSum, discountCardDto.getDiscountPercentage());
                    return cashReceiptHeader.append(
                            cashReceiptInformationService.createCashReceiptResults(
                                    totalSum,
                                    discountCardDto.getDiscountPercentage(),
                                    discount,
                                    promoDiscountBuilder,
                                    totalSum.subtract(discount).subtract(promoDiscount[0]))
                    );
                })
                .findFirst()
                .orElse(new StringBuilder());
    }

    protected BigDecimal getPromotionDiscount(ProductDto productDto) {
        return productDto.getTotal()
                .multiply(BigDecimal.valueOf(0.1))
                .stripTrailingZeros();
    }

    protected BigDecimal getDiscount(BigDecimal totalSum, BigDecimal percentage) {
        return totalSum.multiply(BigDecimal.valueOf(0.01)
                .multiply(percentage));
    }

}
