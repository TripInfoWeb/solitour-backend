package solitour_backend.solitour.information.service;

import static solitour_backend.solitour.information.repository.InformationRepositoryCustom.LIKE_COUNT_SORT;
import static solitour_backend.solitour.information.repository.InformationRepositoryCustom.VIEW_COUNT_SORT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformationRepository;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.category.repository.CategoryRepository;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.great_information.repository.GreatInformationRepository;
import solitour_backend.solitour.image.dto.mapper.ImageMapper;
import solitour_backend.solitour.image.dto.request.ImageDeleteRequest;
import solitour_backend.solitour.image.dto.request.ImageUseRequest;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.image.entity.Image;
import solitour_backend.solitour.image.exception.ImageNotExistsException;
import solitour_backend.solitour.image.exception.ImageRequestValidationFailedException;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.image.repository.ImageRepository;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.info_tag.entity.InfoTag;
import solitour_backend.solitour.info_tag.repository.InfoTagRepository;
import solitour_backend.solitour.information.dto.mapper.InformationMapper;
import solitour_backend.solitour.information.dto.request.InformationModifyRequest;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.request.InformationRegisterRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationDetailResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.dto.response.InformationResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.information.repository.InformationRepository;
import solitour_backend.solitour.place.dto.mapper.PlaceMapper;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.place.exception.PlaceNotExistsException;
import solitour_backend.solitour.place.repository.PlaceRepository;
import solitour_backend.solitour.tag.dto.mapper.TagMapper;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.tag.entity.Tag;
import solitour_backend.solitour.tag.repository.TagRepository;
import solitour_backend.solitour.user.dto.UserPostingResponse;
import solitour_backend.solitour.user.dto.mapper.UserMapper;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.entity.UserRepository;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.zone_category.dto.mapper.ZoneCategoryMapper;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryNotExistsException;
import solitour_backend.solitour.zone_category.repository.ZoneCategoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InformationService {

    private final InformationRepository informationRepository;
    private final CategoryRepository categoryRepository;
    private final ZoneCategoryRepository zoneCategoryRepository;
    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final InfoTagRepository infoTagRepository;
    private final InformationMapper informationMapper;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    private final PlaceMapper placeMapper;
    private final ZoneCategoryMapper zoneCategoryMapper;
    private final ImageMapper imageMapper;
    private final UserMapper userMapper;
    private final GreatInformationRepository greatInformationRepository;
    private final BookMarkInformationRepository bookMarkInformationRepository;

    public static final String IMAGE_PATH = "information";
    private final ImageRepository imageRepository;


    @Transactional
    public InformationResponse registerInformation(InformationRegisterRequest informationRegisterRequest,
                                                   MultipartFile thumbnail, List<MultipartFile> contentImages) {
        Category category = categoryRepository.findById(informationRegisterRequest.getCategoryId())
                .orElseThrow(
                        () -> new CategoryNotExistsException("해당하는 id의 category 가 없습니다"));
        ZoneCategory parentZoneCategory = zoneCategoryRepository.findByName(
                        informationRegisterRequest.getZoneCategoryNameParent())
                .orElseThrow(() -> new ZoneCategoryNotExistsException("해당하는 name의 ZoneCategory 없습니다"));

        ZoneCategory childZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                        parentZoneCategory.getId(), informationRegisterRequest.getZoneCategoryNameChild())
                .orElseThrow(() -> new ZoneCategoryNotExistsException(
                        "해당하는 ParentZoneCategoryId 와 name의 ZoneCategory 없습니다"));

        Place savePlace = placeRepository.save(
                new Place(
                        informationRegisterRequest.getPlaceRegisterRequest().getSearchId(),
                        informationRegisterRequest.getPlaceRegisterRequest().getName(),
                        informationRegisterRequest.getPlaceRegisterRequest().getXAxis(),
                        informationRegisterRequest.getPlaceRegisterRequest().getYAxis(),
                        informationRegisterRequest.getPlaceRegisterRequest().getAddress())
        );

        User user = userRepository.findById(informationRegisterRequest.getUserId())
                .orElseThrow(
                        () -> new UserNotExistsException("해당하는 id의 User 가 없습니다"));

        Information information =
                new Information(
                        category,
                        childZoneCategory,
                        user,
                        savePlace,
                        informationRegisterRequest.getInformationTitle(),
                        informationRegisterRequest.getInformationAddress(),
                        LocalDateTime.now(),
                        0,
                        informationRegisterRequest.getInformationContent(),
                        informationRegisterRequest.getInformationTips()
                );

        Information saveInformation = informationRepository.save(information);
        LocalDate localDate = LocalDate.now();

        String thumbNailImageUrl = s3Uploader.upload(thumbnail, IMAGE_PATH, saveInformation.getId());
        Image thumbImage = new Image(ImageStatus.THUMBNAIL, saveInformation, thumbNailImageUrl, localDate);
        imageRepository.save(thumbImage);

        for (MultipartFile multipartFile : contentImages) {
            String upload = s3Uploader.upload(multipartFile, IMAGE_PATH, saveInformation.getId());
            Image contentImage = new Image(ImageStatus.CONTENT, saveInformation, upload, localDate);
            imageRepository.save(contentImage);
        }

        List<Tag> tags = tagMapper.mapToTags(informationRegisterRequest.getTagRegisterRequests());
        List<Tag> saveTags = tagRepository.saveAll(tags);

        for (Tag tag : saveTags) {
            infoTagRepository.save(new InfoTag(tag, saveInformation));
        }

        return informationMapper.mapToInformationResponse(information);
    }


    public InformationDetailResponse getDetailInformation(Long userId, Long informationId) {
        Information information = informationRepository.findById(informationId).orElseThrow(
                () -> new InformationNotExistsException("해당하는 id의 information이 존재하지 않습니다."));
        List<InfoTag> infoTags = infoTagRepository.findAllByInformationId(information.getId());

        UserPostingResponse userPostingResponse = userMapper.mapToUserPostingResponse(
                information.getUser());

        List<TagResponse> tagResponses = new ArrayList<>();
        if (!infoTags.isEmpty()) {
            tagResponses = infoTags.stream()
                    .map(data ->
                            tagMapper.mapToTagResponse(data.getTag()))
                    .toList();
        }
        PlaceResponse placeResponse = placeMapper.mapToPlaceResponse(information.getPlace());
        ZoneCategoryResponse zoneCategoryResponse = zoneCategoryMapper.mapToZoneCategoryResponse(
                information.getZoneCategory());

        List<Image> images = imageRepository.findAllByInformationId(information.getId());

        List<ImageResponse> imageResponseList = imageMapper.toImageResponseList(images);

        int likeCount = greatInformationRepository.countByInformationId(information.getId());

        List<InformationBriefResponse> informationRecommend = informationRepository.getInformationRecommend(
                information.getId(), information.getCategory().getId(), userId);

        return new InformationDetailResponse(
                information.getTitle(),
                information.getAddress(),
                information.getCreatedDate(),
                information.getViewCount(),
                information.getContent(),
                information.getTip(),
                userPostingResponse,
                tagResponses,
                placeResponse,
                zoneCategoryResponse,
                imageResponseList,
                likeCount,
                informationRecommend);
    }

    @Transactional
    public InformationResponse modifyInformation(Long id, InformationModifyRequest informationModifyRequest,
                                                 MultipartFile thumbNail, List<MultipartFile> contentImages) {
        Information information = informationRepository.findById(id)
                .orElseThrow(
                        () -> new InformationNotExistsException("해당하는 id의 information이 존재하지 않습니다."));
        information.setTitle(informationModifyRequest.getTitle());
        information.setAddress(informationModifyRequest.getAddress());
        information.setContent(informationModifyRequest.getContent());
        information.setTip(informationModifyRequest.getTips());

        Place placeInformation = placeRepository.findById(information.getPlace().getId())
                .orElseThrow(
                        () -> new PlaceNotExistsException("해당하는 information의 place에서의 id가 존재하지 않습니다"));
        placeInformation.setName(informationModifyRequest.getPlaceModifyRequest().getName());
        placeInformation.setAddress(informationModifyRequest.getPlaceModifyRequest().getAddress());
        placeInformation.setXaxis(informationModifyRequest.getPlaceModifyRequest().getXAxis());
        placeInformation.setYaxis(informationModifyRequest.getPlaceModifyRequest().getYAxis());
        placeInformation.setSearchId(informationModifyRequest.getPlaceModifyRequest().getSearchId());

        Category categoryInformation = categoryRepository.findById(informationModifyRequest.getCategoryId())
                .orElseThrow(
                        () -> new CategoryNotExistsException("해당하는 cateogry Id 가 존재하지 않습니다."));
        information.setCategory(categoryInformation);

        ZoneCategory parentZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(null,
                        informationModifyRequest.getZoneCategoryNameParent())
                .orElseThrow(
                        () -> new ZoneCategoryNotExistsException("해당하는 name에 대한 zoneCategory가 존재하지 않습니다"));

        ZoneCategory childZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                        parentZoneCategory.getId(), informationModifyRequest.getZoneCategoryNameChild())
                .orElseThrow(
                        () -> new ZoneCategoryNotExistsException("해당하는 name에 대한 zoneCategory가 존재하지 않습니다"));

        information.setZoneCategory(childZoneCategory);

        List<ImageUseRequest> useImages = informationModifyRequest.getUseImages();

        for (ImageUseRequest imageUseRequest : useImages) {
            if (!imageRepository.existsImageByAddress(imageUseRequest.getAddress())) {
                throw new ImageNotExistsException("계속 사용하려는 주소의 이미지는 없습니다.");
            }
        }

        List<ImageDeleteRequest> deleteImages = informationModifyRequest.getDeleteImages();

        for (ImageDeleteRequest imageDeleteRequest : deleteImages) {
            if (!imageRepository.existsImageByAddress(imageDeleteRequest.getAddress())) {
                throw new ImageNotExistsException("삭제하려는 주소의 이미지가 없습니다.");
            }
            s3Uploader.deleteImage(imageDeleteRequest.getAddress());
            imageRepository.deleteByAddress(imageDeleteRequest.getAddress());
        }

        List<Image> allByInformationId = imageRepository.findAllByInformationId(
                information.getId());

        for (Image image : allByInformationId) {
            s3Uploader.deleteImage(image.getAddress());
        }

        if (!Objects.isNull(thumbNail)) {
            if (imageRepository.existsByInformationIdAndImageStatus(information.getId(),
                    ImageStatus.THUMBNAIL)) {
                throw new ImageRequestValidationFailedException("이미 썸네일 이미지가 있습니다");
            } else {
                String thumbNailImageUrl = s3Uploader.upload(thumbNail, IMAGE_PATH,
                        information.getId());
                Image thumbImage = new Image(ImageStatus.THUMBNAIL, information, thumbNailImageUrl,
                        LocalDate.now());
                imageRepository.save(thumbImage);
            }
        } else {
            if (!imageRepository.existsByInformationIdAndImageStatus(information.getId(),
                    ImageStatus.THUMBNAIL)) {
                throw new ImageRequestValidationFailedException("썸네일 이미지가 없습니다");
            }
        }
        if (Objects.nonNull(contentImages)) {
            for (MultipartFile multipartFile : contentImages) {
                String upload = s3Uploader.upload(multipartFile, IMAGE_PATH, information.getId());
                Image contentImage = new Image(ImageStatus.CONTENT, information, upload,
                        LocalDate.now());
                imageRepository.save(contentImage);
            }
        }

        List<InfoTag> infoTags = infoTagRepository.findAllByInformationId(information.getId());

        infoTagRepository.deleteAllByInformationId(information.getId());

        for (InfoTag infoTag : infoTags) {
            tagRepository.deleteById(infoTag.getTag().getTagId());
        }

        List<Tag> saveTags = tagRepository.saveAll(
                tagMapper.mapToTags(
                        informationModifyRequest.getTagRegisterRequests()));

        for (Tag tag : saveTags) {
            infoTagRepository.save(new InfoTag(tag, information));
        }

        return informationMapper.mapToInformationResponse(information);
    }

    @Transactional
    public void deleteInformation(Long id) {
        Information information = informationRepository.findById(id).orElseThrow(
                () -> new InformationNotExistsException("해당하는 id의 information이 존재하지 않습니다."));

        List<InfoTag> infoTags = infoTagRepository.findAllByInformationId(information.getId());
        infoTagRepository.deleteAllByInformationId(information.getId());

        for (InfoTag infoTag : infoTags) {
            tagRepository.deleteById(infoTag.getTag().getTagId());
        }

        greatInformationRepository.deleteAllByInformationId(information.getId());

        bookMarkInformationRepository.deleteAllByInformationId(information.getId());

        List<Image> allByInformationId = imageRepository.findAllByInformationId(
                information.getId());

        for (Image image : allByInformationId) {
            s3Uploader.deleteImage(image.getAddress());
        }

        imageRepository.deleteAllByInformationId(information.getId());
        informationRepository.deleteById(id);

    }


    public Page<InformationBriefResponse> getPageInformation(Pageable pageable, Long userId, Long parentCategoryId,
                                                             InformationPageRequest informationPageRequest) {
        if (!categoryRepository.existsByIdAndParentCategoryId(parentCategoryId, null)) {
            throw new CategoryNotExistsException("해당하는 id의 부모 category 는 없습니다");
        }

        if (Objects.nonNull(informationPageRequest.getChildCategoryId())) {
            Category category = categoryRepository.findById(informationPageRequest.getChildCategoryId())
                    .orElseThrow(
                            () -> new CategoryNotExistsException("해당하는 id의 category는 없습니다"));

            if (!Objects.equals(category.getParentCategory().getId(), parentCategoryId)) {
                throw new RequestValidationFailedException("자식 카테고리의 부모 카테고리와 요청한 부모 카테고리가 다릅니다");
            }
        }

        if (Objects.nonNull(informationPageRequest.getZoneCategoryId())) {
            if (!zoneCategoryRepository.existsById(informationPageRequest.getZoneCategoryId())) {
                throw new ZoneCategoryNotExistsException("해당하는 지역 카테고리가 없습니다");
            }
        }

        if (Objects.nonNull(informationPageRequest.getSort())) {
            if (!Objects.equals(LIKE_COUNT_SORT, informationPageRequest.getSort()) || !Objects.equals(VIEW_COUNT_SORT,
                    informationPageRequest.getSort())) {
                throw new RequestValidationFailedException("잘못된 정렬 코드입니다.");
            }
        }

        return informationRepository.getInformationPageFilterAndOrder(pageable, informationPageRequest, userId,
                parentCategoryId);
    }

    public List<InformationRankResponse> getRankInformation() {
        return informationRepository.getInformationRank();
    }

    public List<InformationMainResponse> getMainPageInformation(Long userId) {
        return informationRepository.getInformationLikeCountFromCreatedIn3(userId);
    }
}
