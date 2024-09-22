package solitour_backend.solitour.gathering.controller;

import static solitour_backend.solitour.information.controller.InformationController.PAGE_SIZE;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.exception.TokenNotExistsException;
import solitour_backend.solitour.auth.exception.TokenNotValidException;
import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.gathering.dto.request.GatheringModifyRequest;
import solitour_backend.solitour.gathering.dto.request.GatheringPageRequest;
import solitour_backend.solitour.gathering.dto.request.GatheringRegisterRequest;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringDetailResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringRankResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringResponse;
import solitour_backend.solitour.gathering.service.GatheringService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gatherings")
public class GatheringController {
    private final GatheringService gatheringService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<GatheringResponse> createGathering(@AuthenticationPrincipal Long userId,
                                                             @Valid @RequestBody GatheringRegisterRequest gatheringRegisterRequest,
                                                             BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);

        if (gatheringRegisterRequest.getEndAge() > gatheringRegisterRequest.getStartAge()) {
            throw new RequestValidationFailedException("시작 나이 연도가 끝 나이 연도 보다 앞에 있네요");
        }
        if (gatheringRegisterRequest.getScheduleStartDate().isAfter(gatheringRegisterRequest.getScheduleEndDate())) {
            throw new RequestValidationFailedException("시작 날짜는 종료 날짜보다 앞에 있어야 합니다.");
        }

        if (gatheringRegisterRequest.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RequestValidationFailedException("마감일은 현재 시간보다 이후여야 합니다.");
        }

        GatheringResponse gatheringResponse = gatheringService.registerGathering(userId, gatheringRegisterRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gatheringResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GatheringDetailResponse> getGatheringDetail(@PathVariable Long id,
                                                                      HttpServletRequest request,
                                                                      HttpServletResponse response) {
        Long userId = findUser(request);
        GatheringDetailResponse gatheringDetail = gatheringService.getGatheringDetail(userId, id, request, response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gatheringDetail);
    }

    @PutMapping("/{gatheringId}")
    public ResponseEntity<GatheringResponse> updateGathering(@AuthenticationPrincipal Long userId,
                                                             @PathVariable Long gatheringId,
                                                             @Valid @RequestBody GatheringModifyRequest gatheringModifyRequest) {

        if (gatheringModifyRequest.getEndAge() > gatheringModifyRequest.getStartAge()) {
            throw new RequestValidationFailedException("시작 나이 연도가 끝 나이 연도 보다 앞에 있네요");
        }

        if (gatheringModifyRequest.getScheduleStartDate().isAfter(gatheringModifyRequest.getScheduleEndDate())) {
            throw new RequestValidationFailedException("시작 날짜는 종료 날짜보다 앞에 있어야 합니다.");
        }

        if (gatheringModifyRequest.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RequestValidationFailedException("마감일은 현재 시간보다 이후여야 합니다.");
        }

        GatheringResponse gatheringResponse = gatheringService.modifyGathering(userId, gatheringId,
                gatheringModifyRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gatheringResponse);
    }

    @DeleteMapping("/{gatheringId}")
    public ResponseEntity<Void> deleteGathering(@PathVariable Long gatheringId, HttpServletRequest request) {
        Long userId = findUser(request);

        gatheringService.deleteGathering(gatheringId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    @GetMapping
    public ResponseEntity<Page<GatheringBriefResponse>> pageGatheringSortAndFilter(@RequestParam(defaultValue = "0") int page,
                                                                                   @Valid @ModelAttribute GatheringPageRequest gatheringPageRequest,
                                                                                   BindingResult bindingResult,
                                                                                   HttpServletRequest request) {
        Utils.validationRequest(bindingResult);
        Long userId = findUser(request);

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<GatheringBriefResponse> pageGathering = gatheringService.getPageGathering(pageable, gatheringPageRequest,
                userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageGathering);
    }

    @GetMapping("/tag/search")
    public ResponseEntity<Page<GatheringBriefResponse>> getPageGatheringByTag(@RequestParam(defaultValue = "0") int page,
                                                                              @Valid @ModelAttribute GatheringPageRequest gatheringPageRequest,
                                                                              @RequestParam(required = false, name = "tagName") String tag,
                                                                              BindingResult bindingResult,
                                                                              HttpServletRequest request)
            throws UnsupportedEncodingException {
        String decodedValue = java.net.URLDecoder.decode(tag, "UTF-8");
        String filteredTag = decodedValue.replaceAll("[^a-zA-Z0-9가-힣]", "");

        Utils.validationRequest(bindingResult);
        Long userId = findUser(request);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<GatheringBriefResponse> briefGatheringPage = gatheringService.getPageGatheringByTag(pageable, userId, gatheringPageRequest, filteredTag);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(briefGatheringPage);
    }

    @GetMapping("/ranks")
    public ResponseEntity<List<GatheringRankResponse>> getGatheringRankOrderByLikes() {
        List<GatheringRankResponse> gatheringRankOrderByLikes = gatheringService.getGatheringRankOrderByLikes();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gatheringRankOrderByLikes);
    }

    @PutMapping("/finish/{gatheringId}")
    public ResponseEntity<Void> gatheringFinish(@AuthenticationPrincipal Long userId,
                                                @PathVariable Long gatheringId) {
        gatheringService.setGatheringFinish(userId, gatheringId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/not-finish/{gatheringId}")
    public ResponseEntity<Void> gatheringNotFinish(@AuthenticationPrincipal Long userId,
                                                   @PathVariable Long gatheringId) {

        gatheringService.setGatheringNotFinish(userId, gatheringId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


    @GetMapping("/home")
    public ResponseEntity<List<GatheringBriefResponse>> getHomeGathering(HttpServletRequest request) {
        Long userId = findUser(request);

        List<GatheringBriefResponse> gatheringOrderByLikesFilterByCreate3After = gatheringService.getGatheringOrderByLikesFilterByCreate3After(
                userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gatheringOrderByLikesFilterByCreate3After);
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
