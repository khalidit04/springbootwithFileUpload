package com.bookm.bookmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {

    private final String UPLOAD_ROOT="upload-dir";

    private ImageRepository imageRepository;
    private ResourceLoader resourceLoader;

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader resourceLoader) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
    }

    public Resource findOneImage(String fileName){
        return this.resourceLoader.getResource("file:"+UPLOAD_ROOT+"/"+fileName);
    }

    public void createImage(MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT,file.getOriginalFilename()));
            imageRepository.save(new Image(file.getOriginalFilename()));
        }
    }

    public void deleteImage(String fileName) throws IOException {
        final Image byName=imageRepository.findByName(fileName);
        imageRepository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT,fileName));
    }

    @Bean
//    @Profile("dev")
    CommandLineRunner setUp(ImageRepository repository) throws IOException {
        return (args)->{
            FileSystemUtils.deleteRecursively(new java.io.File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));
            FileCopyUtils.copy("Test File",new FileWriter(UPLOAD_ROOT+"/test"));
            imageRepository.save(new Image("test"));
            FileCopyUtils.copy("Test File2",new FileWriter(UPLOAD_ROOT+"/test2"));
            imageRepository.save(new Image("test2"));

            FileCopyUtils.copy("Test File3",new FileWriter(UPLOAD_ROOT+"/test3"));
            imageRepository.save(new Image("test3"));

        };

    }
}
