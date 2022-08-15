package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.VisitAdapter;
import bsu.rpact.medionefrontend.entity.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    @Autowired
    private VisitAdapter visitAdapter;

    public List<Visit> getAllVisitsOfDoctor(Integer id) {
        return visitAdapter.getAllOfDoctor(id);
    }
}
