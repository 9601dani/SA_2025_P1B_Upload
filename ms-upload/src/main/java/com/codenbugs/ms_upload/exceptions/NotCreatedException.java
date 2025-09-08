package com.codenbugs.ms_upload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotCreatedException extends UploadException {

    public NotCreatedException(String message) {
        super(message);
    }
}
