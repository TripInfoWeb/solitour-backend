package solitour_backend.solitour.admin.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import solitour_backend.solitour.admin.dto.UserListResponseDTO;
import solitour_backend.solitour.admin.dto.UserListWithPage;
import solitour_backend.solitour.admin.repository.AdminRepository;
import solitour_backend.solitour.user.entity.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public UserListWithPage getUserInfoList(String nickname, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        UserListWithPage reponseUserListWithPage = new UserListWithPage();
        Page<User> users;
        if (StringUtils.hasText(nickname)) {
            users = adminRepository.findAllByNicknameContainingIgnoreCase(nickname, pageable);
        } else {
            users = adminRepository.findAll(pageable);
        }
        List<UserListResponseDTO> userListResponse = users.map(user -> new UserListResponseDTO(
                user.getId(),
                user.getUserStatus(),
                user.getOauthId(),
                user.getProvider(),
                user.getNickname(),
                user.getName(),
                user.getAge(),
                user.getSex(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getIsAdmin(),
                user.getLatestLoginAt(),
                user.getCreatedAt(),
                user.getDeletedAt()
        )).toList();
        reponseUserListWithPage.setUsers(userListResponse);
        reponseUserListWithPage.setCount(adminRepository.count());
        return reponseUserListWithPage;
    }
}
