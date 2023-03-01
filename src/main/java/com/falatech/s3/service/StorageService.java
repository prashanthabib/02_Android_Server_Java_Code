package com.falatech.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import org.json.JSONObject;
import java.io.*;

@Service
@Slf4j
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public  ResponseEntity<HttpStatus> uploadImage(MultipartFile file, String customerFolderName, String date, String fileName){
    try{
        File fileObj = convertMultiPartFileToFile(file);
        String path = customerFolderName+"/"+date+"/"+fileName;
        s3Client.putObject(new PutObjectRequest(bucketName,path,fileObj));
        fileObj.delete();
        return new ResponseEntity<>(HttpStatus.OK);
    }catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    }

    public  ResponseEntity<HttpStatus> uploadStartStopImage(MultipartFile file, String fileName){
        try{
            File fileObj = convertMultiPartFileToFile(file);
            String path = fileName+".JPG";
            s3Client.putObject(new PutObjectRequest("custom-labels-console-ap-south-1",path,fileObj));
            fileObj.delete();
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    public ResponseEntity<HttpStatus> uploadFCMToken(MultipartFile file, String customerFolderName){
        try{
            File fileObj = convertMultiPartFileToFile(file);
            String path = customerFolderName+"/"+"token.txt";
            s3Client.putObject(new PutObjectRequest(bucketName,path,fileObj));
            fileObj.delete();
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }


    }

    public ResponseEntity<HttpStatus> createCustomerFolder(String customerFolderName){
        try{

            JSONObject customerIdObj = new JSONObject(customerFolderName);

            ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, customerIdObj.getString("customerId")+"/");

            if(result.getKeyCount() > 0){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            else{
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(0);

                InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

                s3Client.putObject(new PutObjectRequest(bucketName,customerIdObj.getString("customerId")+"/",emptyContent, metadata));

                return new ResponseEntity<>(HttpStatus.OK);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

}
