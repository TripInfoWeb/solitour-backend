package solitour_backend.solitour.user_image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.user.entity.User;

@Entity
@Getter
@Table(name = "user_image")
@NoArgsConstructor
public class UserImage {
    @Id
    @Column(name = "user_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_image_address")
    private String address;

    @Column(name = "user_image_created_date")
    private LocalDate createdDate;

    public UserImage(String address, LocalDate createdDate) {
        this.address = address;
        this.createdDate = createdDate;
    }
}
