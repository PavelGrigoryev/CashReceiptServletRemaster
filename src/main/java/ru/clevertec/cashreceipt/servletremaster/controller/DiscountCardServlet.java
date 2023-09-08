package ru.clevertec.cashreceipt.servletremaster.controller;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cashreceipt.servletremaster.dto.DiscountCardDto;
import ru.clevertec.cashreceipt.servletremaster.service.DiscountCardService;
import ru.clevertec.cashreceipt.servletremaster.service.impl.DiscountCardServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/discount_cards")
public class DiscountCardServlet extends HttpServlet {

    private final transient DiscountCardService discountCardService = new DiscountCardServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        String discountCardNumber = req.getParameter("discountCardNumber");
        String pageNumber = req.getParameter("pageNumber");
        String pageSize = req.getParameter("pageSize");
        PrintWriter printWriter = resp.getWriter();
        if (id != null) {
            findById(id, printWriter);
        } else if (discountCardNumber != null) {
            findByDiscountCardNumber(discountCardNumber, printWriter);
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

        DiscountCardDto discountCard = new Gson().fromJson(result.toString(), DiscountCardDto.class);
        DiscountCardDto savedDiscountCard = discountCardService.save(discountCard);
        String discountCardJson = new Gson().toJson(savedDiscountCard);

        PrintWriter printWriter = resp.getWriter();
        resp.setStatus(201);
        printWriter.print(discountCardJson);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        PrintWriter printWriter = resp.getWriter();
        DiscountCardDto discountCard = discountCardService.delete(Long.valueOf(id));
        printWriter.print("{\"info\": \"Discount card with ID " + discountCard.getId() + " successfully deleted\"}");
        printWriter.flush();
    }

    private void findById(String id, PrintWriter printWriter) {
        DiscountCardDto discountCard = discountCardService.findById(Long.valueOf(id));
        String discountCardJson = new Gson().toJson(discountCard);
        printWriter.print(discountCardJson);
        printWriter.flush();
    }

    private void findByDiscountCardNumber(String discountCardNumber, PrintWriter printWriter) {
        DiscountCardDto discountCard = discountCardService.findByDiscountCardNumber(discountCardNumber);
        String discountCardJson = new Gson().toJson(discountCard);
        printWriter.print(discountCardJson);
        printWriter.flush();
    }

    private void findAll(String pageNumber, String pageSize, PrintWriter printWriter) {
        if (pageNumber == null) {
            pageNumber = "1";
        }
        if (pageSize == null) {
            pageSize = "20";
        }
        List<DiscountCardDto> discountCardDtoList =
                discountCardService.findAll(Integer.valueOf(pageNumber), Integer.valueOf(pageSize));
        String discountCardsJson = new Gson().toJson(discountCardDtoList);
        printWriter.print(discountCardsJson);
        printWriter.flush();
    }

}
