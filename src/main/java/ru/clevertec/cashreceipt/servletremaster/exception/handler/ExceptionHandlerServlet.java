package ru.clevertec.cashreceipt.servletremaster.exception.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@Slf4j
@WebServlet(urlPatterns = "/exception_handler")
public class ExceptionHandlerServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(404);
        Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
        log.error(exception.getMessage());
        PrintWriter printWriter = resp.getWriter();
        printWriter.print("{\"error\": \"" + exception.getMessage() + "\"}");
        printWriter.flush();
    }

}
