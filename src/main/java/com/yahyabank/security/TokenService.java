package com.yahyabank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA256;
import static software.amazon.awssdk.auth.signer.internal.SigningAlgorithm.HmacSHA256;

@Service
public class TokenService {


    //===============================================================================================================================================

    private SecretKey key;

    @Value("${jwt.secret.string}")
    private String JWT_SECRET;

    @Value("${jwt.expiration.time}")
    private long JWT_EXPIRATION_TIME;


    //===============================================================================================================================================


    @PostConstruct
    private void init() {
        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }


    //===============================================================================================================================================


    public String createToken(String email) {
//        String permissions = getPermissionsFromRoles(roles);
        return Jwts.builder().subject(email).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME)).signWith(key)
//                .claim("scope",permissions)
                .compact();
    }

//===============================================================================================================================================


    public String getUserFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }


    //===============================================================================================================================================


    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {


        return claimsResolver.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

//===============================================================================================================================================


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUserFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

//===============================================================================================================================================

    private boolean isTokenExpired(String token) {

        return extractClaims(token, Claims::getExpiration).before(new Date());
    }


//===============================================================================================================================================

    private String getPermissionsFromRoles(String roles) {
        Set<String> permissions = new HashSet<>();

        if (roles.contains("ROLE_ADMIN")) {
            permissions.addAll(List.of("READ", "WRITE", "DELETE"));
        }
        if (roles.contains("ROLE_MANAGER")) {
            permissions.add("READ");
        }
        if (roles.contains("ROLE_USER")) {
            permissions.add("READ");
        }

        return String.join(" ", permissions);
    }


}
