package com.example.springbootimagecompressor.img;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImgService {

    private final ImgRepository imgRepository;

    private final String FOLDER_PATH="/home/emon/Newroz_dev/SpringBootImageCompressor/images";

    public ImgService(ImgRepository imgRepository) {
        this.imgRepository = imgRepository;
    }

    public String saveCompressedImg(MultipartFile img, String title) throws IOException {
        Img imageData = imgRepository.save(Img.builder()
                .title(title)
                .imgData(ImgUtils.compressImage(img.getBytes())).build());

        return "file uploaded successfully : " + title;
    }

}
