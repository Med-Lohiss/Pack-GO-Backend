package com.packandgo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_EXPIRATION_TIME}")
    private long expirationTime;

    private Key signingKey;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("JWT_SECRET_KEY no puede ser nulo o vacío");
        }
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String extraerCorreo(String token) {
        return extraerDato(token, Claims::getSubject);
    }

    public List<String> extraerRoles(String token) {
        Claims claims = obtenerClaims(token);
        return objectMapper.convertValue(claims.get("roles"), new TypeReference<>() {});
    }

    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        return generarToken(claims, userDetails);
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        final String correo = extraerCorreo(token);
        return correo.equals(userDetails.getUsername()) && !estaExpirado(token);
    }

    private <T> T extraerDato(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerClaims(token);
        return claimsResolver.apply(claims);
    }

    private String generarToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean estaExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerDato(token, Claims::getExpiration);
    }

    private Claims obtenerClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expirado", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Token no soportado", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token mal formado", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token vacío o inválido", e);
        }
    }

    // Nuevos métodos integrados para validar el token y extraer el username

    // Validar si el token es válido
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    // Obtener el nombre de usuario desde el token
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer la fecha de expiración
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Verificar si el token ha expirado
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Método auxiliar para extraer cualquier tipo de claim del token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public String generarRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // No agregamos roles en el refresh token (puedes ajustarlo si lo necesitas)
        return generarToken(claims, userDetails);  // Utiliza el mismo método de generación de token con diferentes claims
    }

}
