package stanism.marketplace.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for handling JWT (JSON Web Token) operations.
 * This class provides methods for generating, validating, and extracting
 * information from JWT tokens.
 */
@Component
public class JwtUtil {
  /** Logger instance for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

  /** Secret key string for JWT signing. */
  @Value("${jwt.secret}")
  private String secretKeyString;

  /** Token expiration time in milliseconds. */
  @Value("${jwt.expiration}")
  private long expirationTime;

  /** Secret key used for JWT signing. */
  private final SecretKey secretKey;

  /**
   * Constructs a new JwtUtil instance with the provided secret key and expiration
   * time.
   *
   * @param secretKeyString The secret key string for JWT signing
   * @param expirationTime  The token expiration time in milliseconds
   */
  public JwtUtil(
      @Value("${jwt.secret}") String secretKeyString,
      @Value("${jwt.expiration}") long expirationTime) {
    this.secretKeyString = secretKeyString;
    this.expirationTime = expirationTime;
    this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
  }

  /**
   * Generates a JWT token for the given username.
   *
   * @param username The username to include in the token
   * @return The generated JWT token
   */
  public String generateToken(String username) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationTime);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Extracts the username (subject) from the token.
   *
   * @param token The JWT token to extract the username from
   * @return The username extracted from the token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from the token.
   *
   * @param token The JWT token to extract the expiration date from
   * @return The expiration date extracted from the token
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Generic method to extract a claim using a claims resolver.
   *
   * @param <T>            The type of the claim to extract
   * @param token          The JWT token to extract the claim from
   * @param claimsResolver The function to resolve the claim
   * @return The extracted claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Retrieves all claims from the token.
   *
   * @param token The JWT token to extract claims from
   * @return All claims from the token
   */
  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Validates the JWT token.
   *
   * @param token The JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (SignatureException e) {
      LOGGER.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      LOGGER.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      LOGGER.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      LOGGER.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      LOGGER.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  /**
   * Checks if the token is expired.
   *
   * @param token The JWT token to check
   * @return true if the token is expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Returns token expiration time in milliseconds.
   *
   * @return The token expiration time in milliseconds
   */
  public long getExpirationTime() {
    return expirationTime;
  }
}