package solitour_backend.solitour.information.service;

import static solitour_backend.solitour.information.repository.InformationRepositoryCustom.LIKE_COUNT_SORT;
import static solitour_backend.solitour.information.repository.InformationRepositoryCustom.VIEW_COUNT_SORT;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformationRepository;
import solitour_backend.solitour.category.dto.mapper.CategoryMapper;
import solitour_backend.solitour.category.dto.response.CategoryResponse;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.category.repository.CategoryRepository;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.great_information.repository.GreatInformationRepository;
import solitour_backend.solitour.image.dto.mapper.ImageMapper;
import solitour_backend.solitour.image.dto.request.ImageRequest;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.image.entity.Image;
import solitour_backend.solitour.image.exception.ImageAlreadyExistsException;
import solitour_backend.solitour.image.exception.ImageNotExistsException;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.image.repository.ImageRepository;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.info_tag.entity.InfoTag;
import solitour_backend.solitour.info_tag.repository.InfoTagRepository;
import solitour_backend.solitour.information.dto.mapper.InformationMapper;
import solitour_backend.solitour.information.dto.request.InformationCreateRequest;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.request.InformationUpdateRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationDetailResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.dto.response.InformationResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.information.exception.InformationNotManageException;
import solitour_backend.solitour.information.repository.InformationRepository;
import solitour_backend.solitour.place.dto.mapper.PlaceMapper;
import solitour_backend.solitour.place.dto.request.PlaceModifyRequest;
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
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.user.repository.UserRepository;
import solitour_backend.solitour.user_image.entity.UserImage;
import solitour_backend.solitour.user_image.entity.UserImageRepository;
import solitour_backend.solitour.util.HmacUtils;
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
    private final UserImageRepository userImageRepository;
    private final ImageRepository imageRepository;
    private final CategoryMapper categoryMapper;


    @Transactional
    public InformationResponse registerInformation(Long userId, InformationCreateRequest informationCreateRequest) {
        Category category = categoryRepository.findById(informationCreateRequest.getCategoryId())
                .orElseThrow(
                        () -> new CategoryNotExistsException("해당하는 id의 category 가 없습니다"));
        ZoneCategory parentZoneCategory = zoneCategoryRepository.findByName(
                        informationCreateRequest.getZoneCategoryNameParent())
                .orElseThrow(() -> new ZoneCategoryNotExistsException("해당하는 name의 ZoneCategory 없습니다"));

        ZoneCategory childZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                        parentZoneCategory.getId(), informationCreateRequest.getZoneCategoryNameChild())
                .orElseThrow(() -> new ZoneCategoryNotExistsException(
                        "해당하는 ParentZoneCategoryId 와 name의 ZoneCategory 없습니다"));

        Place savePlace = placeRepository.save(
                new Place(
                        informationCreateRequest.getPlaceRegisterRequest().getSearchId(),
                        informationCreateRequest.getPlaceRegisterRequest().getName(),
                        informationCreateRequest.getPlaceRegisterRequest().getXAxis(),
                        informationCreateRequest.getPlaceRegisterRequest().getYAxis(),
                        informationCreateRequest.getPlaceRegisterRequest().getAddress())
        );

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotExistsException("해당하는 id의 User 가 없습니다"));

        Information information =
                new Information(
                        category,
                        childZoneCategory,
                        user,
                        savePlace,
                        informationCreateRequest.getInformationTitle(),
                        informationCreateRequest.getInformationAddress(),
                        0,
                        informationCreateRequest.getInformationContent(),
                        informationCreateRequest.getInformationTips()
                );

        Information saveInformation = informationRepository.save(information);

        List<Tag> tags = tagMapper.mapToTags(informationCreateRequest.getTagRegisterRequests());
        List<Tag> saveTags = tagRepository.saveAll(tags);

        for (Tag tag : saveTags) {
            infoTagRepository.save(new InfoTag(tag, saveInformation));
        }

        Image thumbImage = new Image(ImageStatus.THUMBNAIL, saveInformation, informationCreateRequest.getThumbNailImageUrl());
        imageRepository.save(thumbImage);

        s3Uploader.markImagePermanent(thumbImage.getAddress());
        List<String> contentImagesUrl = informationCreateRequest.getContentImagesUrl();

        if (Objects.nonNull(contentImagesUrl) && !contentImagesUrl.isEmpty()) {
            List<Image> contentImageList = new ArrayList<>();
            for (String contentImage : contentImagesUrl) {
                contentImageList.add(new Image(ImageStatus.CONTENT, saveInformation, contentImage));
                s3Uploader.markImagePermanent(contentImage);
            }
            imageRepository.saveAll(contentImageList);
        }


        return informationMapper.mapToInformationResponse(saveInformation);
    }

    @Transactional
    public InformationDetailResponse getDetailInformation(Long userId, Long informationId, HttpServletRequest request, HttpServletResponse response) {
        Information information = informationRepository.findById(informationId)
                .orElseThrow(
                        () ->
                                new InformationNotExistsException("해당하는 id 의 information 이 존재하지 않습니다."));

        List<InfoTag> infoTags = infoTagRepository.findAllByInformationId(information.getId());

        UserPostingResponse userPostingResponse = userMapper.mapToUserPostingResponse(information.getUser());

        List<TagResponse> tagResponses = new ArrayList<>();
        if (!infoTags.isEmpty()) {
            tagResponses = infoTags.stream()
                    .map(data ->
                            tagMapper.mapToTagResponse(data.getTag()))
                    .toList();
        }
        PlaceResponse placeResponse = placeMapper.mapToPlaceResponse(information.getPlace());
        ZoneCategoryResponse zoneCategoryResponse = zoneCategoryMapper.mapToZoneCategoryResponse(information.getZoneCategory());
        CategoryResponse categoryResponse = categoryMapper.mapToCategoryResponse(information.getCategory());

        List<Image> images = imageRepository.findAllByInformationId(information.getId());

        List<ImageResponse> imageResponseList = imageMapper.toImageResponseList(images);

        int likeCount = greatInformationRepository.countByInformationId(information.getId());

        List<InformationBriefResponse> informationRecommend = informationRepository.getInformationRecommend(information.getId(), information.getCategory().getId(), userId);

        boolean isLike = greatInformationRepository.existsByInformationIdAndUserId(information.getId(), userId);

        User user = information.getUser();

        String userImageUrl = userImageRepository.findById(user.getId())
                .map(UserImage::getAddress)
                .orElseGet(
                        () -> userRepository.getProfileUrl(user.getSex()));

        try {
            updateViewCount(information, request, response, userId);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

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
                categoryResponse,
                imageResponseList,
                likeCount,
                userImageUrl,
                isLike,
                informationRecommend);
    }

    @Transactional
    public InformationResponse updateInformation(Long userId, Long informationId, InformationUpdateRequest informationUpdateRequest) {
        Information information = getInformation(informationId, userId);

        updateInformationDetails(information, informationUpdateRequest);
        updatePlaceDetails(information, informationUpdateRequest);
        updateCategory(information, informationUpdateRequest);
        updateZoneCategory(information, informationUpdateRequest);
        updateTags(information, informationUpdateRequest);
        handleImageDeletions(informationUpdateRequest);
        handleImageAdditions(information, informationUpdateRequest);
        updateThumbnail(information, informationUpdateRequest);

        return informationMapper.mapToInformationResponse(information);
    }

    private Information getInformation(Long informationId, Long userId) {
        Information information = informationRepository.findById(informationId)
                .orElseThrow(() -> new InformationNotExistsException("해당하는 id의 information 이 존재하지 않습니다."));
        if (!Objects.equals(information.getUser().getId(), userId)) {
            throw new InformationNotManageException("권한이 없습니다");
        }
        return information;
    }

    private void updateInformationDetails(Information information, InformationUpdateRequest request) {
        information.setTitle(request.getTitle());
        information.setAddress(request.getAddress());
        information.setContent(request.getContent());
        information.setTip(request.getTips());
    }

    private void updatePlaceDetails(Information information, InformationUpdateRequest request) {
        Place place = placeRepository.findById(information.getPlace().getId())
                .orElseThrow(() -> new PlaceNotExistsException("해당하는 information 의 place 에서의 id가 존재하지 않습니다"));
        PlaceModifyRequest placeRequest = request.getPlaceModifyRequest();
        place.setName(placeRequest.getName());
        place.setAddress(placeRequest.getAddress());
        place.setXaxis(placeRequest.getXAxis());
        place.setYaxis(placeRequest.getYAxis());
        place.setSearchId(placeRequest.getSearchId());
    }

    private void updateCategory(Information information, InformationUpdateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotExistsException("해당하는 category Id 가 존재하지 않습니다."));

        if (Objects.isNull(category.getParentCategory())) {
            throw new RequestValidationFailedException("부모 카테고리는 등록이 안됩니다");
        }
        information.setCategory(category);
    }

    private void updateZoneCategory(Information information, InformationUpdateRequest request) {
        ZoneCategory parentZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(null, request.getZoneCategoryNameParent())
                .orElseThrow(() -> new ZoneCategoryNotExistsException("해당하는 name 에 대한 zoneCategory 가 존재하지 않습니다"));

        ZoneCategory childZoneCategory = zoneCategoryRepository.findByParentZoneCategoryIdAndName(
                        parentZoneCategory.getId(), request.getZoneCategoryNameChild())
                .orElseThrow(() -> new ZoneCategoryNotExistsException("해당하는 name에 대한 zoneCategory가 존재하지 않습니다"));

        information.setZoneCategory(childZoneCategory);
    }

    private void updateTags(Information information, InformationUpdateRequest request) {
        List<InfoTag> infoTags = infoTagRepository.findAllByInformationId(information.getId());

        infoTagRepository.deleteAllByInformationId(information.getId());
        for (InfoTag infoTag : infoTags) {
            tagRepository.deleteById(infoTag.getTag().getTagId());
        }

        List<Tag> saveTags = tagRepository.saveAll(tagMapper.mapToTags(request.getTagRegisterRequests()));
        for (Tag tag : saveTags) {
            infoTagRepository.save(new InfoTag(tag, information));
        }
    }

    private void handleImageDeletions(InformationUpdateRequest request) {
        if (Objects.nonNull(request.getDeleteImagesUrl())) {
            for (ImageRequest deleteImageUrl : request.getDeleteImagesUrl()) {
                if (!imageRepository.existsImageByAddress(deleteImageUrl.getAddress())) {
                    throw new ImageNotExistsException("해당하는 이미지는 없습니다");
                }
                imageRepository.deleteByAddress(deleteImageUrl.getAddress());
            }
        }
    }

    private void handleImageAdditions(Information information, InformationUpdateRequest request) {
        if (Objects.nonNull(request.getNewContentImagesUrl())) {
            List<Image> contentImageList = new ArrayList<>();
            for (ImageRequest newContentImageUrl : request.getNewContentImagesUrl()) {
                contentImageList.add(new Image(ImageStatus.CONTENT, information, newContentImageUrl.getAddress()));
                s3Uploader.markImagePermanent(newContentImageUrl.getAddress());
            }
            imageRepository.saveAll(contentImageList);
        }
    }

    private void updateThumbnail(Information information, InformationUpdateRequest request) {
        if (isInvalidThumbnailUpdate(request)) {
            validateExistingThumbNailImage(information);
        } else if (Objects.isNull(request.getNewThumbNailUrl())) {
            handleThumbnailUpdateWithoutNewUrl(information, request);
        } else {
            handleThumbnailUpdateWithNewUrl(information, request);
        }
    }

    private boolean isInvalidThumbnailUpdate(InformationUpdateRequest request) {
        return Objects.isNull(request.getNewThumbNailUrl())
                && Objects.isNull(request.getNewThumbNailFromContent())
                && Objects.isNull(request.getMoveThumbNailToContent());
    }

    private void validateExistingThumbNailImage(Information information) {
        if (!imageRepository.existsImageByImageStatusAndInformationId(ImageStatus.THUMBNAIL, information.getId())) {
            throw new ImageNotExistsException("썸네일 이미지가 없습니다");
        }
    }

    private void handleThumbnailUpdateWithoutNewUrl(Information information, InformationUpdateRequest request) {
        if (Objects.nonNull(request.getNewThumbNailFromContent()) && Objects.nonNull(request.getMoveThumbNailToContent())) {
            swapThumbnailAndContent(information, request);
        } else if (Objects.nonNull(request.getNewThumbNailFromContent())) {
            setNewThumbnailFromContent(information, request);
        }
    }

    private void handleThumbnailUpdateWithNewUrl(Information information, InformationUpdateRequest request) {
        if (Objects.nonNull(request.getMoveThumbNailToContent())) {
            Image thumbNailImage = imageRepository.findImageByAddress(request.getMoveThumbNailToContent().getAddress()).orElseThrow();
            thumbNailImage.setImageStatus(ImageStatus.CONTENT);
        } else {
            if (imageRepository.existsImageByImageStatusAndInformationId(ImageStatus.THUMBNAIL, information.getId())) {
                throw new ImageAlreadyExistsException("해당 정보에 대한 썸네일 이미지가 존재합니다");
            }
        }
        Image newImage = new Image(ImageStatus.THUMBNAIL, information, request.getNewThumbNailUrl().getAddress());
        imageRepository.save(newImage);
        s3Uploader.markImagePermanent(newImage.getAddress());
    }

    private void swapThumbnailAndContent(Information information, InformationUpdateRequest request) {
        validateExistingThumbNailImage(information);
        Image content = imageRepository.findImageByAddress(request.getNewThumbNailFromContent().getAddress()).orElseThrow();
        content.setImageStatus(ImageStatus.THUMBNAIL);
        Image thumbNail = imageRepository.findImageByAddress(request.getMoveThumbNailToContent().getAddress()).orElseThrow();
        thumbNail.setImageStatus(ImageStatus.CONTENT);
    }

    private void setNewThumbnailFromContent(Information information, InformationUpdateRequest request) {
        if (imageRepository.existsImageByImageStatusAndInformationId(ImageStatus.THUMBNAIL, information.getId())) {
            throw new IllegalStateException("THUMBNAIL image already exists.");
        }
        Image content = imageRepository.findImageByAddress(request.getNewThumbNailFromContent().getAddress()).orElseThrow();
        content.setImageStatus(ImageStatus.THUMBNAIL);
    }


    @Transactional
    public void deleteInformation(Long userId, Long id) {
        Information information = informationRepository.findById(id)
                .orElseThrow(
                        () ->
                                new InformationNotExistsException("해당하는 id의 information 이 존재하지 않습니다."));

        if (!Objects.equals(information.getUser().getId(), userId)) {
            throw new InformationNotManageException("권한이 없습니다");
        }
        List<InfoTag> infoTags = infoTagRepository.findAllByInformationId(information.getId());
        infoTagRepository.deleteAllByInformationId(information.getId());

        for (InfoTag infoTag : infoTags) {
            tagRepository.deleteById(infoTag.getTag().getTagId());
        }

        greatInformationRepository.deleteAllByInformationId(information.getId());

        bookMarkInformationRepository.deleteAllByInformationId(information.getId());

        List<Image> allByInformationId = imageRepository.findAllByInformationId(information.getId());

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
                            () -> new CategoryNotExistsException("해당하는 id의 category 는 없습니다"));

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
            if (!Objects.equals(LIKE_COUNT_SORT, informationPageRequest.getSort()) && !Objects.equals(VIEW_COUNT_SORT,
                    informationPageRequest.getSort())) {
                throw new RequestValidationFailedException("잘못된 정렬 코드입니다.");
            }
        }

        return informationRepository.getPageInformationFilterAndOrder(pageable, informationPageRequest, userId, parentCategoryId);
    }

    public List<InformationRankResponse> getRankInformation() {
        return informationRepository.getInformationRank();
    }

    public List<InformationMainResponse> getMainPageInformation(Long userId) {
        return informationRepository.getInformationLikeCountFromCreatedIn3(userId);
    }

    public Page<InformationBriefResponse> getPageInformationByTag(Pageable pageable, Long userId, Long parentCategoryId,
                                                                  InformationPageRequest informationPageRequest,
                                                                  String decodedTag) {
        return informationRepository.getInformationPageByTag(pageable, userId, parentCategoryId, informationPageRequest,
                decodedTag);
    }

    public void updateViewCount(Information information, HttpServletRequest request, HttpServletResponse response, Long userId) throws Exception {
        String cookieName = "viewed_informations";
        Cookie[] cookies = request.getCookies();
        Cookie postCookie = null;

        if (Objects.nonNull(cookies)) {
            postCookie = Arrays.stream(cookies)
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);
        }

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String cookieData = userId + "_" + information.getId() + "_" + now.format(formatter);

        if (Objects.nonNull(postCookie) && postCookie.getValue() != null) {
            String[] informationDataArray = URLDecoder.decode(postCookie.getValue(), StandardCharsets.UTF_8).split(",");
            boolean isUpdated = false;
            boolean hasExistingData = false;

            for (int i = 0; i < informationDataArray.length; i++) {
                String[] parts = informationDataArray[i].split("\\|");
                if (parts.length == 2) {
                    if (HmacUtils.verifyHmac(parts[0], parts[1])) {
                        String[] cookieInfo = parts[0].split("_");
                        Long cookieUserId = Long.parseLong(cookieInfo[0]);
                        Long cookieGatheringId = Long.parseLong(cookieInfo[1]);
                        LocalDate lastViewedAt = LocalDate.parse(cookieInfo[2], formatter);

                        if (cookieUserId.equals(userId) && cookieGatheringId.equals(information.getId())) {
                            hasExistingData = true;
                            if (lastViewedAt.isBefore(now.minusDays(1))) {
                                incrementInformationViewCount(information);
                                String newHmac = HmacUtils.generateHmac(cookieData);
                                informationDataArray[i] = cookieData + "|" + newHmac;
                                isUpdated = true;
                            }
                            break;
                        }
                    }

                }
            }

            if (isUpdated || !hasExistingData) {
                if (!hasExistingData) {
                    incrementInformationViewCount(information);
                }
                String hmac = HmacUtils.generateHmac(cookieData);
                String updatedValue = String.join(",", informationDataArray) + "," + cookieData + "|" + hmac;
                postCookie.setValue(URLEncoder.encode(updatedValue, StandardCharsets.UTF_8));
                postCookie.setPath("/");
                response.addCookie(postCookie);
            }
        } else {
            incrementInformationViewCount(information);
            String hmac = HmacUtils.generateHmac(cookieData);
            Cookie newCookie = new Cookie(cookieName, URLEncoder.encode(cookieData + "|" + hmac, StandardCharsets.UTF_8));
            newCookie.setMaxAge(60 * 60 * 24);
            newCookie.setPath("/");
            response.addCookie(newCookie);
        }
    }

    private void incrementInformationViewCount(Information information) {
        information.upViewCount();
    }

}
