package bsu.rpact.medionefrontend.utils;

import bsu.rpact.medionefrontend.pojo.authentication.RegisterRequest;
import bsu.rpact.medionefrontend.vaadin.view.HomeView;
import bsu.rpact.medionefrontend.vaadin.view.LoginView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UiUtils {

    @Value("${mappings.preambule}")
    private String webPreambule;
    @Value("${mappings.auth}")
    private String loginMapping;

    public Notification generateErrorNotification(String errorText){
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        Div text = new Div(new Text(errorText));
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });
        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        notification.add(layout);
        notification.setDuration(6000);
        return notification;
    }

    public Notification generateSuccessRegistrationNotification(RegisterRequest userBean) {
        Notification notification = new  Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setDuration(6000);

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        Div statusText = new Div(new Text("Account created, welcome " + userBean.getFirstName()));

        Button returnButton = new Button();
        returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        returnButton.getElement().getStyle().set("margin-left", "var(--lumo-space-xl)");
        returnButton.getElement().executeJs("this.textContent = `Return to login`;");
        returnButton.addClickListener(event -> {
            notification.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });

        HorizontalLayout layout = new HorizontalLayout(statusText, returnButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        return notification;
    }

}
