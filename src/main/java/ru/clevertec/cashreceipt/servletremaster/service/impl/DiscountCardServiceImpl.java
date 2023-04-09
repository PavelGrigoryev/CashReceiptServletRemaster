package ru.clevertec.cashreceipt.servletremaster.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.dao.DiscountCardDao;
import ru.clevertec.cashreceipt.servletremaster.dao.impl.DiscountCardDaoImpl;
import ru.clevertec.cashreceipt.servletremaster.dto.DiscountCardDto;
import ru.clevertec.cashreceipt.servletremaster.exception.NoSuchDiscountCardException;
import ru.clevertec.cashreceipt.servletremaster.mapper.Mapper;
import ru.clevertec.cashreceipt.servletremaster.mapper.impl.DiscountCardToDtoMapper;
import ru.clevertec.cashreceipt.servletremaster.mapper.impl.DtoToDiscountCardMapper;
import ru.clevertec.cashreceipt.servletremaster.model.DiscountCard;
import ru.clevertec.cashreceipt.servletremaster.service.DiscountCardService;
import ru.clevertec.cashreceipt.servletremaster.util.PageFormatChecker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DiscountCardServiceImpl implements DiscountCardService {

    private DiscountCardDao discountCardDao;
    private Mapper<DiscountCard, DiscountCardDto> discountCardToDtoMapper;
    private Mapper<DiscountCardDto, DiscountCard> dtoToDiscountCardMapper;

    public DiscountCardServiceImpl() {
        try {
            discountCardDao = new DiscountCardDaoImpl();
            discountCardToDtoMapper = new DiscountCardToDtoMapper();
            dtoToDiscountCardMapper = new DtoToDiscountCardMapper();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<DiscountCardDto> findAll(Integer pageNumber, Integer pageSize) {
        int offset = PageFormatChecker.checkPageFormat(pageNumber, pageSize);
        List<DiscountCardDto> discountCards = new ArrayList<>();
        try {
            discountCards = discountCardDao.findAll(offset, pageSize)
                    .stream()
                    .map(discountCardToDtoMapper)
                    .toList();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        log.info("findAll discount cards size {}", discountCards.size());
        return discountCards;
    }

    @Override
    public DiscountCardDto findById(Long id) {
        DiscountCardDto discountCard = new DiscountCardDto();
        try {
            discountCard = discountCardDao.findById(id)
                    .map(discountCardToDtoMapper)
                    .orElseThrow(() -> new NoSuchDiscountCardException("DiscountCard with ID " + id + " does not exist"));
            log.info("findById#{} {}", id, discountCard);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return discountCard;
    }

    @Override
    public DiscountCardDto save(DiscountCardDto discountCardDto) {
        DiscountCard discountCard = dtoToDiscountCardMapper.apply(discountCardDto);
        DiscountCard savedDiscountCard = new DiscountCard();
        try {
            savedDiscountCard = discountCardDao.save(discountCard);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        DiscountCardDto dto = discountCardToDtoMapper.apply(savedDiscountCard);
        log.info("save {}", dto);
        return dto;
    }

    @Override
    public DiscountCardDto findByDiscountCardNumber(String discountCardNumber) {
        DiscountCardDto discountCard = new DiscountCardDto();
        try {
            discountCard = discountCardDao.findByDiscountCardNumber(discountCardNumber)
                    .map(discountCardToDtoMapper)
                    .orElseThrow(() -> new NoSuchDiscountCardException("DiscountCard with card number " +
                                                                       discountCardNumber + " does not exist"));
            log.info("findByDiscountCardNumber#{} {}", discountCardNumber, discountCard);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return discountCard;
    }

    @Override
    public DiscountCardDto delete(Long id) {
        DiscountCardDto discountCard = new DiscountCardDto();
        try {
            discountCard = discountCardDao.delete(id)
                    .map(discountCardToDtoMapper)
                    .orElseThrow(() -> new NoSuchDiscountCardException("No discount card with ID " + id + " to delete"));
            log.info("delete {}", discountCard);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return discountCard;
    }

}
