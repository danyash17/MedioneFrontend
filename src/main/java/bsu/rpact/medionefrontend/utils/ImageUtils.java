package bsu.rpact.medionefrontend.utils;

import com.vaadin.flow.component.html.Image;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ImageUtils {

    public static final String RESOURCES = "D:/Projects/MedioneFrontend/src/main/resources/META-INF/resources/";
    public static final String TARGET_RESOURCES = "D:/Projects/MedioneFrontend/target/classes/META-INF/resources/";
    public static final String PATH = "images/cached/";
    public static final String FORMAT = ".png";

    public String chacheByteArrToImageDoctor(byte[] byteArr, String name) {
        if (byteArr != null) {
            File srcFile = new File(RESOURCES + PATH + name + FORMAT);
            File targetFile = new File(TARGET_RESOURCES + PATH + name + FORMAT);
            try {
                FileUtils.writeByteArrayToFile(srcFile, byteArr);
                FileUtils.writeByteArrayToFile(targetFile, byteArr);
                return PATH + name+ FORMAT;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "images/doctorAvatar.png";
    }

    public String chacheByteArrToImagePatient(byte[] byteArr, String name) {
        if (byteArr != null) {
            File srcFile = new File(RESOURCES + PATH + name + FORMAT);
            File targetFile = new File(TARGET_RESOURCES + PATH + name + FORMAT);
            try {
                FileUtils.writeByteArrayToFile(srcFile, byteArr);
                FileUtils.writeByteArrayToFile(targetFile, byteArr);
                return PATH + name+ FORMAT;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "images/patientAvatar.png";
    }

    public Image getImageByDocumentType(String documentType){
        Image image;
        switch (documentType){
            case "Observation" : {
                image = new Image("icons/documents/observation1.png", "icons/question.png");
                break;
            }
            case "Procedure" : {
                image = new Image("icons/documents/procedure.png", "icons/question.png");
                break;
            }
            case "DiagnosticReport" : {
                image = new Image("icons/documents/report1.png", "icons/question.png");
                break;
            }
            default: {
                image = new Image("icons/documents/question.png", "icons/documents/question.png");
            }
        }
        return image;
    }

}
