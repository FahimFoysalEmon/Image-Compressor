package com.example.springbootimagecompressor.img;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ImgController {

    private final ImgService imgService;

    public ImgController(ImgService imgService) {
        this.imgService = imgService;
    }


    @PostMapping(value = "/img/compressor")
    public String saveCompressedImg(@RequestParam MultipartFile img,
                                    @RequestParam String title) throws IOException {
        System.out.println(img);
        return imgService.saveCompressedAndResizedImg(img, title);
    }


    @GetMapping(value = "/img/compressor/all")
    public List<String> saveCompressedImg() {
        return imgService.getAllImages();
    }

}
