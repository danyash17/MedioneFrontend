package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.SpecialityAdapter;
import bsu.rpact.medionefrontend.entity.DoctorSpeciality;
import bsu.rpact.medionefrontend.entity.Speciality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialityService {

    @Autowired
    private SpecialityAdapter adapter;

    public List<Speciality> getAllSpecialities() {
        return adapter.getAll();
    }

}
