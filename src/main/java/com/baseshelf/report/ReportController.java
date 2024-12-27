package com.baseshelf.report;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sample")
    public void getSample(HttpServletResponse response){
        // Step 2: Write Excel file to the response output stream
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.xlsx");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            reportService.orderItemReport(out);
            response.getOutputStream().write(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
