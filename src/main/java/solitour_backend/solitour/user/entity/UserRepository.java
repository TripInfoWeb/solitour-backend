package solitour_backend.solitour.user.entity;

import io.jsonwebtoken.security.Password;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByNickname(String nickname);

  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u JOIN FETCH u.userImage WHERE u.id = :userId")
  User findByUserId(Long userId);
}
