package com.example.springbootimagecompressor.img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import com.example.springbootimagecompressor.img.Img;
import com.example.springbootimagecompressor.img.ImgRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImgService {

    private final ImgRepository imgRepository;
    private final String FOLDER_PATH = "/home/emon/Newroz_dev/SpringBootImageCompressor/images";
    private final int MAX_FILE_SIZE_KB = 100;

    public ImgService(ImgRepository imgRepository) {
        this.imgRepository = imgRepository;
    }

    public String saveCompressedAndResizedImg(MultipartFile img, String title) throws IOException {

        if (imgRepository.existsByTitle(title)){
            throw new EOFException();
        }


        // Read the original image
        BufferedImage originalImage = ImageIO.read(img.getInputStream());

        double scaleFactor = 0.5; // 50% scale
        int targetWidth = (int) (originalImage.getWidth() * scaleFactor);
        int targetHeight = (int) (originalImage.getHeight() * scaleFactor);  BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        resizedImage.createGraphics().dispose();

        // Compress the resized image
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();

        // Determine the image format based on the original file name
        String fileExtension = getFileExtension(img.getOriginalFilename());
        ImageIO.write(resizedImage, fileExtension, compressedStream);

        // Check if the compressed image size is less than 100KB
        if (compressedStream.size() > MAX_FILE_SIZE_KB * 1024) {
            throw new IOException("Compressed image size exceeds 100KB limit.");
        }

        // Save to the database
        Img imageData = imgRepository.save(Img.builder()
                .title(title)
                .imgData(compressedStream.toByteArray())
                .build());

        // Save to the file with the correct file extension
        String filePath = FOLDER_PATH + "/" + title + "." + fileExtension;
        FileUtils.writeByteArrayToFile(new File(filePath), compressedStream.toByteArray());

        return "File uploaded successfully: " + title;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
