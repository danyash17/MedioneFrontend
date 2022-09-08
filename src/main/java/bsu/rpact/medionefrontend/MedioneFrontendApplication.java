package bsu.rpact.medionefrontend;

import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MedioneFrontendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MedioneFrontendApplication.class, args);
    }

}
