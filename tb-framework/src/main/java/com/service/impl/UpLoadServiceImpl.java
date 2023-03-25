package com.service.impl;

import com.common.ResponseResult;
import com.enums.AppHttpCodeEnum;
import com.exception.SystemException;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.service.UpLoadService;
import com.utils.PathUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Component
@Service
@ConfigurationProperties(prefix = "oss")
public class UpLoadServiceImpl implements UpLoadService {

    private String accessKey ;
    private String secretKey ;
    private String bucket ;

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public ResponseResult upLoadImg(MultipartFile img) {
        // 判断文件类型或文件大小
        //获取原始文件名
        String originalFilename = img.getOriginalFilename();
        //对原始文件名进行判断
        if(!originalFilename.endsWith(".png")){
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }

        //判断通过 则上传文件到oss
        String filePath = PathUtils.generateFilePath(originalFilename);
        String url = upLoadOss(img,filePath);//
        return ResponseResult.okResult(url);
    }



    public  String upLoadOss(MultipartFile imgFile,String fileName){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
//        String accessKey = "accessKey";
//        String secretKey = "secretKey";
//        String bucket = "tyy-blog";

//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        String http = "http";
        String yuMing = "rrtaqharu.bkt.clouddn.com";
        try {
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
//            InputStream inputStream =
//                    new FileInputStream("C:\\Users\\fly\\Pictures\\1657815094083.png");
            InputStream inputStream = imgFile.getInputStream();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                //外链
                return http+"://"+yuMing+"/"+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }


        return "123";
    }
}
