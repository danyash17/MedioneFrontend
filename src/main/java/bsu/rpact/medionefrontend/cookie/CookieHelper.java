package bsu.rpact.medionefrontend.cookie;

import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class CookieHelper {

    @Value("${cookie.name.jwt}")
    private String jwtCookieName;

    public void initTokenCookie(String token){
        Cookie jwtTokenCookie = new Cookie(jwtCookieName, token);
        jwtTokenCookie.setMaxAge(9000000);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        VaadinService.getCurrentResponse().addCookie(jwtTokenCookie);
    }

}
