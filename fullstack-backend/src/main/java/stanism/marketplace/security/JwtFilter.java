package stanism.marketplace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter that processes JWT tokens in incoming requests.
 * This filter extracts and validates JWT tokens from the cookie
 * and sets up the Spring Security context accordingly.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

  /** Logger instance for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

  /** Cookie name for JWT token. */
  private static final String JWT_COOKIE_NAME = "Authorization";

  /** JWT utility for token validation and extraction. */
  private final JwtUtil jwtUtil;

  /**
   * Constructs a new JwtFilter instance.
   *
   * @param jwtUtil The JWT utility to use for token operations
   */
  public JwtFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * Processes the JWT token in the request and sets up authentication if valid.
   *
   * @param request     The HTTP request
   * @param response    The HTTP response
   * @param filterChain The filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String token = extractTokenFromCookie(request);

      if (token == null) {
        filterChain.doFilter(request, response);
        return;
      }

      if (jwtUtil.validateToken(token)) {
        String username = jwtUtil.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              username,
              null,
              Collections.emptyList());

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
          LOGGER.debug("Authenticated user: {}", username);
        }
      } else {
        LOGGER.warn("Invalid JWT token");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    } catch (Exception e) {
      LOGGER.error("Failed to process JWT authentication", e);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracts the JWT token from the request cookie.
   *
   * @param request The HTTP request
   * @return The JWT token if found, null otherwise
   */
  private String extractTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (JWT_COOKIE_NAME.equals(cookie.getName())) {
          String token = cookie.getValue();
          
          
          return token;
        }
      }
    }
    return null;
  }

  /**
   * Determines if the filter should be skipped for the given request.
   * Skips filtering for public endpoints like login and registration.
   *
   * @param request The HTTP request
   * @return true if the filter should be skipped, false otherwise
   */
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // Skip filtering for public endpoints
    String path = request.getRequestURI();
    return path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/register") ||
        path.startsWith("/api/auth/isLoggedIn") ||
        path.startsWith("/api/auth/logout");
  }
}