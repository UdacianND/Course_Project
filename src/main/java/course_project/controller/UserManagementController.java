package course_project.controller;

import course_project.base_service.UserBaseService;
import course_project.payload.request.UserRoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/management")
public class UserManagementController {

    private final UserBaseService userBaseService;

    @PostMapping("/delete")
    public void delete(
            @RequestBody Long[] userIds
    ){
        userBaseService.deleteAllById(userIds);
    }

    @PostMapping("/block")
    public void block(
            @RequestBody Long[] userIds
    )  {
        userBaseService.blockAllById(userIds);
    }

    @PostMapping("/unblock")
    public void unblock(
            @RequestBody Long[] userIds
    ){
        userBaseService.unblockAllById(userIds);
    }

    @PostMapping("/user-role")
    public void setUserRole(
            @RequestBody UserRoleDto userRoleDto
    ){
        userBaseService.setUserRole(userRoleDto);
    }
}
