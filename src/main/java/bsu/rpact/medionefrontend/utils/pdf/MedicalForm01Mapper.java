package bsu.rpact.medionefrontend.utils.pdf;

import bsu.rpact.medionefrontend.pojo.request.MedicationPrescriptionRq;
import bsu.rpact.medionefrontend.vaadin.components.MedicationDetails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class MedicalForm01Mapper {

    private static final String TEMPLATE_URL = "../../../../../META-INF/resources/template/MedicalForm01.pdf";
    private static final String FONT_URL = "src/main/resources/META-INF/resources/template/roboto-regular.ttf";
    private static final File FILE = new File("src/main/resources/generatedPrescription.pdf");

    public File map(MedicationPrescriptionRq rq){
        PDDocument pdfTemplate = null;
        try {
            pdfTemplate = PDDocument.load(new File(MedicalForm01Mapper.class.getResource(TEMPLATE_URL).toURI()));
            PDAcroForm acroForm = pdfTemplate.getDocumentCatalog().getAcroForm();

            PDFont formFont = PDType0Font.load(pdfTemplate, new FileInputStream(FONT_URL), false);
            PDResources res = acroForm.getDefaultResources();
            String fontName = res.add(formFont).getName();
            String defaultAppearanceString = "/" + fontName + " 9 Tf 0 g";
            for (PDField field : acroForm.getFields()) {
                if (field instanceof PDTextField) {
                    PDTextField textField = (PDTextField) field;
                    textField.setDefaultAppearance(defaultAppearanceString);
                }
            }
            acroForm.setNeedAppearances(true);
            Locale russianLocale = new Locale("ru", "RU");
            acroForm.getField("code").setValue("000");
            acroForm.getField("dateFrom").setValue(String.valueOf(LocalDate.now().getDayOfMonth()));
            acroForm.getField("stringFrom").setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, russianLocale));
            acroForm.getField("dateTo").setValue(String.valueOf(rq.getActiveAfter().getDayOfMonth()));
            acroForm.getField("stringTo").setValue(rq.getActiveAfter().getMonth().getDisplayName(TextStyle.FULL, russianLocale));
            acroForm.getField("patientFio").setValue(rq.getPatient().getCredentials().getFirstName() + " " +
                    rq.getPatient().getCredentials().getLastName() + " " + rq.getPatient().getCredentials().getPatronymic());
            acroForm.getField("doctorFio").setValue(rq.getDoctor().getCredentials().getFirstName() + " " +
                    rq.getDoctor().getCredentials().getLastName() + " " + rq.getDoctor().getCredentials().getPatronymic());
            acroForm.getField("birthDate").setValue(new SimpleDateFormat("dd.MM.yyyy").format(rq.getPatient().getCredentials().getBirthDate()));
            if(rq.getMedicationDetails().get(0)!=null){
                acroForm.getField("rp1").setValue(getMedicationRpString(rq.getMedicationDetails().get(0)));
            }
            if(rq.getMedicationDetails().get(1)!=null){
                acroForm.getField("rp2").setValue(getMedicationRpString(rq.getMedicationDetails().get(1)));
            }
            if(rq.getMedicationDetails().get(2)!=null){
                acroForm.getField("rp3").setValue(getMedicationRpString(rq.getMedicationDetails().get(2)));
            }
            acroForm.getField("validityField").setValue(rq.getValidity().toString());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            pdfTemplate.save(FILE);
            pdfTemplate.close();
            new PdfFieldToLabelConverter().convertPdfFormToText(FILE,FILE);
            return FILE;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMedicationRpString(MedicationDetails medicationDetails) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(removeInstructions(medicationDetails.getRegistryMedication().getTradeName()));
        stringBuilder.append("; ");
        stringBuilder.append("Производитель:");
        stringBuilder.append(medicationDetails.getRegistryMedication().getManufacturer());
        stringBuilder.append("; ");
        stringBuilder.append("Формат распространения препарата:");
        stringBuilder.append(medicationDetails.getMedicationForm().getDisplay());
        stringBuilder.append("; ");
        if(!medicationDetails.getOnceDosageMethod().isEmpty()) {
            stringBuilder.append("Единоразовая дозировка: ");
            stringBuilder.append(medicationDetails.getOnceDosageMethod().toString());
            stringBuilder.append(".");
        }
        if(!medicationDetails.getOnDemandDosageMethod().isEmpty()) {
            stringBuilder.append("Дозировка по востребованию: ");
            stringBuilder.append(medicationDetails.getOnDemandDosageMethod().toString());
            stringBuilder.append(".");
        }
        if(!medicationDetails.getPeriodicalDosageMethod().isEmpty()) {
            stringBuilder.append("Периодическая дозировка: ");
            stringBuilder.append(medicationDetails.getPeriodicalDosageMethod().toString());
            stringBuilder.append(".");
        }
        if(!medicationDetails.getTetrationDosageMethod().isEmpty()) {
            stringBuilder.append("Дозировка по тетрационному методу: ");
            stringBuilder.append(medicationDetails.getTetrationDosageMethod().toString());
            stringBuilder.append(".");
        }
        if (medicationDetails.getComment()!=null && !medicationDetails.getComment().isEmpty()){
            stringBuilder.append("Комментарий:");
            stringBuilder.append(medicationDetails.getComment());
        }
        return stringBuilder.toString();
    }

    private String removeInstructions(String input) {
        String keyword = "инструкция";
        int index = input.indexOf(keyword);
        if (index != -1) {
            return input.substring(0, index);
        } else {
            return input;
        }
    }

}
