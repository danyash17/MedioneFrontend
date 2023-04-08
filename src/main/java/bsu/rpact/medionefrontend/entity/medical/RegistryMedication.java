package bsu.rpact.medionefrontend.entity.medical;

import bsu.rpact.medionefrontend.pojo.other.Href;

import java.util.ArrayList;
import java.util.List;

public class RegistryMedication extends RcethRegistryItem{
    private String orderNumber;
    private List<Href> hrefs;
    private String tradeName;
    private String internationalName;
    private String manufacturer;
    private String applicant;
    private String idNumber;
    private String registrationDate;
    private String expirationDate;
    private String original;

    public RegistryMedication() {
        hrefs = new ArrayList<>();
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public List<Href> getHrefs() {
        return hrefs;
    }

    public void setHrefs(List<Href> hrefs) {
        this.hrefs = hrefs;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setInternationalName(String internationalName) {
        this.internationalName = internationalName;
    }

    public String getInternationalName() {
        return internationalName;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getOriginal() {
        return original;
    }
}
