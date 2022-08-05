package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.entity.Speciality;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecialityAdapter extends GeneralAdapter{

    @Value("${mappings.speciality}")
    private String specialityMapping;

    public List<Speciality> getAll(){
        return webClient.get()
                .uri(specialityMapping+"/all")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Speciality>>() {})
                .block();
    }

}
