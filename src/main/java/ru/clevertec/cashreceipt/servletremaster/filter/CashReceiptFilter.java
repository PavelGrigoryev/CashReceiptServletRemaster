package ru.clevertec.cashreceipt.servletremaster.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import ru.clevertec.cashreceipt.servletremaster.exception.InvalidInputException;

import java.io.IOException;

@WebFilter(urlPatterns = "/cash_receipts")
public class CashReceiptFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String idAndQuantity = req.getParameter("idAndQuantity");
            String discountCardNumber = req.getParameter("discountCardNumber");
            if (idAndQuantity == null || discountCardNumber == null) {
                throw new InvalidInputException("idAndQuantity and discountCardNumber fields are required");
            } else if (!StringUtils.isNumeric(discountCardNumber)) {
                throw new InvalidInputException("discountCardNumber field must be numeric whole number");
            }
        }
        chain.doFilter(request, response);
    }

}
