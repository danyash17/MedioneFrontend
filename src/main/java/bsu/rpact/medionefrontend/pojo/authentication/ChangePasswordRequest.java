package bsu.rpact.medionefrontend.pojo.authentication;

public class ChangePasswordRequest {

    private String decodedPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String decodedPassword) {
        this.decodedPassword = decodedPassword;
    }

    public String getDecodedPassword() {
        return decodedPassword;
    }

    public void setDecodedPassword(String decodedPassword) {
        this.decodedPassword = decodedPassword;
    }
}
