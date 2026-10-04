package com.backend.file_vault.service.impl;

import com.backend.file_vault.exception.storageExceptions.StorageException;
import com.backend.file_vault.service.FileStorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }

    @Override
    public void uploadFile(String storedFilename, InputStream inputStream, long size, String contentType) {
        this.initBucket();
        try {
            PutObjectArgs object = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storedFilename)
                    .stream(inputStream, size, -1L)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(object);
        } catch (Exception e) {
            throw new StorageException("Failed to upload file");
        }
    }

    @Override
    public InputStream downloadFile(String storedFilename, String originalFilename) {
        try {
            GetObjectArgs object = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storedFilename)
                    .build();
            return minioClient.getObject(object);
        } catch (Exception e) {
            throw new StorageException("Failed to download file");
        }
    }

    @Override
    public void deleteFile(String StoredFilename) {
        try {
            RemoveObjectArgs object = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(StoredFilename)
                    .build();
            minioClient.removeObject(object);
        } catch (Exception e) {
            throw new StorageException("Failed to delete file");
        }
    }
}
