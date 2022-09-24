package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Doctor;
import bsu.rpact.medionefrontend.pojo.DoctorPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DoctorAdapter extends GeneralAdapter {

    @Value("${mappings.doctors}")
    private String doctorMapping;

    public List<Doctor> getDoctorsBySpeciality(String speciality) {
        return webClient.get()
                .uri(doctorMapping + "/search/" + speciality)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Doctor>>() {
                })
                .block();
    }

    public Doctor getSelf() {
        return webClient.get()
                .uri(doctorMapping + "/self")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Doctor.class)
                .block();
    }

    public MessageResponse updateSelf(DoctorPojo pojo) {
        return webClient.put()
                .uri(doctorMapping + "/self")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(pojo), DoctorPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
