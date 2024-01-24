package bsu.rpact.medionefrontend.pojo;

import java.time.LocalDate;

public class MedcardPojo {
    private LocalDate dateCreated;
    private LocalDate validTo;
    private String residentalAddress;

    public MedcardPojo(LocalDate dateCreated, LocalDate validTo, String residentalAddress) {
        this.dateCreated = dateCreated;
        this.validTo = validTo;
        this.residentalAddress = residentalAddress;
    }

    public MedcardPojo() {
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public String getResidentalAddress() {
        return residentalAddress;
    }

    public void setResidentalAddress(String residentalAddress) {
        this.residentalAddress = residentalAddress;
    }
}
