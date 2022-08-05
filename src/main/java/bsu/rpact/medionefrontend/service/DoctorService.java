package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.DoctorAdapter;
import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.enums.SpecialityName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorAdapter doctorAdapter;

    public List<Doctor> getProperDoctors(List<SpecialityName> selectedButtons) {
        List<Doctor> result = new LinkedList<>();
        result = doctorAdapter.getDoctorsBySpeciality(selectedButtons.get(0).name());
        return result;
    }
}
