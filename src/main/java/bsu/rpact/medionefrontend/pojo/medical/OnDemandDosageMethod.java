package bsu.rpact.medionefrontend.pojo.medical;

public class OnDemandDosageMethod extends DosageMethod{
    private double amount;
    private String unit;

    public OnDemandDosageMethod(double amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public OnDemandDosageMethod() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
