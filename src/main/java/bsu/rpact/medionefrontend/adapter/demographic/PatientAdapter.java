package bsu.rpact.medionefrontend.adapter.demographic;

import bsu.rpact.medionefrontend.entity.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PatientAdapter extends DemographicBaseAdapter {

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

    public List<Patient> getAll() {
        return webClient.get()
                .uri(mapping + "all")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Patient>>() {
                })
                .block();
    }

    public List<Patient> findBySearchTerm(String searchTerm) {
        return webClient.get()
                .uri(mapping + "search/" + searchTerm)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Patient>>() {
                })
                .block();
    }

    public Optional<Patient> getById(Integer id) {
        return Optional.ofNullable(webClient.get()
                .uri(mapping+id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Patient.class)
                .block());
    }
}
