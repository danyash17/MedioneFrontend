package bsu.rpact.medionefrontend.session;

import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedHttpSession;
import com.vaadin.flow.server.WrappedSession;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    public void generateAuthUserAttributes(JwtResponce jwtResponce){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        session.setAttribute("id",jwtResponce.getId());
        session.setAttribute("firstName",jwtResponce.getFirstName());
        session.setAttribute("lastName",jwtResponce.getLastName());
        session.setAttribute("patronymic",jwtResponce.getPatronymic());
        session.setAttribute("login",jwtResponce.getLogin());
        session.setAttribute("phone",jwtResponce.getPhone());
        session.setAttribute("role",jwtResponce.getRole().name());
    }

    public void set2FaAttribute(boolean twoFactorAuth){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        session.setAttribute("2FA",twoFactorAuth);
    }

}
