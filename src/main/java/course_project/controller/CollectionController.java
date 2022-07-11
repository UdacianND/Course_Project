package course_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import course_project.service.CollectionService;
import course_project.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/collections")
@CrossOrigin
@RequiredArgsConstructor
public class CollectionController {


    private final CollectionService collectionService;
    private final ItemService itemService;

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
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/edit/{id}")
    public HttpEntity<?> editCollection(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String topic,
            @RequestParam @Nullable MultipartFile image,
            @RequestParam String description
    ){
        try{
            collectionService.editCollection(id,name, topic, image, description);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("delete")
    public ResponseEntity<?> deleteCollection(@RequestBody Long id){
        try{
            itemService.deleteCollection(id);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("my-collections")
    public ResponseEntity<?> getMyCollections(){
        try {
            String data = collectionService.getUserCollections();
            return ResponseEntity.ok().body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("fields/{collectionId}")
    public ResponseEntity<?> getItemFields(@PathVariable Long collectionId){
        try {
            String data = collectionService.getItemFields(collectionId);
            return ResponseEntity.ok().body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("public/{collectionId}")
    public ResponseEntity<?> getCollection(@PathVariable Long collectionId){
        try {
            String data = collectionService.getCollectionInfo(collectionId);
            return ResponseEntity.ok().body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("public/topics/{name}")
    public ResponseEntity<?> getTopics(@PathVariable String name){
        try {
            String data = collectionService.getTopics(name);
            return ResponseEntity.ok().body(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("public/topCollections")
    public ResponseEntity<?> getTopCollections(){
        try {
            String data = collectionService.topCollections();
            return ResponseEntity.ok().body(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

}
