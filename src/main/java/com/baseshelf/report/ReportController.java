package com.baseshelf.report;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("stores/{store-id}/reports")
public class ReportController {

    private final ReportService reportService;

//    @GetMapping("/sample")
//    public void getSample(HttpServletResponse response){
//        // Step 2: Write Excel file to the response output stream
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.xlsx");
//
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            reportService.orderItemReport(out);
//            response.getOutputStream().write(out.toByteArray());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @GetMapping("/sales/date-range")
    public void getSaleReportForDateRange(
            HttpServletResponse response,
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from")LocalDate from,
            @RequestParam(value = "to")LocalDate to,
            @RequestParam(value = "limit") Integer limit
            ){
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String s1 = "attachment; " + "filename=" + from.toString() + "_" + "to" + "_" + to.toString() +".xlsx";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, s1);

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            reportService.salesReportGeneration(outputStream, storeId, from, to, limit);
            response.getOutputStream().write(outputStream.toByteArray());
        }catch (Exception e){
            throw new ExcelGenerationException(e.getMessage());
        }
    }

    @GetMapping("/orders/date-range")
    public void getOrderReportForDateRange(
            HttpServletResponse response,
            @PathVariable("store-id") Long storeId,
            @RequestParam(value = "from")LocalDate from,
            @RequestParam(value = "to")LocalDate to,
            @RequestParam(value = "limit") Integer limit
    ){
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String s1 = "attachment; " + "filename=" + from.toString() + "_" + "to" + "_" + to.toString() +".xlsx";
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, s1);

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            reportService.orderReportGeneration(outputStream, storeId, from, to, limit);
            response.getOutputStream().write(outputStream.toByteArray());
        }catch (Exception e){
            throw new ExcelGenerationException(e.getMessage());
        }
    }
}
