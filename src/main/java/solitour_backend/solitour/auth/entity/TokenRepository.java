package solitour_backend.solitour.auth.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends Repository<Token, Long> {

    Token save(Token token);

    Optional<Token> findByUserId(Long userId);

    @Modifying
    @Query("delete from Token t where t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
