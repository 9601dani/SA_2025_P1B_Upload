package com.codenbugs.ms_upload.controllers;

import com.codenbugs.ms_upload.exceptions.NotCreatedException;
import com.codenbugs.ms_upload.services.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadController.class)
class UploadControllerTest {

    public static final String PATH = "test-path";
    public static final String FILENAME = "test-name";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadService uploadService;

    @Test
    void uploadFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.pdf", "application/pdf", "some-image-content".getBytes());
        when(uploadService.uploadFile(multipartFile, PATH)).thenReturn(FILENAME);

        mockMvc.perform(multipart("/v1/uploads/documents")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void uploadImage() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.png", "image/png", "some-image-content".getBytes());
        when(uploadService.uploadFile(multipartFile, PATH)).thenReturn(FILENAME);

        mockMvc.perform(multipart("/v1/uploads/images")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void testUploadFile() throws NotCreatedException {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.png", "image/png", "some-image-content".getBytes());
        when(uploadService.uploadFile(multipartFile, PATH)).thenReturn(FILENAME);

        String actual = this.uploadService.uploadFile(multipartFile, PATH);
        assertEquals(FILENAME, actual);

    }

    @Test
    void getImageAsBase64Success() throws Exception {
        String fileKey = "test-image.png";
        String base64Image = "data:image/png;base64,abc123";

        when(uploadService.getFileAsBase64(fileKey)).thenReturn(base64Image);

        mockMvc.perform(get("/v1/uploads/images/base64")
                        .param("fileKey", fileKey))
                .andExpect(status().isOk())
                .andExpect(content().string(base64Image));
    }

    @Test
    void getImageAsBase64_NotFound() throws Exception {
        String fileKey = "non-existent.png";

        when(uploadService.getFileAsBase64(fileKey)).thenThrow(new IOException("Archivo no encontrado"));

        mockMvc.perform(get("/v1/uploads/images/base64")
                        .param("fileKey", fileKey))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se pudo cargar la imagen: Archivo no encontrado"));
    }
}