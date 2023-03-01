package com.falatech.s3.controller;

import com.falatech.s3.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins="*")
public class StorageController {

    @Autowired
    private StorageService service;

    @PostMapping("/createCustomerFolder")
    public ResponseEntity<HttpStatus> createCustomerFolder(@RequestBody String customerFolderName){
        return service.createCustomerFolder(customerFolderName);
    }


    @PostMapping("/uploadImage/{customerFolderName}/{date}/{fileName}")
    public ResponseEntity<HttpStatus> uploadImage(@RequestParam(value = "file") MultipartFile file,
                                              @PathVariable ("customerFolderName") String customerFolderName,
                                              @PathVariable ("date") String date,
                                              @PathVariable ("fileName") String fileName
                                              ){
        return service.uploadImage(file, customerFolderName, date, fileName);
    }

    @PostMapping("/uploadStartStopImage/{fileName}")
    public ResponseEntity<HttpStatus> uploadStartStopImage(@RequestParam(value = "file") MultipartFile file,
                                                           @PathVariable ("fileName") String fileName
    ){
        return service.uploadStartStopImage(file, fileName);
    }

    @PostMapping("/uploadFCMToken/{customerFolderName}")
    public ResponseEntity<HttpStatus> uploadFCMToken(@RequestParam(value = "file") MultipartFile file,
                                              @PathVariable ("customerFolderName") String customerFolderName
    ){
        return service.uploadFCMToken(file, customerFolderName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }

}
