package solitour_backend.solitour.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.admin.dto.UserListWithPage;
import solitour_backend.solitour.admin.service.AdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/user/list")
    public ResponseEntity<UserListWithPage> getUserInfoList(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "") String nickname) {
        UserListWithPage response = adminService.getUserInfoList(nickname, page);
        return ResponseEntity.ok(response);
    }

}
