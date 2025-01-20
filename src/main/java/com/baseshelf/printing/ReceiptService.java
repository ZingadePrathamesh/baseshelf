package com.baseshelf.printing;

import com.baseshelf.order.response.OrderItemResponse;
import com.baseshelf.order.response.ProductOrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    final int PADDING_X = 20;
    final int PADDING_Y = 40;
    final int ELEMENT_MARGIN_Y = 5;

    final int TEXT_HEIGHT_1 = 36;
    final int TEXT_HEIGHT_3 = 25;

    public BufferedImage createReceipt(ProductOrderResponseDto dto){
        int x = 0;
        int y = 0;

        final int RECEIPT_WIDTH = 685;
        int RECEIPT_HEIGHT = calculateReceiptHeight(dto);
        
        BufferedImage bufferedImage = new BufferedImage(RECEIPT_WIDTH, RECEIPT_HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.fillRect(0, 0, RECEIPT_WIDTH, RECEIPT_HEIGHT);

        Font font1 = new Font("SansSerif", Font.PLAIN, TEXT_HEIGHT_1);
        Font font2 = new Font("SansSerif", Font.BOLD, TEXT_HEIGHT_3);
        Font font3 = new Font("SansSerif", Font.PLAIN, TEXT_HEIGHT_3);

        graphics2D.setFont(font3);
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        graphics2D.setColor(Color.BLACK);

        //initially setting the co-ords
        y += PADDING_Y;
        x += PADDING_X;
        y += TEXT_HEIGHT_3;

        //printing order id
        graphics2D.drawString("#" + dto.getId().toString(), x, y);


        //printing receipt
        y += ELEMENT_MARGIN_Y;
        y += TEXT_HEIGHT_3;
        fontMetrics = graphics2D.getFontMetrics();
        x = (RECEIPT_WIDTH - fontMetrics.stringWidth("RECEIPT")) / 2;
        graphics2D.drawString("RECEIPT", x, y);

        //printing shop details
        //printing shop name
        x = PADDING_X;
        y += TEXT_HEIGHT_1 + ELEMENT_MARGIN_Y * 4;
        graphics2D.setFont(font1);
        graphics2D.drawString(dto.getStore().getName(), x, y);

        //printing gstin number
        y += ELEMENT_MARGIN_Y + TEXT_HEIGHT_3;
        graphics2D.setFont(font3);
        graphics2D.drawString("GSTIN: " + dto.getStore().getGstinNumber(), x, y);

        //printing Address number
        y += ELEMENT_MARGIN_Y + TEXT_HEIGHT_3;
        graphics2D.setFont(font3);
        int w = RECEIPT_WIDTH - PADDING_X * 2;
        drawTextLine(dto.getStore().getAddress(), x, w, fontMetrics, graphics2D, y);
//        graphics2D.drawString(dto.getStore().getAddress(), x, y);

        //printing Address number
        y += ELEMENT_MARGIN_Y + TEXT_HEIGHT_3;
        graphics2D.setFont(font3);
        graphics2D.drawString(dto.getStore().getContactNumber(), x, y);

        //printing Address number
        y += ELEMENT_MARGIN_Y + TEXT_HEIGHT_3;
        graphics2D.setFont(font3);
        graphics2D.drawString(dto.getCreatedOn().toString() + " " + dto.getOrderTime().toString() , x, y);

        //divider
        y+= ELEMENT_MARGIN_Y * 2;
        graphics2D.drawLine(PADDING_X, y, RECEIPT_WIDTH - PADDING_X, y);

        //
        y += ELEMENT_MARGIN_Y * 4;
        y += TEXT_HEIGHT_3;

        fontMetrics = graphics2D.getFontMetrics();
        int x1 = x;
        int x5 = RECEIPT_WIDTH - PADDING_X - fontMetrics.stringWidth("000000.00");
        int x4 = x5 - PADDING_X - fontMetrics.stringWidth("0000.00");
        int x3 = x4 - PADDING_X - fontMetrics.stringWidth("0000.00");
        int x2 = x3 - PADDING_X - fontMetrics.stringWidth("000");

        graphics2D.drawString("Item", x1, y);
        graphics2D.drawString("QT.", x2, y);
        graphics2D.drawString("CGST", x3, y);
        graphics2D.drawString("SGST.", x4, y);
        graphics2D.drawString("Amt.", x5, y);

        y+= ELEMENT_MARGIN_Y;
        graphics2D.setColor(Color.GRAY);
        graphics2D.drawLine(PADDING_X, y, RECEIPT_WIDTH - PADDING_X, y);
        graphics2D.setColor(Color.BLACK);
        y += ELEMENT_MARGIN_Y;
//        w = x1 - x2;
        for (OrderItemResponse oi : dto.getOrderItems()){
            y += TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y;

            String name = oi.getProduct().getName();
            name = name.length() < 16? name : name.substring(0, 16);
            graphics2D.drawString(name, x1, y);
            graphics2D.drawString(oi.getQuantity().toString(), x2, y);
            graphics2D.drawString(oi.getCgst().toString(), x3, y);
            graphics2D.drawString(oi.getSgst().toString(), x4, y);
            graphics2D.drawString(oi.getAmountExcludingGst().toString(), x5, y);

            y += ELEMENT_MARGIN_Y + TEXT_HEIGHT_3;
            graphics2D.drawString(oi.getCgstAmount().toString(), x3, y);
            graphics2D.drawString(oi.getSgstAmount().toString(), x4, y);
            graphics2D.drawString(oi.getAmountIncludingGst().toString(), x5, y);

            y += ELEMENT_MARGIN_Y;
            graphics2D.setColor(Color.GRAY);
            graphics2D.drawLine(x, y,RECEIPT_WIDTH-PADDING_X, y);
            graphics2D.setColor(Color.BLACK);
        }

        y += ELEMENT_MARGIN_Y * 4 + TEXT_HEIGHT_3;

        //Totalling amount here
        graphics2D.drawString("Sub Total", x1, y);
//        graphics2D.drawString("--", x2, y);
        graphics2D.drawString(dto.getTotalAmountExcludingGst().toString(), x5, y);

        y += TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y;
        graphics2D.drawString("Total GST", x1, y);
//        graphics2D.drawString("--" , x2, y);
        graphics2D.drawString(dto.getTotalGst().toString(), x5, y);


        y += TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y;
        graphics2D.drawString("Discount", x1, y);
        graphics2D.drawString(dto.getTotalDiscount().toString(), x5, y);

        y+= ELEMENT_MARGIN_Y;
        graphics2D.setColor(Color.GRAY);
        graphics2D.drawLine(PADDING_X, y, RECEIPT_WIDTH - PADDING_X, y);
        graphics2D.setColor(Color.BLACK);
        y += ELEMENT_MARGIN_Y;

        y += TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y;
        graphics2D.setFont(font2);
        graphics2D.drawString("Grand Total (INR)", x1, y);
        graphics2D.drawString(dto.getTotalAmountIncludingGst().toString(), x5, y);

        graphics2D.setFont(font3);
        y += TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y;
        graphics2D.drawString("(in words)", x1, y);
        int width = 685 - x2 - 20;

        y = drawTextLine(dto.getAmountInWords(), x2, width, fontMetrics, graphics2D, y);

        //ending
        fontMetrics = graphics2D.getFontMetrics();
        y += ELEMENT_MARGIN_Y * 4 + TEXT_HEIGHT_3;
        graphics2D.setColor(Color.GRAY);
        graphics2D.drawString("Customer Copy", (RECEIPT_WIDTH - fontMetrics.stringWidth("Customer Copy")) / 2, y);

        y += TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y;
        graphics2D.drawString("Please Visit Again!", (RECEIPT_WIDTH - fontMetrics.stringWidth("Please Visit Again!")) / 2, y);

        graphics2D.dispose();
        return bufferedImage;
    }

    private int calculateReceiptHeight(ProductOrderResponseDto dto) {
        int baseHeight = 800; // Base receipt height
        int itemsHeight = dto.getOrderItems().size() * (TEXT_HEIGHT_3 + ELEMENT_MARGIN_Y) * 2;
        return baseHeight + itemsHeight;
    }

    private static int drawTextLine(String str, int x2, int width, FontMetrics fontMetrics, Graphics2D graphics2D, int y) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for(String word: str.split(" ")){
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (fontMetrics.stringWidth(testLine) > width) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word); // Start a new line with the current word
            } else {
                currentLine.append(currentLine.isEmpty() ? word : " " + word);
            }
        }
        // Add the last line
        lines.add(currentLine.toString());

        // Draw the lines
        for (String line : lines) {
            graphics2D.drawString(line, x2, y);
            y += 25 + 5; // Move to the next line
        }
        return y;
    }

    public BufferedImage generateReceipt(ProductOrderResponseDto order) {
        int width = 685;
        int height = 600 + (order.getOrderItems().size() * 20); // Adjust height based on items
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();
        // Background and anti-aliasing
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = 20; // Vertical starting position

        // Header
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString(order.getStore().getName(), 20, y);
        y += 20;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString(order.getStore().getDescription(), 20, y);
        y += 15;

        g2d.drawString(order.getStore().getContactNumber(), 20, y);
        y += 15;

        g2d.drawString("GSTIN: " + order.getStore().getGstinNumber(), 20, y);
        y += 20;

        // Order details
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString("Date: " + order.getCreatedOn().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")), 20, y);
        y += 15;

        g2d.drawString("Time: " + order.getOrderTime().format(DateTimeFormatter.ofPattern("hh:mm a")), 20, y);
        y += 20;

        g2d.drawString("Receipt #: " + order.getId(), 20, y);
        y += 30;

        // Table Header
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.drawString("Item Name", 20, y);
        g2d.drawString("Qty", 200, y);
        g2d.drawString("Amt", 300, y);
        y += 15;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g2d.setColor(new Color(0, 0, 0, 100)); // Light grey
        g2d.drawLine(20, y, 380, y);
        y += 10;

        g2d.setColor(Color.BLACK);

        // Items
        List<OrderItemResponse> items = order.getOrderItems();
        for (OrderItemResponse item : items) {
            g2d.drawString(item.getProduct().getName(), 20, y);
            g2d.drawString(String.valueOf(item.getQuantity()), 200, y);
            g2d.drawString(item.getAmountIncludingGst().toString(), 300, y);
            y += 20;
        }

        // Subtotals
        y += 10;
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2d.drawString("Sub Total:", 200, y);
        g2d.drawString(order.getTotalAmountExcludingGst().toString(), 300, y);
        y += 15;

        g2d.drawString("CGST (5%):", 200, y);
        g2d.drawString(order.getTotalGst().divide(BigDecimal.valueOf(2)).toString(), 300, y);
        y += 15;

        g2d.drawString("SGST (5%):", 200, y);
        g2d.drawString(order.getTotalGst().divide(BigDecimal.valueOf(2)).toString(), 300, y);
        y += 15;

        g2d.drawString("Discount:", 200, y);
        g2d.drawString(order.getTotalDiscount().toString(), 300, y);
        y += 15;

        g2d.drawString("Total Amount:", 200, y);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.drawString(order.getTotalAmountIncludingGst().toString(), 300, y);
        y += 20;

        // Footer
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g2d.setColor(new Color(0, 0, 0, 60)); // Light grey
        g2d.drawLine(20, y, 380, y);
        y += 15;

        g2d.setColor(Color.BLACK);
        g2d.drawString("Customer Copy", 20, y);
        y += 15;
        g2d.drawString("Please visit again!", 20, y);

        g2d.dispose();
        return image;
    }
}
