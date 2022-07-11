package course_project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import course_project.base_service.UserBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/management")
public class UserManagementController {

    private final UserBaseService userBaseService;


    @GetMapping("allUsers")
    public ResponseEntity<?> getAllUsers(){
        try {
            String userList = userBaseService.getUserList();
            return ResponseEntity.ok(userList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(
            @RequestBody Long[] userIds
    ){
        userBaseService.deleteAllById(userIds);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/block")
    public ResponseEntity<?> block(
            @RequestBody Long[] userIds
    )  {
        userBaseService.blockAllById(userIds);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/unblock")
    public ResponseEntity<?> unblock(
            @RequestBody Long[] userIds
    ){
        userBaseService.unblockAllById(userIds);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/userRole")
    public ResponseEntity<?> setUserRole(
            @RequestBody String userData
    ){
        try {
            userBaseService.setUserRole(userData);
            return ResponseEntity.ok().body(null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}
