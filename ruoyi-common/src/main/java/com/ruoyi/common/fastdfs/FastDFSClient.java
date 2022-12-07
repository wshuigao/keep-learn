package com.ruoyi.common.fastdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FastDFSClient {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    /**
     * 文件上传
     *
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @return fastDfs路径
     */
    public String uploadFile(byte[] bytes, long fileSize, String extension) {
        System.out.println("fastDFS文件上传开始------");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        StorePath storePath = fastFileStorageClient.uploadFile(byteArrayInputStream, fileSize, extension, null);
        String fullPath = storePath.getFullPath();
        System.out.println("fastDFS文件上传完成------");
        return fullPath;
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件URL
     * @return 文件字节
     */
    public byte[] downloadFile(String fileUrl) {
        int group = fileUrl.indexOf("group");
        //获取到文件名
        String fileName = fileUrl.substring(group);
        String groupName = fileName.substring(0, fileName.indexOf("/"));
        String pathName = fileName.substring(fileName.indexOf("/")+1);

        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(groupName, pathName, downloadByteArray);
        return bytes;
    }


    /**
     * 删除文件
     * @param fileUrl
     * @throws IOException
     */
    public void delResource(String fileUrl) throws IOException {
        int group = fileUrl.indexOf("group");
        //获取到文件名
        String fileName = fileUrl.substring(group);
        String groupName = fileName.substring(0, fileName.indexOf("/"));
        String pathName = fileName.substring(fileName.indexOf("/"));

//        fastFileStorageClient.deleteFile(groupName, pathName);

        fastFileStorageClient.deleteFile(fileName);
        System.out.println(pathName+"文件已删除");
    }


}
