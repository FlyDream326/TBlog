package com.controller;

import com.domain.ResponseResult;
import com.service.UpLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @Autowired
    private UpLoadService upLoadService;
    @PostMapping("/upload")
    public ResponseResult upload(@RequestParam("img")  MultipartFile file){
        return upLoadService.upLoadImg(file);
    }


}
