package ru.clevertec.cashreceipt.servletremaster.controller;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cashreceipt.servletremaster.dto.ProductDto;
import ru.clevertec.cashreceipt.servletremaster.service.ProductService;
import ru.clevertec.cashreceipt.servletremaster.service.impl.ProductServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/products")
public class ProductServlet extends HttpServlet {

    private final transient ProductService productService = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        String pageNumber = req.getParameter("pageNumber");
        String pageSize = req.getParameter("pageSize");
        PrintWriter printWriter = resp.getWriter();
        if (id != null) {
            findById(id, printWriter);
        } else {
            findAll(pageNumber, pageSize, printWriter);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        ProductDto product = new Gson().fromJson(result.toString(), ProductDto.class);
        ProductDto savedProduct = productService.save(product);
        String productJson = new Gson().toJson(savedProduct);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(productJson);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        String quantity = req.getParameter("quantity");
        PrintWriter printWriter = resp.getWriter();
        ProductDto product = productService.update(Long.valueOf(id), Integer.valueOf(quantity));
        String productJson = new Gson().toJson(product);
        printWriter.print(productJson);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        ProductDto product = productService.delete(Long.valueOf(id));
        printWriter.print("{\"info\": \"Product with ID " + product.getId() + " successfully deleted\"}");
        printWriter.flush();
    }

    private void findById(String id, PrintWriter printWriter) {
        ProductDto product = productService.findById(Long.valueOf(id));
        String productJson = new Gson().toJson(product);
        printWriter.print(productJson);
        printWriter.flush();
    }

    private void findAll(String pageNumber, String pageSize, PrintWriter printWriter) {
        if (pageNumber == null || pageNumber.isEmpty()) {
            pageNumber = "1";
        }
        if (pageSize == null || pageSize.isEmpty()) {
            pageSize = "20";
        }
        List<ProductDto> products = productService.findAll(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
        String productsJson = new Gson().toJson(products);
        printWriter.print(productsJson);
        printWriter.flush();
    }

}
