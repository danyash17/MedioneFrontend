package bsu.rpact.medionefrontend.adapter.medical;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FhirBaseAdapter {

    @Value("${fhir.serverbase}")
    private String server;
    private FhirContext context;
    private IGenericClient client;

    @PostConstruct
    public void initAdapter() {
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient(server);
    }

    public IGenericClient getClient() {
        return client;
    }

    public FhirContext getContext(){return context;}
}
