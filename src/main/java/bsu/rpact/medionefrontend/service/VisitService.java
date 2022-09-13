package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.VisitAdapter;
import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.enums.Role;
import bsu.rpact.medionefrontend.pojo.DoctorVisitPojo;
import bsu.rpact.medionefrontend.pojo.PatientVisitPojo;
import bsu.rpact.medionefrontend.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private List<Visit> getAllVisitsOfPatientSelf() {
        return visitAdapter.getAllOfPatientSelf();
    }

    private List<Visit> getAllVisitsOfDoctorSelf() {
        return visitAdapter.getAllOfDoctorSelf();
    }

    public List<Visit> getAllMyVisitsSelf(){
        if (sessionManager.getRoleAttribute().equals(Role.PATIENT.name())) {
            return getAllVisitsOfPatientSelf();
        }
        else if (sessionManager.getRoleAttribute().equals(Role.DOCTOR.name())) {
            return getAllVisitsOfDoctorSelf();
        }
        return new ArrayList<>();
    }

    public void createVisitBySelf(PatientVisitPojo visitPojo) {
        if (sessionManager.getRoleAttribute().equals(Role.PATIENT.name())) {
            visitAdapter.createByPatientSelf(visitPojo);
        }
    }

    public void update(Visit visit) {
        if (sessionManager.getRoleAttribute().equals(Role.PATIENT.name())) {
            PatientVisitPojo patientVisitPojo = new PatientVisitPojo();
            patientVisitPojo.setReason(visit.getReason());
            patientVisitPojo.setDatetime(visit.getDatetime());
            patientVisitPojo.setDiagnosis(visit.getDiagnosis());
            patientVisitPojo.setComments(visit.getComments());
            patientVisitPojo.setActive(visit.getActive());
            patientVisitPojo.setDoctorId(visit.getDoctor().getId());
            visitAdapter.updateByPatientSelf(patientVisitPojo,visit.getId());
        }
        if (sessionManager.getRoleAttribute().equals(Role.DOCTOR.name())) {
            DoctorVisitPojo doctorVisitPojo = new DoctorVisitPojo();
            doctorVisitPojo.setActive(visit.getActive());
            doctorVisitPojo.setDatetime(visit.getDatetime());
            doctorVisitPojo.setComments(visit.getComments());
            doctorVisitPojo.setDiagnosis(visit.getDiagnosis());
            doctorVisitPojo.setReason(visit.getReason());
            doctorVisitPojo.setPatientId(visit.getPatient().getId());
            visitAdapter.updateByDoctorSelf(doctorVisitPojo, visit.getId());
        }
    }

    public void createVisitScheduleBySelf() {
        if (sessionManager.getRoleAttribute().equals(Role.PATIENT.name())) {
            createVisitScheduleBySelfPatient();
        }
        else if (sessionManager.getRoleAttribute().equals(Role.DOCTOR.name())) {
            createVisitScheduleBySelfDoctor();
        }
    }

    private void createVisitScheduleBySelfDoctor() {
        visitAdapter.createScheduleByDoctorSelf();
    }

    private void createVisitScheduleBySelfPatient() {
        visitAdapter.createScheduleByPatientSelf();
    }
}
