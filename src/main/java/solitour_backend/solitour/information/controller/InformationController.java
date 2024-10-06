package solitour_backend.solitour.information.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.UnsupportedEncodingException;
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
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.exception.TokenNotExistsException;
import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.information.dto.request.InformationCreateRequest;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.request.InformationUpdateRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationDetailResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.dto.response.InformationResponse;
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
    public ResponseEntity<InformationResponse> createInformation(@AuthenticationPrincipal Long userId,
                                                                 @Valid @RequestBody InformationCreateRequest informationCreateRequest,
                                                                 BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);

        InformationResponse informationResponse = informationService.registerInformation(userId, informationCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationResponse);
    }

    @GetMapping("/{informationId}")
    public ResponseEntity<InformationDetailResponse> getDetailInformation(@PathVariable Long informationId,
                                                                          HttpServletRequest request,
                                                                          HttpServletResponse response) {
        Long userId = findUser(request);
        InformationDetailResponse informationDetailResponse = informationService.getDetailInformation(userId, informationId, request, response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(informationDetailResponse);
    }

    @Authenticated
    @PutMapping("/{informationId}")
    public ResponseEntity<InformationResponse> modifyInformation(@AuthenticationPrincipal Long userId,
                                                                 @PathVariable Long informationId,
                                                                 @Valid @RequestBody InformationUpdateRequest informationUpdateRequest,
                                                                 BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);

        InformationResponse informationResponse = informationService.updateInformation(userId, informationId, informationUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationResponse);
    }

    @Authenticated
    @DeleteMapping("/{informationId}")
    public ResponseEntity<Void> deleteInformation(@AuthenticationPrincipal Long userId,
                                                  @PathVariable Long informationId) {
        informationService.deleteInformation(userId, informationId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<InformationBriefResponse>> pageInformationSortAndFilter(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "1") Long parentCategoryId,
                                                                                       @Valid @ModelAttribute InformationPageRequest informationPageRequest,
                                                                                       BindingResult bindingResult,
                                                                                       HttpServletRequest request) {
        Utils.validationRequest(bindingResult);
        Long userId = findUser(request);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> pageInformation = informationService.getPageInformation(pageable, userId,
                parentCategoryId, informationPageRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageInformation);
    }

    @GetMapping("/tag/search")
    public ResponseEntity<Page<InformationBriefResponse>> getPageInformationByTag(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "1") Long parentCategoryId,
                                                                                  @Valid @ModelAttribute InformationPageRequest informationPageRequest,
                                                                                  @RequestParam(required = false, name = "tagName") String tag,
                                                                                  BindingResult bindingResult,
                                                                                  HttpServletRequest request)
            throws UnsupportedEncodingException {
        String decodedValue = java.net.URLDecoder.decode(tag, "UTF-8");
        String filteredTag = decodedValue.replaceAll("[^a-zA-Z0-9가-힣]", "");

        Utils.validationRequest(bindingResult);
        Long userId = findUser(request);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> briefInformationPage = informationService.getPageInformationByTag(
                pageable, userId, parentCategoryId, informationPageRequest, filteredTag);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefInformationPage);
    }

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

        if (jwtTokenProvider.validateTokenNotUsable(token)) {
            throw new TokenNotExistsException("토큰이 존재하지 않습니다.");
        }

        return jwtTokenProvider.getPayload(token);
    }

}

