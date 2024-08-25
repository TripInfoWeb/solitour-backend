package solitour_backend.solitour.auth.support;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") final String secretKey,
                            @Value("${security.jwt.token.access-token-expire-length}") final long accessTokenValidityInMilliseconds,
                            @Value("${security.jwt.token.refresh-token-expire-length}") final long refreshTokenValidityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }

    public String createAccessToken(Long payload) {
        return createToken(payload, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(Long payload) {
        return createToken(payload, refreshTokenValidityInMilliseconds);
    }

    private String createToken(Long payload, long validityInMilliseconds) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(Long.toString(payload))
                .issuedAt(new Date())
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public Long getPayload(String token) {
        return Long.valueOf(
                getClaims(token).getPayload().getSubject());
    }

    public boolean validateTokenNotUsable(String token) {
        try {
            Jws<Claims> claims = getClaims(token);

            return claims.getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    private Jws<Claims> getClaims(String token) {
        final int CLOCK_SKEW_SECONDS = 3 * 60;
        return Jwts.parser().clockSkewSeconds(CLOCK_SKEW_SECONDS).verifyWith(key).build()
                .parseSignedClaims(token);
    }
}
