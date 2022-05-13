package bsu.rpact.medionefrontend.pojo;

import java.util.Date;

public class MedcardPojo {
    private Date dateCreated;
    private Date validTo;
    private String residentalAddress;

    public MedcardPojo(Date dateCreated, Date validTo, String residentalAddress) {
        this.dateCreated = dateCreated;
        this.validTo = validTo;
        this.residentalAddress = residentalAddress;
    }

    public MedcardPojo() {
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getResidentalAddress() {
        return residentalAddress;
    }

    public void setResidentalAddress(String residentalAddress) {
        this.residentalAddress = residentalAddress;
    }
}
