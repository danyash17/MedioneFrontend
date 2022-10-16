package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import bsu.rpact.medionefrontend.vaadin.components.ListContentPanel;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.IconItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.LinkedList;
import java.util.List;

@Route(value = "documents", layout = MainLayout.class)
@PageTitle("Documents")
public class DocumentView extends VerticalLayout {

    private final ListContentPanel listContentPanel;
    private final TextField searchField;
    private final List cardList;
    private HorizontalLayout pagingLayout;
    private Integer itemsPerPage = 5;
    private Integer currentPage = 1;
    private Integer totalPages;
    private Label currentNumber = new Label();

    public DocumentView() {
        setDefaultHorizontalComponentAlignment(Alignment.START);
        listContentPanel = new ListContentPanel();
        searchField = new TextField();
        cardList = getAllCards();
        totalPages = cardList.size() / itemsPerPage;
        pagingLayout = setupPagingLayout();
        listContentPanel.add(searchField);
        populateCurrentCards(listContentPanel, cardList);
        listContentPanel.add(pagingLayout);
        add(listContentPanel);

    }

    private void populateCurrentCards(ListContentPanel listContentPanel, List cardList) {
        listContentPanel.removeAsList(cardList);
        listContentPanel.remove(pagingLayout);
        Integer currentIndex = itemsPerPage * currentPage;
        boolean lastPage = currentPage == totalPages;
        listContentPanel.addAsList(cardList.subList(currentIndex,
                lastPage ? cardList.size() -1 : currentIndex + itemsPerPage));
        listContentPanel.add(pagingLayout);
        if (!lastPage){
            pagingLayout.setHeightFull();
            pagingLayout.setAlignItems(Alignment.CENTER);
        }
        else {
            pagingLayout.setHeight("308px");
            pagingLayout.setAlignItems(Alignment.END);
        }
        currentNumber.setText(currentPage.toString());
    }

    private List getAllCards() {
        List cardList = new LinkedList();
        for (int i=0; i<23; i++) {
            Image img = new Image("icons/document.png", "Some Document");
            img.setWidth("40px");
            img.setHeight("40px");
            RippleClickableCard card = new RippleClickableCard(
                    componentEvent -> Notification.show("A RippleClickableCard was clicked!"),
                    new IconItem(img, "Some Document", "Description")
            );
            card.setWidthFull();
            card.setHeight("100px");
            cardList.add(card);
        }
        return cardList;
    }

    private HorizontalLayout setupPagingLayout() {
        HorizontalLayout pagingLayout = new HorizontalLayout();
        pagingLayout.setAlignItems(Alignment.CENTER);
        pagingLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Button buttonLeft = new Button(new Icon(VaadinIcon.ANGLE_LEFT));
        buttonLeft.addClickListener(e -> {
           if(currentPage>1){
               currentPage--;
               populateCurrentCards(listContentPanel, cardList);
           }
        });
        Button buttonDoubleLeft = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        buttonDoubleLeft.addClickListener(e -> {
            if(currentPage>1){
                currentPage=1;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Button buttonRight = new Button(new Icon(VaadinIcon.ANGLE_RIGHT));
        buttonRight.addClickListener(e -> {
            if(currentPage<totalPages){
                currentPage++;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Button buttonDoubleRight = new Button(new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        buttonDoubleRight.addClickListener(e -> {
            if(currentPage<totalPages){
                currentPage = totalPages;
                populateCurrentCards(listContentPanel, cardList);
            }
        });
        Label page = new Label("Page ");
        currentNumber.setText(currentPage.toString());
        Label of = new Label("of");
        Label totalNumber = new Label(totalPages.toString());
        HorizontalLayout labelLayout = new HorizontalLayout(page, currentNumber, of, totalNumber);
        labelLayout.setAlignItems(Alignment.CENTER);
        labelLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        labelLayout.setHeight("14.5%");
        pagingLayout.add(buttonDoubleLeft,buttonLeft,labelLayout, buttonRight,buttonDoubleRight);
        return pagingLayout;
    }
}