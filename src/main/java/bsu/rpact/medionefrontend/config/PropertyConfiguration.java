package bsu.rpact.medionefrontend.config;

import bsu.rpact.medionefrontend.vaadin.i18n.I18nProvider;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;
import java.util.Locale;

@Configuration
public class PropertyConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(20 * 1024 * 1024));
        factory.setMaxRequestSize(DataSize.ofBytes(20 * 1024 * 1024));
        return factory.createMultipartConfig();
    }
    @Bean
    public I18NProvider i18NProvider(){
        Locale.setDefault(I18nProvider.ENGLISH);
        return new I18nProvider();
    }
}
