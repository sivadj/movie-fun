package org.superbiz.moviefun.blobstore;

import com.amazonaws.util.IOUtils;
import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.albums.AlbumsBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {


    private final Tika tika = new Tika();


    @Override
    public void put(Blob blob) throws IOException {
        // ...

        File file = new File(blob.name);

        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        try(FileOutputStream outputStream = new FileOutputStream(file)){
            IOUtils.copy(blob.inputStream,outputStream);
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        // ...

        File file = new File(name);

        if (file.exists()){

            Blob blob = new Blob(name, new FileInputStream(file), tika.detect(file));

            return Optional.of(blob);

        } else{
            return Optional.empty();
        }


    }

    @Override
    public void deleteAll() {
        // ...
    }



    public void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, File targetFile) throws IOException {
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(uploadedFile.getBytes());
        }
    }

    public HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(coverFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    public File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    public Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get("classpath:default-cover.jpg");
        }

        return coverFilePath;
    }
}