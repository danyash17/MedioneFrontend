package bsu.rpact.medionefrontend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class VersionUtils {

    @Value("${app.version}")
    private String releaseState;

    public String getCurrentVersion() throws InterruptedException, IOException {
        Process process = Runtime.getRuntime().exec( "git rev-list --count HEAD" );
        process.waitFor();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader( process.getInputStream() ) );

        return releaseState + " v1." + reader.readLine();
    }

}
