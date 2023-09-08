package ru.clevertec.cashreceipt.servletremaster.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.exception.PDFFileNotFoundException;
import ru.clevertec.cashreceipt.servletremaster.service.PdfUploadFileService;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class PdfUploadFileServiceImpl implements PdfUploadFileService {

    @Override
    public void uploadFilePdf(String cashReceipt) {
        PathResult pathResult = findPaths();

        Path path = Paths.get(pathResult.pdf());
        try {
            Document document = new Document(PageSize.A4);

            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(path));
            document.open();

            PdfReader reader = new PdfReader(pathResult.template());
            PdfImportedPage page = writer.getImportedPage(reader, 1);

            PdfContentByte contentByte = writer.getDirectContent();
            contentByte.addTemplate(page, 0, 0);

            Font font = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
            Paragraph paragraph = new Paragraph(30, cashReceipt, font);
            Paragraph empty = new Paragraph("\n".repeat(12));

            document.add(empty);
            document.add(paragraph);

            document.close();
            writer.close();

            log.info("uploadFilePdf {}", path);
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static PathResult findPaths() {
        URL pdfURL = PdfUploadFileServiceImpl.class.getResource("/pdf");
        URL templateURL = PdfUploadFileServiceImpl.class.getResource("/pdf/Clevertec_Template.pdf");
        if (pdfURL == null) {
            throw new PDFFileNotFoundException("Can not find a way to upload a pdf file");
        }
        if (templateURL == null) {
            throw new PDFFileNotFoundException("Can not find a way to download the pdf cover");
        }
        String pdf = URLDecoder.decode(pdfURL.getPath(), StandardCharsets.UTF_8)
                .concat("CashReceipt.pdf")
                .substring(1);
        String template = URLDecoder.decode(templateURL.getPath(), StandardCharsets.UTF_8);
        return new PathResult(pdf, template);
    }

    private record PathResult(String pdf, String template) {
    }

}
