package solitour_backend.solitour.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    @Query("SELECT u FROM User u JOIN FETCH u.userImage WHERE u.id = :userId AND u.userStatus = '활성화' OR u.userStatus = '비활성화'")
    User findByUserId(Long userId);

    @Query("SELECT u FROM User u JOIN FETCH u.userImage WHERE u.oauthId = :oauthId AND u.userStatus = '활성화' OR u.userStatus = '비활성화'")
    Optional<User> findByOauthId(String oauthId);

    boolean existsByNickname(String nickname);
}
