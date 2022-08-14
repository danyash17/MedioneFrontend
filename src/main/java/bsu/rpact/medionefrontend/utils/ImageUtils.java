package bsu.rpact.medionefrontend.utils;

import bsu.rpact.medionefrontend.session.SessionManager;
import com.vaadin.flow.component.html.Image;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ImageUtils {

    public static final String RESOURCES = "D:/Projects/MedioneFrontend/src/main/resources/META-INF/resources/";
    public static final String PATH = "images/cached/";
    public static final String FORMAT = ".png";

    public String chacheByteArrToImage(byte[] byteArr, String name) {
        if (byteArr != null) {
            File file = new File(RESOURCES + PATH + name + FORMAT);
            try {
                FileUtils.writeByteArrayToFile(file, byteArr);
                return PATH + name+ FORMAT;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "images/doctorAvatar.png";
    }

}
