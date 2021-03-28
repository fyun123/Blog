package com.whut.blog.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.UUID;

@RestController
public class OssController {
    @Resource
    private OSSClient ossClient;

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.access-key}")
    private String accessId;

    @RequestMapping("/oss/uploadfile")
//    @RequestBody
    public JSONObject policy(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(value = "editormd-image-file", required = false) MultipartFile attach) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "text/html");
        JSONObject jsonObject=new JSONObject();
        try {
            String bucket = "blog-fyun"; // 请填写您的 bucketname 。
            String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String dir = format + "/"; // 用户上传文件时指定的前缀。
            UUID uuid = UUID.randomUUID();

            InputStream inputStream = new ByteArrayInputStream(attach.getBytes());
            ossClient.putObject(bucket, dir + uuid + "_" + attach.getOriginalFilename(),inputStream);

            jsonObject.put("success", 1);
            jsonObject.put("message", "上传成功");
            String originalFilename = attach.getOriginalFilename();

            jsonObject.put("url", host+"/"+dir+uuid + "_" + URLEncoder.encode(originalFilename));
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("fail", 0);
            System.out.println(e.getMessage());

        } finally {
            ossClient.shutdown();

        }
//        return R.ok().put("data",respMap);
        return jsonObject;
    }
}
