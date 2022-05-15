package bsu.rpact.medionefrontend.security.filter;

import bsu.rpact.medionefrontend.security.RestrictHelper;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Order(1)
public class RestrictedAccessFilter implements Filter {

    @Value("${frontend.mappings.restricted}")
    private String restrictedMapping;
    @Value("${frontend.mappings.preambule}")
    private String frontendPreambule;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Optional<String> header = Optional.ofNullable(request.getHeader("Authorization"));
        if ((header.isEmpty() || !header.get().contains(" Bearer ")) && !RestrictHelper.isAllowed(request)) {
            response.sendRedirect(restrictedMapping);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
