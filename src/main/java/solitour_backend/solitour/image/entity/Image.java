package solitour_backend.solitour.image.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.image.image_status.ImageStatusConverter;
import solitour_backend.solitour.information.entity.Information;

@Entity
@Getter
@Setter
@Table(name = "image")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @Column(name = "image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_status_id")
    @Convert(converter = ImageStatusConverter.class)
    private ImageStatus imageStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private Information information;

    @Column(name = "image_address")
    private String address;

    @CreatedDate
    @Column(name = "image_created_date")
    private LocalDate createdDate;

    public Image(ImageStatus imageStatus, Information information, String address) {
        this.imageStatus = imageStatus;
        this.information = information;
        this.address = address;
    }
}
