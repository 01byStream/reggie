package com.bys.reggie.controller;

import com.bys.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Administrator
 * @version 1.0
 * @description: 完成文件上传下载
 * @date 2022/9/10 10:25
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.filePath}")
    private String basePath;

    /**
     * @description: 文件上传
     * @param file 前端文件input框的name参数，
     *             file是一个临时文件，需要存到其他位置，否则本次请求结束文件就被删除了
     * @return R<String>
     * @author Administrator
     * @date 2022/9/10 10:28
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //获取原始文件名后缀，包括一个点
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成文件名，防止文件名重复
        String fileName = UUID.randomUUID() + suffix;
        //判断目录是否存在，不存在则创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //转存到配置文件指定的位置
        try {
            file.transferTo(new File(basePath, fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName); //返回文件名
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流读取文件
            FileInputStream inputStream = new FileInputStream(new File(basePath, name));
            //设置响应格式为图片
            response.setContentType("image/jpeg");
            //输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
