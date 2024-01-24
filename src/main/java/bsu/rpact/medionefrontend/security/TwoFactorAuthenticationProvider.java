package bsu.rpact.medionefrontend.security;

import bsu.rpact.medionefrontend.pojo.authentication.LoginRequest;
import bsu.rpact.medionefrontend.pojo.authentication.PrimaryLoginResponce;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorAuthenticationProvider {

    private LoginRequest temporaryRequest;
    private PrimaryLoginResponce primaryLoginResponce;

    public TwoFactorAuthenticationProvider() {
    }

    public LoginRequest getTemporaryRequest() {
        return temporaryRequest;
    }

    public void setTemporaryRequest(LoginRequest temporaryRequest) {
        this.temporaryRequest = temporaryRequest;
    }

    public PrimaryLoginResponce getTotpResponce() {
        return primaryLoginResponce;
    }

    public void setTotpResponce(PrimaryLoginResponce primaryLoginResponce) {
        this.primaryLoginResponce = primaryLoginResponce;
    }
}
