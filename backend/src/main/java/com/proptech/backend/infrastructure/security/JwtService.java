package com.proptech.backend.infrastructure.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey; // expected base64-encoded key

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final ObjectMapper mapper = new ObjectMapper();

    public String extractUsername(String token) {
        Map<String, Object> claims = decodePayload(token);
        Object sub = claims.get("sub");
        return sub != null ? sub.toString() : null;
    }

    public <T> T extractClaim(String token, Function<Map<String, Object>, T> resolver) {
        Map<String, Object> claims = decodePayload(token);
        return resolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long now = System.currentTimeMillis() / 1000L;
        Map<String, Object> payload = new HashMap<>();
        if (extraClaims != null) payload.putAll(extraClaims);
        payload.put("sub", userDetails.getUsername());
        payload.put("iat", now);
        payload.put("exp", now + (jwtExpiration / 1000L));

        try {
            String headerJson = mapper.writeValueAsString(Map.of("alg", "HS256", "typ", "JWT"));
            String payloadJson = mapper.writeValueAsString(payload);
            String headerB = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadB = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String signingInput = headerB + "." + payloadB;
            String signature = signHmacSha256(signingInput, secretKey);
            return signingInput + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (!verifySignature(token)) return false;
        String username = extractUsername(token);
        if (username == null || !username.equals(userDetails.getUsername())) return false;
        Map<String, Object> claims = decodePayload(token);
        Object exp = claims.get("exp");
        if (exp != null) {
            long expL = Long.parseLong(exp.toString());
            return expL * 1000L > System.currentTimeMillis();
        }
        return true;
    }

    private boolean verifySignature(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            String signingInput = parts[0] + "." + parts[1];
            String expectedSig = signHmacSha256(signingInput, secretKey);
            return constantTimeEquals(expectedSig, parts[2]);
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, Object> decodePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return Map.of();
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            return mapper.readValue(decoded, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String signHmacSha256(String data, String base64Key) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
