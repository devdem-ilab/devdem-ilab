package org.metrobank.itg.innolab.emftopng.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import com.aspose.imaging.Image;
import com.aspose.imaging.imageoptions.EmfRasterizationOptions;
import com.aspose.imaging.imageoptions.PngOptions;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

@Service
public class ConversionService {
    
    public String convertEmfToPng(String base64Emf) throws IOException {
        byte[] emfBytes = Base64.decodeBase64(base64Emf);
        File emfFile = File.createTempFile("input", ".emf");
        Files.write(emfFile.toPath(), emfBytes);

        File pngFile = File.createTempFile("output", ".png");
        String emfFileName = String.valueOf(emfFile.toPath());
        String pngFileName = String.valueOf(pngFile.toPath());

        // Image image = Image.load(emfFileName);

        // try {
        //     EmfRasterizationOptions rasterizationOptions = new EmfRasterizationOptions();
        //     rasterizationOptions.setPageWidth(image.getWidth());
        //     rasterizationOptions.setPageHeight(image.getHeight());

        //     PngOptions pngOptions = new PngOptions();
        //     pngOptions.setVectorRasterizationOptions(rasterizationOptions);

        //     image.save(pngFileName, pngOptions);
        // } finally {
        //     image.close();
        // }

        EMFInputStream emfInputStream = new EMFInputStream(new FileInputStream(new File(emfFileName)));
        
        int width =  (int) (emfInputStream.readHeader().getBounds().getWidth());
        int height = (int) (emfInputStream.readHeader().getBounds().getHeight());

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2d = bufferedImage.createGraphics();
        
        EMFRenderer emfRenderer = new EMFRenderer(emfInputStream);

        emfRenderer.paint(graphics2d);

        graphics2d.dispose();

        int newWidth = (int) (width * 1.5);
        int newHeight = (int) (height * 1.5);
            
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        ImageIO.write(resizedImage, "png", new File(pngFileName));
            
        try (FileInputStream fis = new FileInputStream(pngFile)){
            byte[] pngBytes = new byte[(int) pngFile.length()];
            fis.read(pngBytes);

            String base64Png = "data:image/png;base64," + Base64.encodeBase64String(pngBytes);
            //System.out.println(base64Png);

            return base64Png;
        }
    }
}
