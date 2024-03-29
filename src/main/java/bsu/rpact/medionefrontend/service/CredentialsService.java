package bsu.rpact.medionefrontend.service;

import bsu.rpact.medionefrontend.adapter.demographic.CredentialsAdapter;
import bsu.rpact.medionefrontend.entity.Credentials;
import bsu.rpact.medionefrontend.pojo.CredentialsNoIdPojo;
import bsu.rpact.medionefrontend.pojo.authentication.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialsService{

    @Autowired
    private CredentialsAdapter adapter;


    public MessageResponse update(Credentials credentials, boolean self) {
        CredentialsNoIdPojo pojo = new CredentialsNoIdPojo(
                credentials.getLogin(),
                credentials.getPassword(),
                credentials.getFirstName(),
                credentials.getLastName(),
                credentials.getPatronymic(),
                credentials.isEnabled2Fa(),
                credentials.getPhone(),
                credentials.getRole(),
                credentials.getBirthDate()
        );
        return self ? adapter.updateSelf(pojo) : adapter.update(pojo,credentials.getId());
    }

    public Credentials get(Integer id) {
        return adapter.get(id);
    }

    public Credentials getSelf() {
        return adapter.getSelf();
    }
}
