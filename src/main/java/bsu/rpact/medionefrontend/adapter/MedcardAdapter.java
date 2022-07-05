package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Medcard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MedcardAdapter extends GeneralAdapter {

    @Value("${mappings.patient}")
    protected String patientMapping;

    public Medcard getSelf() {
        return webClient.get()
                .uri(patientMapping + "/self/medcard")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Medcard.class)
                .block();
    }
}
