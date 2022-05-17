package bsu.rpact.medionefrontend.pojo.authentication;

public class TotpResponce {
    private String qrUri;

    public TotpResponce(String qrUri) {
        this.qrUri = qrUri;
    }

    public TotpResponce() {
    }

    public String getQrUri() {
        return qrUri;
    }

    public void setQrUri(String qrUri) {
        this.qrUri = qrUri;
    }
}
