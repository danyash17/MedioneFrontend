package bsu.rpact.medionefrontend.pojo;

import java.time.LocalDate;

public class IllnessPojo {
    private LocalDate illFrom;
    private LocalDate illTo;
    private String description;

    public IllnessPojo(LocalDate illFrom, LocalDate illTo, String description) {
        this.illFrom = illFrom;
        this.illTo = illTo;
        this.description = description;
    }

    public IllnessPojo(String description) {
        this.description = description;
    }

    public IllnessPojo() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getIllFrom() {
        return illFrom;
    }

    public void setIllFrom(LocalDate illFrom) {
        this.illFrom = illFrom;
    }

    public LocalDate getIllTo() {
        return illTo;
    }

    public void setIllTo(LocalDate illTo) {
        this.illTo = illTo;
    }
}
