package bsu.rpact.medionefrontend.pojo;

public class DoctorPojo {
    private String hospital;
    private boolean available;
    private String commonInfo;
    private byte[] photo;

    public DoctorPojo(String hospital, boolean available, String commonInfo, byte[] photo) {
        this.hospital = hospital;
        this.available = available;
        this.commonInfo = commonInfo;
        this.photo = photo;
    }

    public DoctorPojo() {
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCommonInfo() {
        return commonInfo;
    }

    public void setCommonInfo(String commonInfo) {
        this.commonInfo = commonInfo;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
