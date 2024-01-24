package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.DoctorSpecialityAdapter;
import bsu.rpact.medionefrontend.pojo.DoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorSpecialityService{

    @Autowired
    private DoctorSpecialityAdapter adapter;


    public List<RepresentativeDoctorSpecialityPojo> getDoctorSpecialities(Integer id) {
        return adapter.getSpecialities(id);
    }

    public MessageResponse saveDoctorSpecialitySelf(RepresentativeDoctorSpecialityPojo pojo) {
        DoctorSpecialityPojo doctorSpecialityPojo = new DoctorSpecialityPojo();
        doctorSpecialityPojo.setDescription(pojo.getSpeciality());
        doctorSpecialityPojo.setInstitute(pojo.getInstitute());
        doctorSpecialityPojo.setExperience(pojo.getExperience());
        return adapter.saveSelf(doctorSpecialityPojo);
    }

    public MessageResponse updateDoctorSpecialitySelf(RepresentativeDoctorSpecialityPojo pojo) {
        DoctorSpecialityPojo doctorSpecialityPojo = new DoctorSpecialityPojo();
        doctorSpecialityPojo.setDescription(pojo.getSpeciality());
        doctorSpecialityPojo.setInstitute(pojo.getInstitute());
        doctorSpecialityPojo.setExperience(pojo.getExperience());
        return adapter.updateSelf(doctorSpecialityPojo);
    }

    public MessageResponse deleteSelf(RepresentativeDoctorSpecialityPojo pojo) {
        DoctorSpecialityPojo doctorSpecialityPojo = new DoctorSpecialityPojo();
        doctorSpecialityPojo.setDescription(pojo.getSpeciality());
        doctorSpecialityPojo.setInstitute(pojo.getInstitute());
        doctorSpecialityPojo.setExperience(pojo.getExperience());
        return adapter.deleteSelf(doctorSpecialityPojo);
    }
}
