package com.backend.file_vault.service;

import java.io.IOException;
import java.io.InputStream;

public interface FileStorageService {
    void uploadFile(String storedFilename, InputStream inputStream, long size, String contentType) throws IOException;
    InputStream downloadFile(String StoredFilename, String originalFilename) throws IOException;
    void deleteFile(String StoredFilename) throws IOException;
}
