package bsu.rpact.medionefrontend.security.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(3)
public class CookieCleanseFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        RequestDecorator requestWrapper = new RequestDecorator(request);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            boolean terminate = false;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("terminator") && cookie.getValue().equals("true")) {
                    terminate = true;
                }
            }
            if(terminate) {
                for (Cookie ck : cookies) {
                    ck.setValue("");
                    ck.setPath("/");
                    ck.setMaxAge(0);
                    response.addCookie(ck);
                    requestWrapper.deleteCookie(ck);
                }
            }
        }

        filterChain.doFilter(requestWrapper, response);
    }
}
