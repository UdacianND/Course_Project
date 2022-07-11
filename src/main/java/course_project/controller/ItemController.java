package course_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import course_project.service.CommentService;
import course_project.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/item")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping("add")
    public ResponseEntity<?> add(
            @RequestParam Long collectionId,
            @RequestParam String name,
            @RequestParam @Nullable String tags,
            @RequestParam @Nullable MultipartFile image,
            @RequestParam @Nullable String fields
            ){
        try {
            itemService.add(collectionId,name, tags, image, fields);
            return ResponseEntity.ok(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("public/tags/{name}")
    public ResponseEntity<?> getTags(@PathVariable @Nullable String name) {
        try {
            return ResponseEntity.ok(itemService.getTagsByName(name));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("public/getByCollection/{collectionId}")
    public ResponseEntity<?> getItems(@PathVariable Long collectionId) throws JsonProcessingException {
        String data = itemService.getItemsByCollection(collectionId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("public/get/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id){
        try {
            return ResponseEntity.ok(itemService.getItem(id));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("edit/{id}")
    public ResponseEntity<?> editItem(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam @Nullable String tags,
            @RequestParam @Nullable MultipartFile image,
            @RequestParam @Nullable String fields){
        try {
            itemService.editItem(id,name,tags,image,fields);
            return ResponseEntity.ok().body(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("like")
    public ResponseEntity<?> like(
            @RequestBody Long id){
        itemService.like(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("isLiked")
    public ResponseEntity<?> isLiked(
            @RequestBody Long id){
        boolean isLiked = itemService.checkLiked(id);
        return ResponseEntity.ok().body(isLiked);
    }


    @PostMapping("comment/{id}")
    public ResponseEntity<?> comment(
            @PathVariable Long id,
            @RequestBody String content){
        try {
            commentService.addComment(id, content);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("getComments/{id}")
    public ResponseEntity<?> comment(
            @PathVariable Long id){
        try {
            String data = commentService.getComments(id);
            return ResponseEntity.ok(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("delete")
    public ResponseEntity<?> delete(@RequestBody Long id){
        try {
            itemService.delete(id);
            return ResponseEntity.ok().body(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
