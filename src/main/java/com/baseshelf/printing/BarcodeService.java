package com.baseshelf.printing;

import com.baseshelf.product.Product;
import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class BarcodeService {

    /**
     * Generate a barcode PNG image and save it to a file.
     *
     * @param barcodeText The text to encode as an EAN-13 barcode.
     * @param outputFile  The file to save the barcode image.
     * @throws Exception If there is an error during barcode generation.
     */
    public void createBarcodeImage(String barcodeText, File outputFile) throws Exception {
        BufferedImage barcodeImage = generateCode128BarcodeImage(barcodeText);
        if (!outputFile.getName().endsWith(".png")) {
            throw new IllegalArgumentException("Output file must have a .png extension.");
        }
        ImageIO.write(barcodeImage, "png", outputFile);
    }

    public BufferedImage generateCode128BarcodeImage(String barcodeText) throws BarcodeException, OutputException {
        Barcode barcode = BarcodeFactory.createCode128(barcodeText);
        return BarcodeImageHandler.getImage(barcode);
    }

    public BufferedImage createProductLabel(Product product) throws BarcodeException, OutputException {
        int labelWidth = 196;
        int lableHeight = 120;

        BufferedImage bufferedImage = new BufferedImage(labelWidth, lableHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(Color.white);
        graphics2D.fillRect(0, 0, labelWidth, lableHeight);

        // Draw the product name
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font("Arial", Font.ITALIC, 12));
        graphics2D.drawString("Product: " + product.getName(), 20, 20);

        // Draw the price
        graphics2D.setFont(new Font("Arial", Font.PLAIN, 12));
        graphics2D.drawString("Price: $" + product.getSellingPrice(), 20, 35);

        //Draw the brand
        graphics2D.setFont(new Font("Arial", Font.PLAIN, 12));
        graphics2D.drawString("Brand: " + product.getBrand().getName(), 20, 50);

        String plainText = product.getId().toString();

        int x = labelWidth/2 -  180/2;
        // Generate and draw the barcode
        BufferedImage barcodeImage = generateCode128BarcodeImage(plainText);
        graphics2D.drawImage(barcodeImage, x, 60, 180 , 35 ,null);

        Font font = new Font("ARIAL", Font.PLAIN, 14);
        FontMetrics metrics = graphics2D.getFontMetrics(font);
        graphics2D.setFont(font);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(plainText, labelWidth/2 - metrics.stringWidth(plainText)/2, 110);

        // Clean up
        graphics2D.dispose();

        return bufferedImage;
    }

    public BufferedImage createBarcode(String plainText) throws OutputException, BarcodeException {
        int labelWidth  = 192;
        int labelHeight = 80;

        BufferedImage bufferedImage = new BufferedImage(labelWidth, labelHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.fillRect(0, 0, labelWidth, labelHeight);

        int x = labelWidth/2 - 180/2;
        // Generate and draw the barcode
        BufferedImage barcodeImage = generateCode128BarcodeImage(plainText);
        graphics2D.drawImage(barcodeImage, x, 10, 180 , 45,null);

        Font font = new Font("ARIAL", Font.PLAIN, 14);
        FontMetrics metrics = graphics2D.getFontMetrics(font);
        graphics2D.setFont(font);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(plainText, labelWidth/2 - metrics.stringWidth(plainText)/2, 70);

        graphics2D.dispose();

        return bufferedImage;
    }
}
