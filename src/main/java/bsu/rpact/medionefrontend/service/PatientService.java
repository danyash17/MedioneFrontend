package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.PatientAdapter;
import bsu.rpact.medionefrontend.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientAdapter adapter;

    public Patient getSelf() {
        return null;
    }
}
