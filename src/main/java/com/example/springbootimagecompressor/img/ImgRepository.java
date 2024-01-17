package com.example.springbootimagecompressor.img;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgRepository extends JpaRepository<Img, String> {

    boolean existsByTitle(String title);
}
