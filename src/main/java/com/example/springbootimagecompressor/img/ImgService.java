package com.example.springbootimagecompressor.img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImgService {

    private final ImgRepository imgRepository;
    private final String FOLDER_PATH = "/home/emon/Newroz_dev/SpringBootImageCompressor/images";
    private final int MAX_FILE_SIZE_KB = 200;

    public ImgService(ImgRepository imgRepository) {
        this.imgRepository = imgRepository;
    }

    public String saveCompressedAndResizedImg(MultipartFile img, String title) throws IOException {

        if (imgRepository.existsByTitle(title)) {
            throw new IOException("File with the same title already exists.");
        }

        // Read the original image
        BufferedImage originalImage = ImageIO.read(img.getInputStream());

        // First step: Resize the image to 50% of the original size
        double firstStepScaleFactor = 0.5;
        int firstStepTargetWidth = (int) (originalImage.getWidth() * firstStepScaleFactor);
        int firstStepTargetHeight = (int) (originalImage.getHeight() * firstStepScaleFactor);
        BufferedImage resizedImageStep1 = resizeImage(originalImage, firstStepTargetWidth, firstStepTargetHeight);

        // Second step: Compress the resized image with dynamic compression level
        byte[] compressedImageData = compressImage(resizedImageStep1);

        // Save to the database
        Img imageData = imgRepository.save(Img.builder()
                .title(title)
                .imgData(compressedImageData)
                .build());

        // Save to the file with the correct file extension
        String fileExtension = getFileExtension(img.getOriginalFilename());
        String filePath = FOLDER_PATH + "/" + title + "." + fileExtension;
        FileUtils.writeByteArrayToFile(new File(filePath), compressedImageData);

        return "File uploaded successfully: " + title;
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        resizedImage.createGraphics().dispose();
        return resizedImage;
    }

    private byte[] compressImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();

        // Iterate over compression levels until the size is below the threshold
        for (float compressionQuality = 1.0f; compressionQuality >= 0.1f; compressionQuality -= 0.05f) {
            ImageIO.write(image, "jpg", compressedStream);

            if (compressedStream.size() <= MAX_FILE_SIZE_KB * 1024) {
                return compressedStream.toByteArray();
            }

            compressedStream.reset(); // Reset the stream for the next iteration
        }

        throw new IOException("Unable to compress image within size limit.");
    }
}
