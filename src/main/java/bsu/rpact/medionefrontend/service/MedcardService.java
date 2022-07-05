package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.MedcardAdapter;
import bsu.rpact.medionefrontend.entity.Medcard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedcardService {

    @Autowired
    private MedcardAdapter adapter;

    public Medcard getSelf(){
        return adapter.getSelf();
    }
}
