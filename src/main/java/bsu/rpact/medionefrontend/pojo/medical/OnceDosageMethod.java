package bsu.rpact.medionefrontend.pojo.medical;

public class OnceDosageMethod extends DosageMethod {
    private double amount;
    private String unit;

    public OnceDosageMethod(double amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public OnceDosageMethod() {
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

    public boolean isEmpty() {
        return unit == null && amount == 0.0;
    }

    @Override
    public String toString() {
        return getAmountString() + " " + unit;
    }

    private String getAmountString(){
        return amount % 1 == 0 ? String.valueOf((int)amount) : String.valueOf(amount);
    }
}
