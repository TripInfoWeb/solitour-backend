package solitour_backend.solitour.great_information.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.great_information.dto.response.GreatInformationResponse;
import solitour_backend.solitour.great_information.entity.GreatInformation;
import solitour_backend.solitour.great_information.repository.GreatInformationRepository;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.repository.InformationRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GreatInformationService {

    private final GreatInformationRepository greatInformationRepository;
    private final UserRepository userRepository;
    private final InformationRepository informationRepository;

    public GreatInformationResponse getInformationGreatCount(Long informationId) {
        int greatInformation = greatInformationRepository.countByInformationId(informationId);

        return new GreatInformationResponse(greatInformation);
    }

    @Transactional
    public GreatInformation createInformationGreat(Long userId, Long infoId) {
        User user = userRepository.findByUserId(userId);
        Information information = informationRepository.findById(infoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정보가 없습니다."));

        return greatInformationRepository.findByIdAndUserId(infoId, userId)
                .orElseGet(
                        () -> greatInformationRepository.save(new GreatInformation(user, information)));
    }

    @Transactional
    public void deleteInformationGreat(Long userId, Long bookMarkId) {
        GreatInformation greatInformation = greatInformationRepository.findByIdAndUserId(bookMarkId,
                        userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 정보에는 좋아요를 하지 않았습니다"));

        greatInformationRepository.delete(greatInformation);
    }
}
