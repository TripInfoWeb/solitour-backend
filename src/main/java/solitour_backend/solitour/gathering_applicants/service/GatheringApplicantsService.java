package solitour_backend.solitour.gathering_applicants.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.exception.GatheringNotExistsException;
import solitour_backend.solitour.gathering.repository.GatheringRepository;
import solitour_backend.solitour.gathering_applicants.dto.request.GatheringApplicantsModifyRequest;
import solitour_backend.solitour.gathering_applicants.entity.GatheringApplicants;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsAlreadyExistsException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsAlreadyFullPeopleException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsManagerException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsNotExistsException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringNotManagerException;
import solitour_backend.solitour.gathering_applicants.repository.GatheringApplicantsRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class GatheringApplicantsService {
    private final GatheringRepository gatheringRepository;
    private final GatheringApplicantsRepository gatheringApplicantsRepository;
    private final UserRepository userRepository;

    public void participateGatheringFromAnotherUser(Long userId, Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(
                        () ->
                                new GatheringNotExistsException("해당하는 id의 gathering 이 존재 하지 않습니다"));

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () ->
                                new UserNotExistsException("해당하는 id의 user가 없습니다"));

        if (Objects.equals(gathering.getUser(), user)) {
            throw new GatheringApplicantsManagerException("모임을 만든 사람은 해당 모임에 무조건 참여 하여 이미 있습니다");
        }

        if (gatheringApplicantsRepository.existsByGatheringIdAndUserId(gathering.getId(), user.getId())) {
            throw new GatheringApplicantsAlreadyExistsException("해당 유저는 이미 참여 해 있습니다.");
        }

        Integer personCount = gathering.getPersonCount();
        int nowPersonCount = gatheringApplicantsRepository.countAllByGathering_IdAndGatheringStatus(gatheringId,
                GatheringStatus.CONSENT);

        if (personCount <= nowPersonCount) {
            throw new GatheringApplicantsAlreadyFullPeopleException("이미 인원이 가득 찼습니다.");
        }

        GatheringApplicants gatheringApplicants = new GatheringApplicants(gathering, user, GatheringStatus.WAIT);

        gatheringApplicantsRepository.save(gatheringApplicants);
    }

    public void deleteGatheringApplicantsFromAnotherUser(Long userId, Long gatheringId) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(
                        () ->
                                new GatheringNotExistsException("해당하는 id의 gathering 이 존재 하지 않습니다"));

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () ->
                                new UserNotExistsException("해당하는 id의 user 가 없습니다"));

        if (Objects.equals(gathering.getUser(), user)) {
            throw new GatheringApplicantsManagerException("모임을 만든 사람은 해당 모임에 빠질 수 없습니다.");
        }

        GatheringApplicants gatheringApplicants = gatheringApplicantsRepository.findByGatheringIdAndUserId(
                        gathering.getId(), user.getId())
                .orElseThrow(
                        () ->
                                new GatheringApplicantsNotExistsException(
                                        "해당하는 모임과 user 의 gathering applicants 가 없습니다."));

        gatheringApplicantsRepository.delete(gatheringApplicants);
    }

    public boolean updateGatheringApplicantsManagement(Long userId, Long gatheringId,
                                                       GatheringApplicantsModifyRequest gatheringApplicantsModifyRequest) {
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(
                        () ->
                                new GatheringNotExistsException("해당하는 id의 gathering 이 존재 하지 않습니다"));

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () ->
                                new UserNotExistsException("해당하는 id의 user 가 없습니다"));

        if (!Objects.equals(gathering.getUser(), user)) {
            throw new GatheringNotManagerException("해당하는 user 가 해당 gathering 의 manage 가 아닙니다");
        }

        GatheringApplicants gatheringApplicants = gatheringApplicantsRepository.findByGatheringIdAndUserId(
                        gathering.getId(), gatheringApplicantsModifyRequest.getUserId())
                .orElseThrow(
                        () ->
                                new GatheringApplicantsNotExistsException("해당하는 모임, user 의 applicants 가 없습니다")
                );

        if (Objects.equals(gathering.getUser(), gatheringApplicants.getUser())) {
            throw new GatheringApplicantsManagerException("모임을 만든 사람은 해당 모임 참여에 대한 수정이 불가 합니다.");
        }

        if (Objects.equals(gatheringApplicants.getGatheringStatus(),
                gatheringApplicantsModifyRequest.getGatheringStatus())) {
            return false;
        }

        gatheringApplicants.setGatheringStatus(gatheringApplicantsModifyRequest.getGatheringStatus());
        return true;
    }
}
