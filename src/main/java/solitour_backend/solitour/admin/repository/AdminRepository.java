package solitour_backend.solitour.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.user.entity.User;

public interface AdminRepository extends JpaRepository<User, Long> {

    Page<User> findAllByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

}
