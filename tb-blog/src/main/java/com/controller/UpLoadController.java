package com.controller;

import com.domain.ResponseResult;
import com.service.UpLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UpLoadController {
    @Autowired
    private UpLoadService upLoadService;
    @PostMapping("/upload")
    public ResponseResult upLoadImg(MultipartFile img){
        return upLoadService.upLoadImg(img);
    }
}
