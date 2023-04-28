package bsu.rpact.medionefrontend.utils.pdf;

import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.service.DoctorService;
import bsu.rpact.medionefrontend.vaadin.components.MedicationDetails;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Locale;

public class MedicalForm01Mapper {

    private static final String TEMPLATE_URL = "../../../../../META-INF/resources/template/MedicalForm01.pdf";

    public void map(MedicationPrescriptionRq rq){
        // Load the template PDF file
        PDDocument pdfTemplate = null;
        try {
            pdfTemplate = PDDocument.load(new File(MedicalForm01Mapper.class.getResource(TEMPLATE_URL).toURI()));
            PDAcroForm acroForm = pdfTemplate.getDocumentCatalog().getAcroForm();
            acroForm.setNeedAppearances(true);
            acroForm.getField("code").setValue("000");
            acroForm.getField("dateFrom").setValue(String.valueOf(LocalDate.now().getDayOfMonth()));
            acroForm.getField("stringFrom").setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
            acroForm.getField("dateTo").setValue(String.valueOf(LocalDate.now().getDayOfMonth()));
            acroForm.getField("stringTo").setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
            acroForm.getField("patientFio").setValue(rq.getPatient().getCredentials().getFirstName() + " " +
                    rq.getPatient().getCredentials().getLastName() + " " + rq.getPatient().getCredentials().getPatronymic());
            acroForm.getField("doctorFio").setValue(rq.getDoctor().getCredentials().getFirstName() + " " +
                    rq.getDoctor().getCredentials().getLastName() + " " + rq.getDoctor().getCredentials().getPatronymic());
            acroForm.getField("birthDate").setValue(new SimpleDateFormat("dd.MM.yyyy").format(rq.getPatient().getCredentials().getBirthDate()));
            if(rq.getMedicationDetails().get(0)!=null){
                acroForm.getField("rp1").setValue(getMedicationRpString(rq.getMedicationDetails().get(0)));
            }
            if(rq.getMedicationDetails().size()==2 && rq.getMedicationDetails().get(1)!=null){
                acroForm.getField("rp2").setValue(getMedicationRpString(rq.getMedicationDetails().get(1)));
            }
            if(rq.getMedicationDetails().size()==3 && rq.getMedicationDetails().get(2)!=null){
                acroForm.getField("rp3").setValue(getMedicationRpString(rq.getMedicationDetails().get(2)));
            }
            acroForm.getField("validityField").setValue(rq.getValidity().toString());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Save the PDF document with the populated fields
        try {
            pdfTemplate.save(new File("output.pdf"));
            pdfTemplate.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMedicationRpString(MedicationDetails medicationDetails) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(medicationDetails.getRegistryMedication().getTradeName());
        stringBuilder.append(" ");
        stringBuilder.append(medicationDetails.getRegistryMedication().getInternationalName());
        stringBuilder.append("; ");
        stringBuilder.append("Производитель:");
        stringBuilder.append(medicationDetails.getRegistryMedication().getManufacturer());
        stringBuilder.append("; ");
        stringBuilder.append("Формат распространения препарата:");
        stringBuilder.append(medicationDetails.getMedicationForm().getDisplay());
        stringBuilder.append("; ");
        stringBuilder.append("Дозировка: ");
        if(!medicationDetails.getOnceDosageMethod().isEmpty()) {
            stringBuilder.append(medicationDetails.getOnceDosageMethod().toString());
        }
        if(!medicationDetails.getOnDemandDosageMethod().isEmpty()) {
            stringBuilder.append(medicationDetails.getOnDemandDosageMethod().toString());
        }
        if(!medicationDetails.getPeriodicalDosageMethod().isEmpty()) {
            stringBuilder.append(medicationDetails.getPeriodicalDosageMethod().toString());
        }
        if(!medicationDetails.getTetrationDosageMethod().isEmpty()) {
            stringBuilder.append(medicationDetails.getTetrationDosageMethod().toString());
        }
        stringBuilder.append("; ");
        if (medicationDetails.getComment()!=null){
            stringBuilder.append("Комментарий:");
            stringBuilder.append(medicationDetails.getComment());
        }
        return stringBuilder.toString();
    }

}
