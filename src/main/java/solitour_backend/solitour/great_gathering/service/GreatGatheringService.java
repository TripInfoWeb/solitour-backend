package solitour_backend.solitour.great_gathering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.exception.GatheringNotExistsException;
import solitour_backend.solitour.gathering.repository.GatheringRepository;
import solitour_backend.solitour.great_gathering.entity.GreatGathering;
import solitour_backend.solitour.great_gathering.exception.GatheringGreatNotExistsException;
import solitour_backend.solitour.great_gathering.repository.GreatGatheringRepository;
import solitour_backend.solitour.great_information.exception.InformationGreatNotExistsException;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GreatGatheringService {

    private final GreatGatheringRepository greatGatheringRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;

    @Transactional
    public GreatGathering createGatheringGreat(Long userId, Long gatheringId) {
        User user = userRepository.findByUserId(userId);
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new GatheringNotExistsException("해당 모임이 없습니다."));

        return greatGatheringRepository.findByGatheringIdAndUserId(gatheringId, userId)
                .orElseGet(() -> greatGatheringRepository.save(new GreatGathering(user, gathering)));
    }

    @Transactional
    public void deleteGatheringGreat(Long userId, Long gatheringId) {
        GreatGathering greatGathering = greatGatheringRepository.findByGatheringIdAndUserId(gatheringId,
                        userId)
                .orElseThrow(() -> new GatheringGreatNotExistsException("해당 모임에는 좋아요를 하지 않았습니다"));

        greatGatheringRepository.delete(greatGathering);
    }
}
