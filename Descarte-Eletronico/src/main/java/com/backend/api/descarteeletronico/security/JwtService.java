package com.backend.api.descarteeletronico.security;

import com.backend.api.descarteeletronico.model.usuario.Usuario;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtEncoder jwtEncoder;
  private final JwtProperties jwtProperties;

  public String generateToken(Usuario usuario) {
    Instant now = Instant.now();
    String authorities =
            usuario.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

    JwtClaimsSet claims =
            JwtClaimsSet.builder()
                    .subject(usuario.getEmail())
                    .issuedAt(now)
                    .expiresAt(now.plus(jwtProperties.expiration()))
                    .claim("scope", authorities)
                    .build();

    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  public long expiresInSeconds() {
    return jwtProperties.expiration().toSeconds();
  }
}
