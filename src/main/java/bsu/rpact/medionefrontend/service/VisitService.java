package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.VisitAdapter;
import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.enums.Role;
import bsu.rpact.medionefrontend.pojo.PatientVisitPojo;
import bsu.rpact.medionefrontend.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    @Autowired
    private VisitAdapter visitAdapter;
    @Autowired
    private SessionManager sessionManager;

    public List<Visit> getAllVisitsOfDoctor(Integer id) {
        return visitAdapter.getAllOfDoctor(id);
    }

    public void createVisitBySelf(PatientVisitPojo visitPojo) {
        if (sessionManager.getRoleAttribute().equals(Role.PATIENT.name())) {
            visitAdapter.createByPatientSelf(visitPojo);
        }
    }
}
