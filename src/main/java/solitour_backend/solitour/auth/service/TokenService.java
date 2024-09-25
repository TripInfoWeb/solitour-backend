package solitour_backend.solitour.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.auth.entity.Token;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.auth.exception.TokenNotExistsException;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoTokenResponse;
import solitour_backend.solitour.user.entity.User;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void synchronizeRefreshToken(User user, String refreshToken) {
        Token token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new TokenNotExistsException("토큰이 존재하지 않습니다"));

        token.updateRefreshToken(refreshToken);
    }

    @Transactional
    public void deleteByMemberId(Long memberId) {
        tokenRepository.deleteByUserId(memberId);
    }

    @Transactional
    public Token saveToken(String refreshToken, User user) {
        Token token = Token.builder()
                .user(user)
                .oauthToken(refreshToken)
                .build();

        tokenRepository.save(token);

        return token;
    }
}
