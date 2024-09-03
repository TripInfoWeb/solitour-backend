package solitour_backend.solitour.image.dto.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.image.entity.Image;
import solitour_backend.solitour.image.image_status.ImageStatus;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

    @Mapping(source = "imageStatus", target = "imageStatus", qualifiedByName = "mapImageStatus")
    ImageResponse toImageResponse(Image image);

    @Named("mapImageStatus")
    default String mapImageStatus(ImageStatus imageStatus) {
        return imageStatus.getName();
    }

    List<ImageResponse> toImageResponseList(List<Image> images);
}
