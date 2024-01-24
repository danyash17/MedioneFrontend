package bsu.rpact.medionefrontend.pojo.authentication;

import bsu.rpact.medionefrontend.enums.Role;

public class PrimaryLoginResponce extends JwtResponce{
    private String qrUri;
    private boolean isEnabled2Fa;

    public PrimaryLoginResponce(String token, Integer id, String login,
                                String firstName, String lastName,
                                String patronymic, String phone, Role role, String qrUri, boolean isEnabled2Fa) {
        super(token, id, login, firstName, lastName, patronymic, phone, role);
        this.qrUri = qrUri;
        this.isEnabled2Fa = isEnabled2Fa;
    }

    public PrimaryLoginResponce() {
    }

    public String getQrUri() {
        return qrUri;
    }

    public void setQrUri(String qrUri) {
        this.qrUri = qrUri;
    }

    public boolean isEnabled2Fa() {
        return isEnabled2Fa;
    }

    public void setEnabled2Fa(boolean enabled2Fa) {
        isEnabled2Fa = enabled2Fa;
    }
}
