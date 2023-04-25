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

@WebFilter(urlPatterns = "/products")
public class ProductFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if ("PUT".equalsIgnoreCase(req.getMethod())) {
            doPut(req);
        } else if ("DELETE".equalsIgnoreCase(req.getMethod())) {
            doDelete(req);
        }
        chain.doFilter(request, response);
    }

    private void doPut(HttpServletRequest req) {
        String id = req.getParameter("id");
        String quantity = req.getParameter("quantity");
        if (id == null || quantity == null) {
            throw new InvalidInputException("id and quantity fields are required");
        } else if (!StringUtils.isNumeric(id) || !StringUtils.isNumeric(quantity)) {
            throw new InvalidInputException("id and quantity fields must be numeric whole numbers");
        }
    }

    private void doDelete(HttpServletRequest req) {
        String id = req.getParameter("id");
        if (id == null) {
            throw new InvalidInputException("id field is required");
        } else if (!StringUtils.isNumeric(id)) {
            throw new InvalidInputException("id field must be numeric whole number");
        }
    }

}
