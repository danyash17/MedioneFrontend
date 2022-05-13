package bsu.rpact.medionefrontend.pojo;

import java.util.Date;

public class IllnessPojo {
    private Date illFrom;
    private Date illTo;
    private String description;

    public IllnessPojo(Date illFrom, Date illTo, String description) {
        this.illFrom = illFrom;
        this.illTo = illTo;
        this.description = description;
    }

    public IllnessPojo(String description) {
        this.description = description;
    }

    public IllnessPojo() {
    }

    public Date getIllFrom() {
        return illFrom;
    }

    public void setIllFrom(Date illFrom) {
        this.illFrom = illFrom;
    }

    public Date getIllTo() {
        return illTo;
    }

    public void setIllTo(Date illTo) {
        this.illTo = illTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
