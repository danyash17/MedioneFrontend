package bsu.rpact.medionefrontend.adapter.demographic;

import bsu.rpact.medionefrontend.entity.Credentials;
import bsu.rpact.medionefrontend.pojo.CredentialsNoIdPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CredentialsAdapter extends DemographicBaseAdapter {

    @Value("${mappings.credentials}")
    private String credentialsMapping;

    public MessageResponse update(CredentialsNoIdPojo pojo, Integer id) {
        return webClient.put()
                .uri(credentialsMapping+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(pojo), CredentialsNoIdPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }

    public MessageResponse updateSelf(CredentialsNoIdPojo pojo) {
        return webClient.put()
                .uri(credentialsMapping)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(pojo), CredentialsNoIdPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }

    public Credentials get(Integer id) {
        return webClient.get()
                .uri(credentialsMapping)
                .retrieve()
                .bodyToMono(Credentials.class)
                .block();
    }

    public Credentials getSelf() {
        return webClient.get()
                .uri(credentialsMapping+"/self")
                .retrieve()
                .bodyToMono(Credentials.class)
                .block();
    }
}
