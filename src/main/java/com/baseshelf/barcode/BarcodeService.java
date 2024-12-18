package com.baseshelf.barcode;

import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class BarcodeService {

    @Bean
    @Order(value = 8)
    public CommandLineRunner generateBarcode(){
        return args -> {
            String barcodeText = "6jdhvjkadjva9"; // Example barcode value
            File outputFile = new File("EAN13Barcode.png");

            try {
                createBarcodeImage(barcodeText, outputFile);
                System.out.println("Barcode image successfully created: " + outputFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Error while generating barcode: " + e.getMessage());
            }
        };
    }

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
//
    public BufferedImage generateCode128BarcodeImage(String barcodeText) throws Exception {
        Barcode barcode = BarcodeFactory.createCode128(barcodeText);
        return BarcodeImageHandler.getImage(barcode);
    }

//    /**
//     * Generate a BufferedImage for an EAN-13 barcode.
//     *
//     * @param barcodeText The text to encode as an EAN-13 barcode.
//     * @return A BufferedImage representing the barcode.
//     * @throws Exception If there is an error during barcode generation.
//     */
//    public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
//        if (barcodeText.length() != 13 || !barcodeText.matches("\\d+")) {
//            throw new IllegalArgumentException("Barcode must be a 13-digit number.");
//        }
//
//        EAN13Bean barcodeGenerator = new EAN13Bean();
//        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
//                300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
//
//        barcodeGenerator.generateBarcode(canvas, barcodeText);
//        canvas.finish();
//        return canvas.getBufferedImage();
//    }
}
