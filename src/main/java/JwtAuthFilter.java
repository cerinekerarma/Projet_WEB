import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/api/*")
public class JwtAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("""
            {
              "message": "Token manquant ou invalide",
              "status": "unauthorized"
            }
            """);
            return;
        }

        try {
            String token = authHeader.substring("Bearer ".length());
            JwtManager.decodeJWT(token); // Lève exception si invalide
            chain.doFilter(req, res); // OK → continuer vers la servlet
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("""
            {
              "message": "Token invalide ou expiré",
              "status": "unauthorized"
            }
            """);
        }
    }
}
