package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.DoctorAdapter;
import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.enums.SpecialityName;
import bsu.rpact.medionefrontend.pojo.DoctorPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DoctorAdapter doctorAdapter;

    public List<Doctor> getProperDoctors(List<SpecialityName> specialityNames) {
        List<Doctor> result = new LinkedList<>();
        if (!specialityNames.isEmpty()) {
            result = doctorAdapter.getDoctorsBySpeciality(specialityNames.get(0).name());
        }
        for (final Iterator<Doctor> iter = result.listIterator();iter.hasNext();){
            Doctor doctor = iter.next();
            boolean hasCompetence = specialityNames.stream().allMatch(specialityName -> {
                return doctor.getSpecialityList().stream().
                        anyMatch(doctorSpeciality -> doctorSpeciality.getDescription().equals(specialityName.name()));
            });
            if (!hasCompetence){
                iter.remove();
            }
        }
        return result;
    }

    public Doctor getDoctorSelf() {
        return doctorAdapter.getSelf();
    }

    public MessageResponse updateSelf(Doctor doctor) {
        DoctorPojo doctorPojo = new DoctorPojo();
        doctorPojo.setAvailable(doctor.getAvailable());
        doctorPojo.setCommonInfo(doctor.getCommonInfo());
        doctorPojo.setHospital(doctor.getHospital());
        doctorPojo.setPhoto(doctor.getDoctorPhoto());
        return doctorAdapter.updateSelf(doctorPojo);
    }
}
