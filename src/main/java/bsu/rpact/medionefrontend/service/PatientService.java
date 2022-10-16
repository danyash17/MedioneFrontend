package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.PatientAdapter;
import bsu.rpact.medionefrontend.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientAdapter adapter;

    public Optional<Patient> getSelf() {
        return adapter.getSelf();
    }
}
