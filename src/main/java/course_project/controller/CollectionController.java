package course_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import course_project.service.CollectionService;
import course_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {


    private final UserService userService;
    private final CollectionService collectionService;

    @PostMapping("/add")
    public HttpEntity<?> newCollection(
            @RequestParam String name,
            @RequestParam String topic,
            @RequestParam MultipartFile image,
            @RequestParam String description,
            @RequestParam String fields
    ){
        try{
            collectionService.addCollection(name, topic, image, description, fields);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (JsonProcessingException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
