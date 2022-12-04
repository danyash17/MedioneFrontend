package bsu.rpact.medionefrontend.adapter.medical;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
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

    protected String getIncludeParameterAsString(Class type, ReferenceClientParam... vararg) {
        StringBuilder builder = new StringBuilder();
        builder.append(type.getSimpleName() + ":");
        for (int i = 0; i < vararg.length; i++) {
            builder.append(vararg[i].getParamName());
            if (i + 1 != vararg.length) {
                builder.append("&");
            }
        }
        return builder.toString();
    }

    public IGenericClient getClient() {
        return client;
    }

    public FhirContext getContext(){return context;}
}
