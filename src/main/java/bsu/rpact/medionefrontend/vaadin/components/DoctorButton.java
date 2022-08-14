package bsu.rpact.medionefrontend.vaadin.components;

import bsu.rpact.medionefrontend.enums.SpecialityName;
import com.vaadin.flow.component.button.Button;

public class DoctorButton extends Button {

    public DoctorButton(String speciality) {
        super(speciality);
    }

    private SpecialityName specialityName;
    private boolean selected;

    public SpecialityName getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(SpecialityName specialityName) {
        this.specialityName = specialityName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
