package solitour_backend.solitour.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.auth.entity.Token;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.user.entity.User;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void synchronizeRefreshToken(User user, String refreshToken) {
        tokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(new Token(user, refreshToken))
                );
    }

    @Transactional
    public void deleteByMemberId(Long memberId) {
        tokenRepository.deleteByUserId(memberId);
    }
}
