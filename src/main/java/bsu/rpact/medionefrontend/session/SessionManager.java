package bsu.rpact.medionefrontend.session;

import bsu.rpact.medionefrontend.pojo.authentication.JwtResponce;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedHttpSession;
import com.vaadin.flow.server.WrappedSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    @Value("${cookie.name.jwt}")
    private String jwt;

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

    public String getIdAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (String) session.getAttribute("id");
    }

    public String getFirstNameAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (String) session.getAttribute("firstName");
    }

    public String getLastNameAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (String) session.getAttribute("lastName");
    }

    public String getPatronymicAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (String) session.getAttribute("patronymic");
    }

    public String getPhoneAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (String) session.getAttribute("phone");
    }

    public String getRoleAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (String) session.getAttribute("role");
    }

    public Boolean get2FaAttribute(){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return (Boolean) session.getAttribute("2FA");
    }

    public void set2FaAttribute(boolean twoFactorAuth){
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        session.setAttribute("2FA",twoFactorAuth);
    }

    public void setTokenAttribute(String token) {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        session.setAttribute(jwt, token);
    }

    public String getTokenAttribute() {
        WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
        return String.valueOf(session.getAttribute(jwt));
    }
}
