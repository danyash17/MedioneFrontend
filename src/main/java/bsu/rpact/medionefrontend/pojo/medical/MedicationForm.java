package bsu.rpact.medionefrontend.pojo.medical;

public class MedicationForm {

    private String code;
    private String display;

    public MedicationForm(String code, String display) {
        this.code = code;
        this.display = display;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }

}
