package com.codenbugs.ms_upload.services;

import com.codenbugs.ms_upload.exceptions.NotCreatedException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadServiceTest {

    private final String BUCKET_NAME = "test-bucket";
    private final String PATH = "test-path";

    @InjectMocks
    private UploadService uploadService;

    @Mock
    private Storage storage;


    @Mock
    private Bucket bucket;

    @Mock
    private MultipartFile file;

    @Mock
    private Blob blob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        uploadService = new UploadService(storage, BUCKET_NAME);
    }

    @Test
    void uploadFile() throws IOException, NotCreatedException {
        String objectName = PATH + "/" + UUID.randomUUID();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";


        when(storage.get(BUCKET_NAME)).thenReturn(bucket);
        when(bucket.create(objectName, fileBytes, contentType)).thenReturn(null);
        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        String result = uploadService.uploadFile(file, PATH);

        verify(bucket, times(1)).create(anyString(), any(byte[].class), anyString());
        assertTrue(result.startsWith(PATH + "/"));
    }

    @Test
    void shouldThrowFileNotCreatedException() throws IOException {
        when(file.getBytes()).thenThrow(new IOException());

        assertThrows(NotCreatedException.class, () -> uploadService.uploadFile(file, PATH));
    }

    @Test
    void getFileAsBase64Successfully() throws IOException {
        String objectName = "file.png";
        byte[] content = "fake-image-content".getBytes();
        String contentType = "image/png";
        String expectedBase64 = "data:" + contentType + ";base64," + java.util.Base64.getEncoder().encodeToString(content);

        when(storage.get(com.google.cloud.storage.BlobId.of(BUCKET_NAME, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.getContent()).thenReturn(content);
        when(blob.getContentType()).thenReturn(contentType);

        String actual = uploadService.getFileAsBase64(objectName);

        assertEquals(expectedBase64, actual);
    }

    @Test
    void getFileAsBase64ThrowsWhenBlobMissing() {
        String objectName = "missing-file.png";

        when(storage.get(com.google.cloud.storage.BlobId.of(BUCKET_NAME, objectName))).thenReturn(null);

        IOException exception = assertThrows(IOException.class, () -> {
            uploadService.getFileAsBase64(objectName);
        });

        assertEquals("El archivo no existe en el bucket.", exception.getMessage());
    }

    @Test
    void getFileAsBase64WithNullMimeType() throws IOException {
        String objectName = "file.bin";
        byte[] content = "binary-content".getBytes();
        String defaultMimeType = "application/octet-stream";
        String expectedBase64 = "data:" + defaultMimeType + ";base64," + java.util.Base64.getEncoder().encodeToString(content);

        when(storage.get(com.google.cloud.storage.BlobId.of(BUCKET_NAME, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.getContent()).thenReturn(content);
        when(blob.getContentType()).thenReturn(null);

        String actual = uploadService.getFileAsBase64(objectName);

        assertEquals(expectedBase64, actual);
    }

    @Test
    void getFileAsBase64ThrowsWhenBlobIsNull() {
        String objectName = "nonexistent-file.jpg";

        when(storage.get(com.google.cloud.storage.BlobId.of(BUCKET_NAME, objectName))).thenReturn(null); // Simula blob == null

        IOException exception = assertThrows(IOException.class, () -> {
            uploadService.getFileAsBase64(objectName);
        });

        assertEquals("El archivo no existe en el bucket.", exception.getMessage());
    }

    @Test
    void getFileAsBase64ThrowsWhenBlobDoesNotExist() {
        String objectName = "ghost-file.jpg";

        when(storage.get(com.google.cloud.storage.BlobId.of(BUCKET_NAME, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(false);

        IOException exception = assertThrows(IOException.class, () -> {
            uploadService.getFileAsBase64(objectName);
        });

        assertEquals("El archivo no existe en el bucket.", exception.getMessage());
    }





}