package com.bookm.bookmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class UploadController {

    private static final String BASE_PATH="/images";

    @Autowired
    private static  ImageService imageService;

    private static final String FILE_NAME="{filename:.+}";

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH+"/"+FILE_NAME+"/raw")
    @ResponseBody
    public ResponseEntity<?> onRawImage(@PathVariable String filename) throws IOException {

            Resource file=imageService.findOneImage(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).contentLength(file.contentLength()).body(new InputStreamResource(file.getInputStream()));

    }
}
