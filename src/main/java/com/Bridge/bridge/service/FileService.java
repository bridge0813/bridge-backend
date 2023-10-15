package com.Bridge.bridge.service;

import com.Bridge.bridge.domain.File;
import com.Bridge.bridge.exception.badrequest.FileDeleteException;
import com.Bridge.bridge.exception.badrequest.FileUploadException;
import com.Bridge.bridge.exception.notfound.NotFoundFileException;
import com.Bridge.bridge.repository.FileRepository;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3Client amazonS3Client;

    private final FileRepository fileRepository;


    /**
     * 파일 S3에 업로드 저장
     */
    @Transactional
    private Long uploadFile(MultipartFile file) {

        String uploadFilePath =  "bridge/" + getFolderName();
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            InputStream inputStream = file.getInputStream();
            String keyName = uploadFilePath + "/" + getUuidFileName(file.getOriginalFilename());

            //s3에 폴더 및 파일 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata));

            uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();

            File newFile = File.builder()
                    .uploadFileUrl(uploadFileUrl)
                    .keyName(keyName)
                    .originName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .build();

            File saveFile = fileRepository.save(newFile);
            return saveFile.getId();

        } catch (IOException | AmazonS3Exception e) {
            throw new FileUploadException();
        }
    }

    /**
     * S3에 업로드한 파일 삭제
     */
    @Transactional
    public String deleteFile(Long fileId) {
        File findFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundFileException());

        fileRepository.delete(findFile);

        try {
            String keyName = findFile.getKeyName();
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, keyName);

            if(isObjectExist) {
                amazonS3Client.deleteObject(bucketName, keyName);
                return "File Delete Success";
            }
            else {
                return "File Delete Failed";
            }
        } catch (AmazonS3Exception e) {
            throw new FileDeleteException();
        }
    }

    // UUID 이용 파일 이름 반환
    private String getUuidFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 파일 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // 날짜(년/월/일)로 폴더명 설정
    private String getFolderName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String strDate = dateFormat.format(date);
        return strDate.replace("-", "/");
    }
}
