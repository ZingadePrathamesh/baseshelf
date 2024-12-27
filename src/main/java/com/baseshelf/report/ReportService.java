package com.baseshelf.report;

import com.baseshelf.order.OrderItemRepository;
import com.baseshelf.report.dto.OrderItemReport;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderItemRepository orderItemRepository;

    public void sampleExcel(ByteArrayOutputStream out){
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Person");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Name");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Age");
            headerCell.setCellStyle(headerStyle);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            Row row = sheet.createRow(2);
            Cell cell = row.createCell(0);
            cell.setCellValue("John Smith");
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(20);
            cell.setCellStyle(style);

              workbook.write(out);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void orderItemsExcelSheet(ByteArrayOutputStream out){
        try( Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("OrderItems");
            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 3000);
            sheet.setColumnWidth(2, 3000);
            sheet.setColumnWidth(3, 3000);
            sheet.setColumnWidth(4, 5000);
            sheet.setColumnWidth(5, 5000);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat((short)14);
            sheet.setDefaultColumnStyle(0, dateStyle);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            font.setBold(false);
            headerStyle.setFont(font);

            Field[] fields = OrderItemReport.class.getDeclaredFields();

            for(int i = 0; i < fields.length; i++){
                Cell cell = header.createCell(i);
                cell.setCellValue(fields[i].getName());
                cell.setCellStyle(headerStyle);
            }

            List<OrderItemReport> results = orderItemRepository.getAllOrderItems();
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            for(int i= 0; i< results.size(); i++){
                Row row = sheet.createRow(i+1);
                OrderItemReport report = results.get(i);

                Cell cell = row.createCell(0);
                cell.setCellValue( report.getDate());

                cell = row.createCell(1);
                cell.setCellValue( report.getId());

                cell = row.createCell(2);
                cell.setCellValue(report.getProduct());

                cell = row.createCell(3);
                cell.setCellValue(report.getQuantity());

                cell = row.createCell(4);
                cell.setCellValue(report.getProductOrder());

                cell = row.createCell(5);
                cell.setCellValue(report.getAmount());

                cell.setCellStyle(style);
            }

            workbook.write(out);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public <T> void exportToExcel(List<T> dataList, Class<T> dataClass, ByteArrayOutputStream out, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create a sheet
            Sheet sheet = workbook.createSheet(sheetName);
            Field[] fields = dataClass.getDeclaredFields();

            // Set column widths dynamically
            for (int i = 0; i < fields.length; i++) {
                sheet.setColumnWidth(i, 5000);
            }

            // Create styles
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setFontName("Arial");
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
                cell.setCellStyle(headerStyle);
            }

            // Populate data rows
            for (int i = 0; i < dataList.size(); i++) {
                T item = dataList.get(i);
                Row dataRow = sheet.createRow(i + 1);

                for (int j = 0; j < fields.length; j++) {
                    fields[j].setAccessible(true); // Access private fields
                    Object value = fields[j].get(item); // Get field value
                    Cell cell = dataRow.createCell(j);

                    if (value instanceof LocalDate) {
                        CellStyle dateStyle = workbook.createCellStyle();
                        dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));
                        cell.setCellValue(value.toString()); // Convert to string for date formatting
                        cell.setCellStyle(dateStyle);
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value != null) {
                        cell.setCellValue(value.toString());
                    }

                    cell.setCellStyle(wrapStyle);
                }
            }

            // Write to output stream
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void orderItemReport(ByteArrayOutputStream out){
        List<OrderItemReport> results = orderItemRepository.getAllOrderItems();
        exportToExcel(results, OrderItemReport.class, out, "OrderItem");
    }
}
