package bsu.rpact.medionefrontend.vaadin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class ListContentPanel extends VerticalLayout {
    private VerticalLayout content;

    public ListContentPanel(){
        super();
        preparePanel();
    }

    public ListContentPanel(Component... children){
        super();
        preparePanel();
        this.add(children);
    }

    private void preparePanel() {
        content = new VerticalLayout();
        content.getStyle().set("display", "block");
        content.getStyle().set("overflow", "hidden");
        getStyle().set("overflow", "hidden");
        content.setWidthFull();
        super.add(content);
        setHeightFull();
        setWidthFull();
    }

    public VerticalLayout getContent(){
        return content;
    }

    @Override
    public void add(Component... components){
        content.add(components);
    }

    public void addAsList(List<Component> components){
        components.stream().forEach(component -> content.add(component));
    }

    @Override
    public void remove(Component... components){
        content.remove(components);
    }

    public void removeAsList(List<Component> components) {
        components.stream().forEach(card -> content.remove(card));
    }
}