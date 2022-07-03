package bsu.rpact.medionefrontend.security;

import javax.servlet.http.HttpServletRequest;

public class RestrictHelper {

    public static boolean isAllowed(HttpServletRequest request){
        String loginURI = request.getContextPath() + "/auth";
        boolean isLoginRequest = request.getRequestURI().equals(loginURI);

        String registerURI = request.getContextPath() + "/register";
        boolean isRegisterRequest = request.getRequestURI().equals(registerURI);

        String restrictedURI = request.getContextPath() + "/restricted";
        boolean isRestrictedRequest = request.getRequestURI().equals(restrictedURI);

        boolean vaadinInnerService = request.getRequestURI().contains("/VAADIN/") ||
                request.getRequestURI().contains("/vaadinServlet/") ||
                request.getRequestURI().equals("/") ||
                request.getRequestURI().equals("/sw.js") ||
                request.getRequestURI().contains("/images/");

        return isLoginRequest || isRegisterRequest || isRestrictedRequest || vaadinInnerService;
    }
}
