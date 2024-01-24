package bsu.rpact.medionefrontend.pojo.authentication;

public class TwoFactorAuthenticationRequest extends LoginRequest{
    private String mfaAppTotpCode;
    private String smsProvidedCode;

    public TwoFactorAuthenticationRequest(String login, String password, String mfaAppTotpCode, String smsProvidedCode) {
        super(login, password);
        this.mfaAppTotpCode = mfaAppTotpCode;
        this.smsProvidedCode = smsProvidedCode;
    }

    public String getMfaAppTotpCode() {
        return mfaAppTotpCode;
    }

    public void setMfaAppTotpCode(String mfaAppTotpCode) {
        this.mfaAppTotpCode = mfaAppTotpCode;
    }

    public String getSmsProvidedCode() {
        return smsProvidedCode;
    }

    public void setSmsProvidedCode(String smsProvidedCode) {
        this.smsProvidedCode = smsProvidedCode;
    }
}
