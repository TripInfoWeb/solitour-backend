package solitour_backend.solitour.tag.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.tag.dto.request.TagRegisterRequest;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.tag.entity.Tag;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TagMapper {

  @Mapping(target = "tagId", ignore = true)
  Tag mapToTag(TagRegisterRequest tagRegisterRequest);

  List<Tag> mapToTags(List<TagRegisterRequest> tagRegisterRequests);

  @Mapping(source = "tagId", target = "id")
  TagResponse mapToTagResponse(Tag tag);

  List<TagResponse> mapToTagResponses(List<Tag> tags);
}
