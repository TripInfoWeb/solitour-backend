package solitour_backend.solitour.user.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.userImage WHERE u.nickname = :nickname AND u.userStatus = '활성화'")
    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u JOIN FETCH u.userImage WHERE u.email = :email AND u.userStatus = '활성화'")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.userImage WHERE u.id = :userId AND u.userStatus = '활성화'")
    User findByUserId(Long userId);

    boolean existsByNickname(String nickname);
}
