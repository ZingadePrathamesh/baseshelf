package com.baseshelf.barcode;

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
        int labelWidth = 240;
        int lableHeight = 120;

        BufferedImage bufferedImage = new BufferedImage(labelWidth, lableHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setBackground(Color.white);
        graphics2D.fillRect(0, 0, labelWidth, lableHeight);

        // Draw the product name
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font("Arial", Font.BOLD, 14));
        graphics2D.drawString("Product: " + product.getName(), 20, 20);

        // Draw the price
        graphics2D.setFont(new Font("Arial", Font.PLAIN, 12));
        graphics2D.drawString("Price: $" + product.getSellingPrice(), 20, 40);

        //Draw the brand
        graphics2D.setFont(new Font("Arial", Font.PLAIN, 12));
        graphics2D.drawString("Brand: " + product.getBrand().getName(), 20, 60);

        int x = labelWidth/2 -  150/2;
        // Generate and draw the barcode
        BufferedImage barcodeImage = generateCode128BarcodeImage(product.getId().toString());
        graphics2D.drawImage(barcodeImage, x, 80, 150 , 30 ,null);

        // Clean up
        graphics2D.dispose();

        return bufferedImage;
    }
}
