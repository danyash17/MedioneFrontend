package bsu.rpact.medionefrontend.security.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@Order(0)
public class TerminationFilter implements Filter {
    @Value("${cookie.name.terminator}")
    private String terminatorCookieName;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse responce = (HttpServletResponse) servletResponse;
        Optional<Cookie> terminatorCookie = Optional.empty();
        if(request.getCookies()!=null) {
            terminatorCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> terminatorCookieName.equals(cookie.getName()))
                    .findAny();
        }
        if (terminatorCookie.isPresent() && terminatorCookie.get().getValue() != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null)
                for (Cookie cookie : cookies) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    responce.addCookie(cookie);
                }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
