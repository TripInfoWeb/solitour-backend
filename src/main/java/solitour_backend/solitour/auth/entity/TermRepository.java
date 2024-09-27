package solitour_backend.solitour.auth.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import solitour_backend.solitour.user.entity.User;

public interface TermRepository extends Repository<Term, Long> {
    void save(Term term);

    @Query("SELECT t FROM Term t WHERE t.user = :user")
    <T> Optional<T> findByUser(User user);
}
