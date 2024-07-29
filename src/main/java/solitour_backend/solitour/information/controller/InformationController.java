package solitour_backend.solitour.information.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.information.dto.request.InformationModifyRequest;
import solitour_backend.solitour.information.dto.request.InformationRegisterRequest;
import solitour_backend.solitour.information.dto.response.*;
import solitour_backend.solitour.information.service.InformationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/informations")
public class InformationController {

    private final InformationService informationService;
    public static final int PAGE_SIZE = 12;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping
    @Authenticated
    public ResponseEntity<InformationResponse> createInformation(@Valid @RequestPart("request") InformationRegisterRequest informationRegisterRequest,
                                                                 @RequestPart("thumbNailImage") MultipartFile thumbnail,
                                                                 @RequestPart("contentImages") List<MultipartFile> contentImages,
                                                                 BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);
        InformationResponse informationResponse = informationService.registerInformation(
                informationRegisterRequest, thumbnail, contentImages);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationResponse);
    }

    @GetMapping("/{informationId}")
    public ResponseEntity<InformationDetailResponse> getDetailInformation(@PathVariable Long informationId,
                                                                          HttpServletRequest request) {
        Long userId = findUser(request);
        InformationDetailResponse informationDetailResponse = informationService.getDetailInformation(userId, informationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(informationDetailResponse);
    }

    @Authenticated
    @PutMapping("/{informationId}")
    public ResponseEntity<InformationResponse> modifyInformation(@PathVariable Long informationId,
                                                                 @RequestPart(value = "thumbNailImage", required = false) MultipartFile thumbnail,
                                                                 @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages,
                                                                 @Valid @RequestPart("request") InformationModifyRequest informationModifyRequest,
                                                                 BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);

        InformationResponse informationResponse = informationService.modifyInformation(
                informationId, informationModifyRequest, thumbnail, contentImages);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationResponse);
    }

    @Authenticated
    @DeleteMapping("/{informationId}")
    public ResponseEntity<Void> deleteInformation(@PathVariable Long informationId) {
        informationService.deleteInformation(informationId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    //default
    @GetMapping("/parent-category/{parentCategoryId}")
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationByParentCategoryFilterZoneCategory(@RequestParam(defaultValue = "0") int page,
                                                                                                            @RequestParam(required = false, name = "zoneCategory") Long zoneCategoryId,
                                                                                                            @PathVariable("parentCategoryId") Long categoryId,
                                                                                                            HttpServletRequest request) {

        Long userId = findUser(request);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByParentCategoryFilterZoneCategory(pageable, categoryId, userId, zoneCategoryId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }

    @GetMapping("/child-category/{childCategoryId}")
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationByChildCategoryFilterZoneCategory(@RequestParam(defaultValue = "0") int page,
                                                                                                           @PathVariable("childCategoryId") Long categoryId,
                                                                                                           @RequestParam(required = false, name = "zoneCategory") Long zoneCategoryId,
                                                                                                           HttpServletRequest request) {
        Long userId = findUser(request);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByChildCategoryFilterZoneCategory(pageable, categoryId, userId, zoneCategoryId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }

    //지역 카테고리 별 좋아요순
    @GetMapping("/parent-category/{parentCategory}/like-count")
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationByParentCategoryFilterZoneCategoryLikeCount(@RequestParam(defaultValue = "0") int page,
                                                                                                                     @RequestParam(required = false, name = "zoneCategory") Long zoneCategoryId,
                                                                                                                     @PathVariable("parentCategory") Long categoryId,
                                                                                                                     HttpServletRequest request) {
        Long userId = findUser(request);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByParentCategoryFilterZoneCategoryLikeCount(pageable, categoryId, userId, zoneCategoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }

    @GetMapping("/child-category/{childCategory}/like-count")
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationChildCategoryFilterZoneCategoryLikeCount(@RequestParam(defaultValue = "0") int page,
                                                                                                                  @RequestParam(required = false, name = "zoneCategory") Long zoneCategoryId,
                                                                                                                  @PathVariable("childCategory") Long categoryId,
                                                                                                                  HttpServletRequest request) {
        Long userId = findUser(request);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByChildCategoryFilterZoneCategoryLikeCount(pageable, categoryId, userId, zoneCategoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }


    //지역 카테고리 별 조회순
    @GetMapping("/parent-category/{parentCategoryId}/view-count")
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationByParentCategoryFilterZoneCategoryViewCount(@RequestParam(defaultValue = "0") int page,
                                                                                                                     @RequestParam(required = false, name = "zoneCategory") Long zoneCategoryId,
                                                                                                                     @PathVariable("parentCategoryId") Long categoryId,
                                                                                                                     HttpServletRequest request) {

        Long userId = findUser(request);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByParentCategoryFilterZoneCategoryViewCount(pageable, categoryId, userId, zoneCategoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }

    @GetMapping("/child-category/{childCategoryId}/view-count")
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationByChildCategoryViewCount(@RequestParam(defaultValue = "0") int page,
                                                                                                  @PathVariable("childCategoryId") Long categoryId,
                                                                                                  @RequestParam(required = false, name = "zoneCategory") Long zoneCategoryId,
                                                                                                  HttpServletRequest request) {
        Long userId = findUser(request);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByChildCategoryFilterZoneCategoryViewCount(pageable, categoryId, userId, zoneCategoryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }

    //지역카테고리별
//    @GetMapping("/parent-category/{parentCategoryId}/zone-category/{zoneCategory}")
//    public ResponseEntity<Page<InformationBriefResponse>> pageInformationByParentCategoryAndZoneCategory(@RequestParam(defaultValue = "0") int page,
//                                                                                                         @PathVariable("parentCategoryId") Long categoryId,
//                                                                                                         @PathVariable("zoneCategory") Long zoneCategoryId,
//                                                                                                         HttpServletRequest request) {
//        Long userId = findUser(request);
//        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
//        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByParentCategoryAndZoneCategory(pageable, categoryId, userId, zoneCategoryId);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(briefInformationPage);
//    }


//    @GetMapping("/child-category/{childCategoryId}/zone-category/{zoneCategory}")
//    public ResponseEntity<Page<InformationBriefResponse>> pageInformationChildCategoryAndZoneCategory(@RequestParam(defaultValue = "0") int page,
//                                                                                                      @PathVariable("childCategoryId") Long categoryId,
//                                                                                                      @PathVariable("zoneCategory") Long zoneCategoryId,
//                                                                                                      HttpServletRequest request) {
//        Long userId = findUser(request);
//        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
//        Page<InformationBriefResponse> briefInformationPage = informationService.getBriefInformationPageByChildCategoryAndZoneCategory(pageable, categoryId, userId, zoneCategoryId);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(briefInformationPage);
//    }


    @GetMapping("/ranks")
    public ResponseEntity<List<InformationRankResponse>> rankInformation() {
        List<InformationRankResponse> rankInformation = informationService.getRankInformation();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rankInformation);
    }

    @GetMapping("/main-page")
    public ResponseEntity<List<InformationMainResponse>> mainPageInformation(HttpServletRequest request) {
        Long userId = findUser(request);

        List<InformationMainResponse> informationList = informationService.getMainPageInformation(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(informationList);
    }


    private Long findUser(HttpServletRequest request) {
        String token = CookieExtractor.findToken("access_token", request.getCookies());

        if (Objects.isNull(token)) {
            token = CookieExtractor.findToken("refresh_token", request.getCookies());
        }
        if (Objects.isNull(token)) {
            return (long) 0;
        }

        return jwtTokenProvider.getPayload(token);
    }

}

