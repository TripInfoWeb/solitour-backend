package solitour_backend.solitour.tag.dto.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.tag.dto.request.TagRegisterRequest;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.tag.entity.Tag;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TagMapper {

    @Mapping(target = "tagId", ignore = true)
    Tag mapToTag(TagRegisterRequest tagRegisterRequest);

    List<Tag> mapToTags(List<TagRegisterRequest> tagRegisterRequests);

    TagResponse mapToTagResponse(Tag tag);

    List<TagResponse> mapToTagResponses(List<Tag> tags);
}
