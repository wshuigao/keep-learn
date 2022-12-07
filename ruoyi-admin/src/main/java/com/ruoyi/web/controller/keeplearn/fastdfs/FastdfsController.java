package com.ruoyi.web.controller.keeplearn.fastdfs;

import com.ruoyi.common.core.domain.AjaxResult;

import com.ruoyi.common.fastdfs.FastDFSClient;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * fastdfs上传文件相关控制类
 * 
 * @author wh
 */
@RestController
@RequestMapping("/fdfs")
public class FastdfsController {
    private static final Logger log = LoggerFactory.getLogger(FastdfsController.class);

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * 下载
     */
    @GetMapping("/download")
    public void fileDownload(String fileUrl, HttpServletResponse response) {
        String suffix = fileUrl.substring(fileUrl.lastIndexOf("."));
        byte[] bytes = new byte[0];
        try {
            bytes = fastDFSClient.downloadFile(fileUrl);
            response.reset();
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("fastDFS" + suffix, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
        } catch (IOException e) {
            log.error("fastDFS 下载文件失败------fileUrl:{}" , fileUrl);
        }
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception {
        String result ="";
        try {
            byte[] bytes = file.getBytes();
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            long fileSize = file.getSize();
            result = fastDFSClient.uploadFile(bytes, fileSize, extension);
        }catch (Exception e) {
            log.error("fastDFS 文件上传失败------fileName:{}" , file.getName());
        }
        return AjaxResult.success(result);
    }


    /**
     * 删除
     */
    @GetMapping("/del")
    public AjaxResult delFile(String fileUrl) throws Exception {
        fastDFSClient.delResource(fileUrl);
        return AjaxResult.success("删除成功");
    }

}
