package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Medcard;
import bsu.rpact.medionefrontend.entity.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientAdapter extends GeneralAdapter{

    @Value("${mappings.patient}")
    private String mapping;

    public Optional<Patient> getSelf() {
        return Optional.ofNullable(webClient.get()
                .uri(mapping+"self")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Patient.class)
                .block());
    }

}
