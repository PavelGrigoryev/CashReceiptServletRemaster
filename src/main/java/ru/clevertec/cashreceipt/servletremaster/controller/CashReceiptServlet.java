package ru.clevertec.cashreceipt.servletremaster.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.clevertec.cashreceipt.servletremaster.service.CashReceiptLogicService;
import ru.clevertec.cashreceipt.servletremaster.service.impl.CashReceiptLogicServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/cashReceipts")
public class CashReceiptServlet extends HttpServlet {

    private final transient CashReceiptLogicService cashReceiptLogicService = new CashReceiptLogicServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idAndQuantity = req.getParameter("idAndQuantity");
        String discountCardNumber = req.getParameter("discountCardNumber");
        PrintWriter printWriter = resp.getWriter();
        String cashReceipt = cashReceiptLogicService.createCashReceipt(idAndQuantity, discountCardNumber);
        printWriter.print(cashReceipt);
        printWriter.flush();
    }

}
