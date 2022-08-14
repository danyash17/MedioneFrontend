package bsu.rpact.medionefrontend.adapter;

import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorSpecialityAdapter extends GeneralAdapter {

    @Value("${mappings.doctors.speciality}")
    protected String doctorSpecialityMapping;

    public List<RepresentativeDoctorSpecialityPojo> getSpecialities(Integer id) {
        return webClient.get()
                .uri(doctorSpecialityMapping+ "/" + id + "/specialities/all/represent")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RepresentativeDoctorSpecialityPojo>>() {
                })
                .block();
    }
}
