package bsu.rpact.medionefrontend.utils.pdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfFieldToLabelConverter {

    public void convertPdfFormToText(File inputFile, File outputFile) throws IOException {
        try (PDDocument pdfDocument = PDDocument.load(inputFile)) {
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                acroForm.refreshAppearances();
                acroForm.flatten();
            }
            pdfDocument.save(outputFile);
        }
    }

}
