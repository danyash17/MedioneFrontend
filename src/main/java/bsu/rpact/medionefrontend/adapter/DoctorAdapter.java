package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Doctor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
}