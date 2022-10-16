package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.MedcardAdapter;
import bsu.rpact.medionefrontend.entity.Medcard;
import bsu.rpact.medionefrontend.pojo.MedcardPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MedcardService {

    @Autowired
    private MedcardAdapter adapter;

    public Optional<Medcard> getSelf(){
        return adapter.getSelf();
    }

    public MessageResponse createSelf(String residentalAddress) {
        MedcardPojo medcardPojo = new MedcardPojo(
                LocalDate.now(),
                LocalDate.now().plusYears(5),
                residentalAddress
        );
        return adapter.createSelf(medcardPojo);
    }
}
