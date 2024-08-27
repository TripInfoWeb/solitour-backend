package solitour_backend.solitour.book_mark_gathering.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.user.entity.User;

@Entity
@Getter
@Table(name = "book_mark_gathering")
@NoArgsConstructor
public class BookMarkGathering {

    @Id
    @Column(name = "book_mark_gathering_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public BookMarkGathering(User user, Gathering gathering) {
        this.user = user;
        this.gathering = gathering;
    }
}
