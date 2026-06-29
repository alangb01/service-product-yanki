package pe.nom.charlygastelo.app.yankiservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class JwtTokenValidator {

    @Value("${jwt.secret}")
    private String secret;

    public Single<UserDetails> validate(String token) {

        log.info("[JWT] Starting token validation.");

        return Single.fromCallable(() -> {

                    log.debug("[JWT] Building HMAC key for token validation.");
                    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

                    log.debug("[JWT] Parsing JWT token.");
                    Claims claims = Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String username = claims.getSubject();
                    List<String> roles = claims.get("roles", List.class);

                    log.info("[JWT] Token validated successfully. subject={}, roles={}", username, roles);

                    return User
                            .withUsername(username)
                            .password("") // no password needed for JWT
                            .roles(roles.toArray(new String[0]))
                            .build();
                })
                .doOnError(error ->
                        log.error("[JWT] Token validation failed. reason={}", error.getMessage(), error)
                );
    }
}
