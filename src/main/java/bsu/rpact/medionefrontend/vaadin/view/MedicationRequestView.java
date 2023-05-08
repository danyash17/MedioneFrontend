package bsu.rpact.medionefrontend.vaadin.view;

import bsu.rpact.medionefrontend.session.FhirCashingContainer;
import bsu.rpact.medionefrontend.utils.mapper.MedicationPrescriptionRqMapper;
import bsu.rpact.medionefrontend.utils.pdf.MedicalForm01Mapper;
import bsu.rpact.medionefrontend.vaadin.components.MainLayout;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Route(value = "medicationRequest", layout = MainLayout.class)
@PageTitle("Medication Request")
public class MedicationRequestView extends VerticalLayout {

    private final FhirCashingContainer container;
    private final MedicationPrescriptionRqMapper mapper;

    public MedicationRequestView(FhirCashingContainer container, MedicationPrescriptionRqMapper mapper) {
        this.container = container;
        this.mapper = mapper;
        PdfViewer pdfViewer = new PdfViewer();
        pdfViewer.setCustomTitle("Medication Prescription");
        pdfViewer.setAddPrintButton(true);
        pdfViewer.setAddDownloadButton(true);
        try {
            pdfViewer.setSrc(fileToStreamResource(new MedicalForm01Mapper().map(mapper.map(container.getMedicationRequest()))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        add(pdfViewer);
    }

    public StreamResource fileToStreamResource(File file) throws FileNotFoundException {
        StreamResource streamResource = new StreamResource(file.getName(),
                () -> {
                    try {
                        return new FileInputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
        return streamResource;
    }
}
