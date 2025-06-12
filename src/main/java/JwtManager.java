import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtManager {
    // pour SHA256 : 256 bits mini
    private static final String SECRET_KEY = "bachibouzoukbachibouzoukbachibouzoukbachibouzouk";

    public static String createJWT(String login) {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(3600);
        Date expDate = Date.from(expiration);

        return Jwts.builder()
                .id(UUID.randomUUID().toString().replace("-", ""))
                .issuedAt(Date.from(now))
                .subject("Authentification pour TP 3 Web services")
                .issuer("maxime.morge@univ-lyon1.fr")
                .expiration(expDate)
                .claim("login", login) // 💡 Ajout du login ici
                .signWith(signingKey)
                .compact();
    }

    public static Claims decodeJWT(String jwt) throws Exception {
        // This line will throw an exception if it is not a signed JWS (as expected)
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey signingKey = Keys.hmacShaKeyFor(keyBytes);
        JwtParser parser = Jwts.parser()
                .verifyWith(signingKey)
                .build();
        Claims claims = parser.parseSignedClaims(jwt).getPayload();
        return claims;
    }

    // Exemple de fonctionnement
    public static void main(String args[]) {
        System.out.println(JwtManager.SECRET_KEY);
        String token = JwtManager.createJWT("bob");
        /*
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        System.out.println(token);
        Claims claims = null;
        try {
            claims = JwtManager.decodeJWT(token);
        } catch (Exception e) {
            System.out.println("jeton invalide " + e.getMessage());
            System.exit(1);
        }
        System.out.println(claims.toString());
    }
}
