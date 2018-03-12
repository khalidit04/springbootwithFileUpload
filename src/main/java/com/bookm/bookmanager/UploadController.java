package com.bookm.bookmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping(value = "/")
public class UploadController {

    private static final String BASE_PATH="images";

    @Autowired
    private ImageService imageService;

    private static final String FILE_NAME="{filename:.+}";

    @RequestMapping(method = RequestMethod.GET, value = BASE_PATH+"/"+FILE_NAME+"/raw")
    @ResponseBody
    public ResponseEntity<?> onRawImage(@PathVariable String filename) throws IOException {

            Resource file=imageService.findOneImage(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).contentLength(file.contentLength()).body(new InputStreamResource(file.getInputStream()));

    }

    @RequestMapping(value = "/")
    public String welcome(Model model, Pageable pageable){
            final Page<Image> page=imageService.findPage(pageable);
            model.addAttribute("page",page);
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST,value = BASE_PATH)
    @ResponseBody
    public ResponseEntity<?> createImageFile(@RequestParam("file")MultipartFile file, HttpServletRequest request){

        try {
            imageService.createImage(file);
            final URI locationURI=new URI(request.getRequestURL().toString()+"/").resolve(file.getOriginalFilename()+"/raw");

            return ResponseEntity.created(locationURI).body("successfully upload"+file.getOriginalFilename());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload"+file.getOriginalFilename()+"=>"+e.getMessage());
        }
    }


    @RequestMapping(method = RequestMethod.DELETE,value=BASE_PATH+"/"+FILE_NAME)
    @ResponseBody
    public ResponseEntity<?> deleteImage(@PathVariable String filename){
        try {
            imageService.deleteImage(filename);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Successfully deleted "+filename);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete "+filename);
        }

    }



    @RequestMapping(method = RequestMethod.POST,value = "images/upload")
    public String singleFileUpload(@RequestParam("filename") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }

            try {
                imageService.createImage(file);
                final URI locationURI=new URI("/").resolve(file.getOriginalFilename()+"/raw");

                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded '" + file.getOriginalFilename() + "'");
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message",
                        "Failed to upload '" + file.getOriginalFilename() + "'");
            }


        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}
