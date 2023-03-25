package com.service;

import com.common.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UpLoadService {
    ResponseResult upLoadImg(MultipartFile img);
}
