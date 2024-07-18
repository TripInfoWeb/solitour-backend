package solitour_backend.solitour.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.image.image_status.ImageStatusConverter;
import solitour_backend.solitour.information.entity.Information;

@Entity
@Getter
@Table(name = "image")
@NoArgsConstructor
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

  @Column(name = "image_created_date")
  private LocalDate createdDate;

  public Image(ImageStatus imageStatus, Information information, String address,
      LocalDate createdDate) {
    this.imageStatus = imageStatus;
    this.information = information;
    this.address = address;
    this.createdDate = createdDate;
  }
}
