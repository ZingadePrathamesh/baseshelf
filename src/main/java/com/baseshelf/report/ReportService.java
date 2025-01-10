package com.baseshelf.report;

import com.baseshelf.order.*;
import com.baseshelf.report.dto.OrderItemReport;
import com.baseshelf.store.Store;
import com.baseshelf.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderItemRepository orderItemRepository;
    private final ProductOrderRepository productOrderRepository;
    private final StoreService storeService;

    public <T> void generateExcelSheet(List<T> data, Class<T> dataClass, ByteArrayOutputStream out, String sheetName){
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet(sheetName);
            Field[] fields = dataClass.getDeclaredFields();

            for(int i = 0; i<fields.length; i++){
                sheet.setColumnWidth(i, 4000);
            }

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont headerFont =((XSSFWorkbook) workbook).createFont();
            headerFont.setColor(IndexedColors.WHITE.index);
            headerFont.setFontHeightInPoints((short)11);
            headerFont.setFontName("Arial");
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);

            CellStyle wrapStyle = workbook.createCellStyle();
            wrapStyle.setWrapText(true);

            Row headerRow = sheet.createRow(0);

            for(int i = 0; i < fields.length; i++){
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fields[i].getName());
                cell.setCellStyle(headerStyle);
            }

            for(int i = 0; i<data.size(); i++){
                T dataItem = data.get(i);
                Row row = sheet.createRow(i+1);

                for(int j = 0; j< fields.length; j++){
                    fields[j].setAccessible(true);
                    Cell cell = row.createCell(j);
                    Object value = fields[j].get(dataItem);

                    if(value instanceof LocalDate){
                        CellStyle dateStyle = workbook.createCellStyle();
                        dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));
                        cell.setCellValue(value.toString());
                        cell.setCellStyle(dateStyle);
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    }else if(value != null) {
                        cell.setCellValue(value.toString());
                    }
                    cell.setCellStyle(wrapStyle);
                }
            }
            workbook.write(out);
        }catch (IOException e){
            System.err.println(e.getMessage());
            throw new ExcelGenerationException("Failed to generate excel sheet: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ExcelGenerationException("Failed to generate excel sheet: " + e.getMessage());
        }
    }

    public void salesReportGeneration(ByteArrayOutputStream outputStream, Long storeId, LocalDate from, LocalDate to, Integer limit) {
        Store store = storeService.getById(storeId);
        List<OrderItemReport> reports = orderItemRepository.getSalesReportByDateRange(store, from, to, limit);
        generateExcelSheet(reports, OrderItemReport.class, outputStream, "Sales Report");
    }

    public void orderReportGeneration(ByteArrayOutputStream outputStream, Long storeId, LocalDate from, LocalDate to, Integer limit){
        Store store = storeService.getById(storeId);
        List<ProductOrderDto> orders = productOrderRepository.findAllProductOrderByDateRange(store, from, to, limit);
        generateExcelSheet(orders, ProductOrderDto.class, outputStream, "Order Report");
    }
}
