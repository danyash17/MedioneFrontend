package bsu.rpact.medionefrontend.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@Order(1)
public class SetAuthorizationRequestHeaderFilter implements Filter {
    @Value("${cookie.name.jwt}")
    private String jwtCookieName;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> jwtCookieName.equals(cookie.getName()))
                .findAny();
        if (jwtCookie.isPresent() && jwtCookie.get().getValue() != null) {
            RequestDecorator requestWrapper = new RequestDecorator(request);
            requestWrapper.addHeader("Authorization", " Bearer " + jwtCookie.get().getValue());
            filterChain.doFilter(requestWrapper, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
