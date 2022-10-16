package bsu.rpact.medionefrontend.adapter.demographic;

import bsu.rpact.medionefrontend.pojo.DoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.RepresentativeDoctorSpecialityPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class DoctorSpecialityAdapter extends DemographicBaseAdapter {

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

    public MessageResponse saveSelf(DoctorSpecialityPojo doctorSpecialityPojo) {
        return webClient.post()
                .uri(doctorSpecialityMapping + "/self/specialities")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(doctorSpecialityPojo), DoctorSpecialityPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }

    public MessageResponse updateSelf(DoctorSpecialityPojo doctorSpecialityPojo) {
        return webClient.put()
                .uri(doctorSpecialityMapping + "/self/specialities")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(doctorSpecialityPojo), DoctorSpecialityPojo.class)
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }

    public MessageResponse deleteSelf(DoctorSpecialityPojo doctorSpecialityPojo) {
        return webClient.delete()
                .uri(doctorSpecialityMapping + "/self/specialities/" + doctorSpecialityPojo.getDescription())
                .retrieve()
                .bodyToMono(MessageResponse.class)
                .block();
    }
}
