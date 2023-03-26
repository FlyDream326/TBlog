package com.service;

import com.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UpLoadService {
    ResponseResult upLoadImg(MultipartFile img);
}
