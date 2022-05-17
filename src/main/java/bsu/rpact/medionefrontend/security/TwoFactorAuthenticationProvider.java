package bsu.rpact.medionefrontend.security;

import bsu.rpact.medionefrontend.pojo.authentication.LoginRequest;
import bsu.rpact.medionefrontend.pojo.authentication.TotpResponce;
import org.springframework.stereotype.Component;

@Component
public class TwoFactorAuthenticationProvider {

    private LoginRequest temporaryRequest;
    private TotpResponce totpResponce;

    public TwoFactorAuthenticationProvider() {
    }

    public LoginRequest getTemporaryRequest() {
        return temporaryRequest;
    }

    public void setTemporaryRequest(LoginRequest temporaryRequest) {
        this.temporaryRequest = temporaryRequest;
    }

    public TotpResponce getTotpResponce() {
        return totpResponce;
    }

    public void setTotpResponce(TotpResponce totpResponce) {
        this.totpResponce = totpResponce;
    }
}
