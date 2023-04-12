package bsu.rpact.medionefrontend.pojo.medical;

public class TetrationDosageMethod extends PeriodicalDosageMethod {
    private double coefficient;
    private String coefTrend;

    public TetrationDosageMethod() {
    }

    public TetrationDosageMethod(double coefficient, String coefTrend) {
        this.coefficient = coefficient;
        this.coefTrend = coefTrend;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getCoefTrend() {
        return coefTrend;
    }

    public void setCoefTrend(String coefTrend) {
        this.coefTrend = coefTrend;
    }
}
