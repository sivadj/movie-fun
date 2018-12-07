package org.superbiz.moviefun.blobstore;

import org.springframework.http.MediaType;

import java.io.InputStream;

public class Blob {
    public String getName() {
        return name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public final String name;
    public final InputStream inputStream;
    public final String contentType;

    public Blob(String name, InputStream inputStream, String contentType) {
        this.name = name;
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    public Blob(){
        this.name = "default-cover.jpg";

        ClassLoader classLoader = getClass().getClassLoader();

        this.inputStream = classLoader.getResourceAsStream("default-cover.jpg");
        this.contentType = MediaType.ALL_VALUE;

    }
}
