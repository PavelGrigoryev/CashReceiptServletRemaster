package ru.clevertec.cashreceipt.servletremaster.util;

import ru.clevertec.cashreceipt.servletremaster.exception.PageFormatException;

public interface PageFormatChecker {

    static Integer checkPageFormat(Integer pageNumber, Integer pageSize) {
        int offset;
        if (pageNumber <= 0) {
            throw new PageFormatException("Your page number is " + pageNumber
                                          + "! Value must be greater than zero!");
        } else if (pageSize <= 0) {
            throw new PageFormatException("Your page size is " + pageSize
                                          + "! Value must be greater than zero!");
        }
        offset = (pageNumber * pageSize) - pageSize;
        return offset;
    }

}
