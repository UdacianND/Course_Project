package course_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import course_project.payload.response.CollectionDto;
import course_project.service.CollectionService;
import course_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            @RequestParam @Nullable MultipartFile image,
            @RequestParam String description,
            @RequestParam @Nullable String fields
    ){
        try{
            collectionService.addCollection(name, topic, image, description, fields);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (JsonProcessingException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("my-collections")
    public ResponseEntity<?> getMyCollections(){
        try {
            String data = collectionService.getUserCollections();
            return ResponseEntity.ok().body(data);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
