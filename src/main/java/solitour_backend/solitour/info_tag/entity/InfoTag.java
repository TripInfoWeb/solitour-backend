package solitour_backend.solitour.info_tag.entity;

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
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.tag.entity.Tag;

@Entity
@Getter
@Table(name = "info_tag")
@NoArgsConstructor
public class InfoTag {

    @Id
    @Column(name = "info_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private Information information;

    public InfoTag(Tag tag, Information information) {
        this.tag = tag;
        this.information = information;
    }
}
