package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Medcard;
import bsu.rpact.medionefrontend.pojo.MedcardPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class MedcardAdapter extends GeneralAdapter {

    @Value("${mappings.patient}")
    protected String patientMapping;
    @Value("${mappings.patient.self.medcard}")
    protected String selfMedcardMapping;

    public Optional<Medcard> getSelf() {
        return Optional.ofNullable(webClient.get()
                .uri(selfMedcardMapping)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Medcard.class)
                .block());
    }

    public MessageResponse createSelf(MedcardPojo medcardPojo) {
        return webClient.post()
                .uri(selfMedcardMapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(medcardPojo), MedcardPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
