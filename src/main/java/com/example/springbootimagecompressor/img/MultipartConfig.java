package com.example.springbootimagecompressor.img;


import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
    public class MultipartConfig {

        @Bean
        public MultipartConfigElement multipartConfigElement() {
            MultipartConfigFactory factory = new MultipartConfigFactory();
            factory.setMaxFileSize(DataSize.parse("30MB"));
            factory.setMaxRequestSize(DataSize.parse("30MB"));
            return factory.createMultipartConfig();
        }
    }

