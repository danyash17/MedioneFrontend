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
@Order(2)
public class SetAuthorizationRequestHeaderFilter implements Filter {
    @Value("${cookie.name.jwt}")
    private String jwtCookieName;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse responce = (HttpServletResponse) servletResponse;
        Optional<Cookie> jwtCookie = Optional.empty();
        if(request.getCookies()!=null) {
            jwtCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> jwtCookieName.equals(cookie.getName()))
                    .findAny();
        }
        if (jwtCookie.isPresent() && jwtCookie.get().getValue() != null) {
            RequestDecorator requestWrapper = new RequestDecorator(request);
            requestWrapper.addHeader("Authorization", " Bearer " + jwtCookie.get().getValue());
            filterChain.doFilter(requestWrapper, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
