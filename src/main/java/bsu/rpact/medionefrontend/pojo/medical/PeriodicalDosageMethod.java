package bsu.rpact.medionefrontend.pojo.medical;

public class PeriodicalDosageMethod extends DosageMethod{
    private double amount;
    private String unit;
    private int times;
    private int timePeriodQuantity;
    private String timePeriod;

    public PeriodicalDosageMethod() {
    }

    public PeriodicalDosageMethod(double amount, String unit, int times, int timePeriodQuantity, String timePeriod) {
        this.amount = amount;
        this.unit = unit;
        this.times = times;
        this.timePeriodQuantity = timePeriodQuantity;
        this.timePeriod = timePeriod;
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

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getTimePeriodQuantity() {
        return timePeriodQuantity;
    }

    public void setTimePeriodQuantity(int timePeriodQuantity) {
        this.timePeriodQuantity = timePeriodQuantity;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }
}
