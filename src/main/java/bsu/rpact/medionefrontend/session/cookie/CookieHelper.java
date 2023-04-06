package bsu.rpact.medionefrontend.session.cookie;

import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
public class CookieHelper {

    @Value("${cookie.name.jwt}")
    private String jwtCookieName;
    @Value("${cookie.name.terminator}")
    private String terminatorCookieName;

    public void addTokenCookie(String token, int age){
        Cookie jwtTokenCookie = new Cookie(jwtCookieName, token);
        jwtTokenCookie.setMaxAge(age);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        VaadinService.getCurrentResponse().addCookie(jwtTokenCookie);
    }

    public String getJwtCookieName() {
        return jwtCookieName;
    }

    public void setJwtCookieName(String jwtCookieName) {
        this.jwtCookieName = jwtCookieName;
    }

    public void generateTerminatorCookie() {
        Cookie terminator = new Cookie(terminatorCookieName, "true");
        terminator.setMaxAge(90000);
        terminator.setSecure(true);
        terminator.setHttpOnly(true);
        VaadinService.getCurrentResponse().addCookie(terminator);
    }
}
