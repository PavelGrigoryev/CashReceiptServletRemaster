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

@WebFilter(urlPatterns = "/discountCards")
public class DiscountCardFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("DELETE".equalsIgnoreCase(req.getMethod())) {
            String id = req.getParameter("id");
            if (id == null) {
                throw new InvalidInputException("id field is required");
            } else if (!StringUtils.isNumeric(id)) {
                throw new InvalidInputException("id field must be numeric whole number");
            }
        }
        chain.doFilter(request, response);
    }

}
