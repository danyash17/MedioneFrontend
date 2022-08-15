package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Visit;
import bsu.rpact.medionefrontend.pojo.PatientVisitPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class VisitAdapter extends GeneralAdapter {

    @Value("${mappings.visit}")
    private String mapping;

    public List<Visit> getAllOfDoctor(Integer id) {
        return webClient.get()
                .uri(mapping + "doctor/" + id + "/schedule/all")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Visit>>() {
                })
                .block();
    }

    public MessageResponse createByPatientSelf(PatientVisitPojo visitPojo) {
        return webClient.post()
                .uri(mapping + "patient/self/schedule/add")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(visitPojo), PatientVisitPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
