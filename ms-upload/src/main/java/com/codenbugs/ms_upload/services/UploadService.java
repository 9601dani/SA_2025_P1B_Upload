package com.codenbugs.ms_upload.services;

import com.codenbugs.ms_upload.exceptions.NotCreatedException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Bucket;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UploadService {

    private final Storage storage;
    private final String bucketName;

    public String uploadFile(MultipartFile file, String path) throws NotCreatedException {
        try {

            String objectName = path + "/" + UUID.randomUUID();
            Bucket bucket = storage.get(bucketName);
            bucket.create(objectName, file.getBytes(), file.getContentType());
            return objectName;
        } catch(IOException exception) {
            throw new NotCreatedException("No se pudo guardar la imagen");
        }
    }

    public String getFileAsBase64(String objectName) throws IOException {
        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        if (blob == null || !blob.exists()) {
            throw new IOException("El archivo no existe en el bucket.");
        }

        byte[] content = blob.getContent();

        String mimeType = blob.getContentType();
        if (mimeType == null) mimeType = "application/octet-stream";

        String base64 = Base64.getEncoder().encodeToString(content);
        return "data:" + mimeType + ";base64," + base64;
    }
}
