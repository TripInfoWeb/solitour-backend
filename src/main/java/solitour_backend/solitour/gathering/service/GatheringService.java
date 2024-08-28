package solitour_backend.solitour.gathering.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.gathering.dto.mapper.GatheringMapper;
import solitour_backend.solitour.gathering.dto.request.GatheringModifyRequest;
import solitour_backend.solitour.gathering.dto.request.GatheringRegisterRequest;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringDetailResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringResponse;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.exception.GatheringCategoryNotExistsException;
import solitour_backend.solitour.gathering.exception.GatheringNotExistsException;
import solitour_backend.solitour.gathering.repository.GatheringRepository;
import solitour_backend.solitour.gathering_applicants.dto.mapper.GatheringApplicantsMapper;
import solitour_backend.solitour.gathering_applicants.dto.response.GatheringApplicantsResponse;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;
import solitour_backend.solitour.gathering_applicants.exception.GatheringNotManagerException;
import solitour_backend.solitour.gathering_applicants.repository.GatheringApplicantsRepository;
import solitour_backend.solitour.gathering_category.entity.GatheringCategory;
import solitour_backend.solitour.gathering_category.repository.GatheringCategoryRepository;
import solitour_backend.solitour.gathering_tag.entity.GatheringTag;
import solitour_backend.solitour.gathering_tag.repository.GatheringTagRepository;
import solitour_backend.solitour.great_gathering.repository.GreatGatheringRepository;
import solitour_backend.solitour.place.dto.mapper.PlaceMapper;
import solitour_backend.solitour.place.dto.request.PlaceModifyRequest;
import solitour_backend.solitour.place.dto.request.PlaceRegisterRequest;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.place.repository.PlaceRepository;
import solitour_backend.solitour.tag.dto.mapper.TagMapper;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.tag.entity.Tag;
import solitour_backend.solitour.tag.repository.TagRepository;
import solitour_backend.solitour.user.dto.UserPostingResponse;
import solitour_backend.solitour.user.dto.mapper.UserMapper;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.zone_category.dto.mapper.ZoneCategoryMapper;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryNotExistsException;
import solitour_backend.solitour.zone_category.repository.ZoneCategoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringService {
    private final GatheringRepository gatheringRepository;
    private final UserRepository userRepository;
    private final ZoneCategoryRepository zoneCategoryRepository;
    private final PlaceRepository placeRepository;
    private final GatheringCategoryRepository gatheringCategoryRepository;
    private final TagMapper tagMapper;
    private final TagRepository tagRepository;
    private final GatheringTagRepository gatheringTagRepository;
    private final GatheringMapper gatheringMapper;
    private final UserMapper userMapper;
    private final PlaceMapper placeMapper;
    private final ZoneCategoryMapper zoneCategoryMapper;
    private final GreatGatheringRepository greatGatheringRepository;
    private final GatheringApplicantsRepository gatheringApplicantsRepository;
    private final GatheringApplicantsMapper gatheringApplicantsMapper;

    public GatheringDetailResponse getGatheringDetail(Long userId, Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(
                        () -> new GatheringNotExistsException("해당하는 id의 gathering 이 존재 하지 않습니다"));
        UserPostingResponse userPostingResponse = userMapper.mapToUserPostingResponse(gathering.getUser());

        List<GatheringTag> gatheringTags = gatheringTagRepository.findAllByGathering_Id(gathering.getId());

        List<TagResponse> tagResponses = new ArrayList<>();

        if (!gatheringTags.isEmpty()) {
            tagResponses = gatheringTags.stream()
                    .map(data ->
                            tagMapper.mapToTagResponse(data.getTag()))
                    .toList();
        }

        PlaceResponse placeResponse = placeMapper.mapToPlaceResponse(gathering.getPlace());

        ZoneCategoryResponse zoneCategoryResponse = zoneCategoryMapper.mapToZoneCategoryResponse(
                gathering.getZoneCategory());

        int likeCount = greatGatheringRepository.countByGatheringId(gathering.getId());

        List<GatheringApplicantsResponse> gatheringApplicantsResponses = gatheringApplicantsMapper.mapToGatheringApplicantsResponses(
                gatheringApplicantsRepository.findAllByGathering_Id(gathering.getId()));

        int nowPersonCount = gatheringApplicantsRepository.countAllByGathering_IdAndGatheringStatus(gathering.getId(),
                GatheringStatus.CONSENT);

        List<GatheringBriefResponse> gatheringRecommend = gatheringRepository.getGatheringRecommend(gathering.getId(),
                gathering.getGatheringCategory().getId(), userId);

        return new GatheringDetailResponse(
                gathering.getTitle(),
                gathering.getContent(),
                gathering.getPersonCount(),
                gathering.getViewCount(),
                gathering.getCreatedAt(),
                gathering.getScheduleStartDate(),
                gathering.getScheduleEndDate(),
                gathering.getDeadline(),
                gathering.getIsFinish(),
                gathering.getAllowedSex(),
                gathering.getStartAge(),
                gathering.getEndAge(),
                tagResponses,
                userPostingResponse,
                placeResponse,
                zoneCategoryResponse,
                likeCount,
                nowPersonCount,
                gatheringApplicantsResponses,
                gatheringRecommend
        );
    }


    @Transactional
    public GatheringResponse registerGathering(Long userId, GatheringRegisterRequest gatheringRegisterRequest) {
        PlaceRegisterRequest placeRegisterRequest = gatheringRegisterRequest.getPlaceRegisterRequest();
        Place place = new Place(
                placeRegisterRequest.getSearchId(),
                placeRegisterRequest.getName(),
                placeRegisterRequest.getXAxis(),
                placeRegisterRequest.getYAxis(),
                placeRegisterRequest.getAddress());

        Place savePlace = placeRepository.save(place);
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotExistsException("해당하는 id의 User 가 없습니다"));
        GatheringCategory gatheringCategory = gatheringCategoryRepository.findById(
                        gatheringRegisterRequest.getGatheringCategoryId())
                .orElseThrow(
                        () -> new GatheringCategoryNotExistsException("해당하는 id의 category 가 없습니다"));

        ZoneCategory parentZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                        null, gatheringRegisterRequest.getZoneCategoryNameParent())
                .orElseThrow(
                        () ->
                                new ZoneCategoryNotExistsException("해당하는 name의 ZoneCategory 없습니다"));

        ZoneCategory childZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                        parentZoneCategory.getId(), gatheringRegisterRequest.getZoneCategoryNameChild())
                .orElseThrow(
                        () -> new ZoneCategoryNotExistsException("해당하는 name의 ZoneCategory 없습니다"));

        Gathering gathering =
                new Gathering(
                        user,
                        childZoneCategory,
                        gatheringCategory,
                        savePlace,
                        gatheringRegisterRequest.getTitle(),
                        gatheringRegisterRequest.getContent(),
                        gatheringRegisterRequest.getPersonCount(),
                        0,
                        gatheringRegisterRequest.getScheduleStartDate(),
                        gatheringRegisterRequest.getScheduleEndDate(),
                        false,
                        gatheringRegisterRequest.getDeadline(),
                        gatheringRegisterRequest.getAllowedSex(),
                        gatheringRegisterRequest.getStartAge(),
                        gatheringRegisterRequest.getEndAge()
                );
        Gathering saveGathering = gatheringRepository.save(gathering);

        List<Tag> tags = tagMapper.mapToTags(gatheringRegisterRequest.getTagRegisterRequests());
        List<Tag> saveTags = tagRepository.saveAll(tags);

        for (Tag tag : saveTags) {
            gatheringTagRepository.save(new GatheringTag(tag, saveGathering));
        }
        return gatheringMapper.mapToGatheringResponse(saveGathering);
    }

    @Transactional
    public GatheringResponse modifyGathering(Long userId, Long gatheringId,
                                             GatheringModifyRequest gatheringModifyRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotExistsException("해당하는 id의 User 가 없습니다"));

        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(
                        () -> new GatheringNotExistsException("해당하는 id의 gathering 이 존재 하지 않습니다"));

        if (!Objects.equals(user, gathering.getUser())) {
            throw new GatheringNotManagerException("해당 유저는 권한이 없습니다");
        }

        GatheringCategory gatheringCategory = gatheringCategoryRepository.findById(
                gatheringModifyRequest.getGatheringCategoryId()).orElseThrow();
        ZoneCategory parentZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(null,
                gatheringModifyRequest.getZoneCategoryNameParent()).orElseThrow();
        ZoneCategory childZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                parentZoneCategory.getId(), gatheringModifyRequest.getZoneCategoryNameChild()).orElseThrow();

        Place place = gathering.getPlace();
        PlaceModifyRequest placeModifyRequest = gatheringModifyRequest.getPlaceModifyRequest();
        place.setSearchId(placeModifyRequest.getSearchId());
        place.setName(placeModifyRequest.getName());
        place.setXaxis(placeModifyRequest.getXAxis());
        place.setYaxis(placeModifyRequest.getYAxis());
        place.setAddress(placeModifyRequest.getAddress());

        gathering.setTitle(gatheringModifyRequest.getTitle());
        gathering.setContent(gatheringModifyRequest.getContent());

        gathering.setPersonCount(gatheringModifyRequest.getPersonCount());
        gathering.setScheduleStartDate(gatheringModifyRequest.getScheduleStartDate());
        gathering.setScheduleEndDate(gatheringModifyRequest.getScheduleEndDate());
        gathering.setDeadline(gatheringModifyRequest.getDeadline());
        gathering.setAllowedSex(gatheringModifyRequest.getAllowedSex());
        gathering.setStartAge(gatheringModifyRequest.getStartAge());
        gathering.setEndAge(gatheringModifyRequest.getEndAge());
        gathering.setGatheringCategory(gatheringCategory);
        gathering.setZoneCategory(childZoneCategory);

        List<GatheringTag> gatheringTags = gatheringTagRepository.findAllByGathering_Id(gathering.getId());

        gatheringTagRepository.deleteAllByGathering_Id(gathering.getId());

        for (GatheringTag gatheringTag : gatheringTags) {
            tagRepository.deleteById(gatheringTag.getTag().getTagId());
        }

        List<Tag> saveTags = tagRepository.saveAll(
                tagMapper.mapToTags(gatheringModifyRequest.getTagRegisterRequests()));

        for (Tag tag : saveTags) {
            gatheringTagRepository.save(new GatheringTag(tag, gathering));
        }

        return gatheringMapper.mapToGatheringResponse(gathering);
    }


}
















