package cn.itsuki.blog.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FirebaseConfig {
    @Value("${firebase.type}")
    private String firebaseType;

    @Value("${firebase.clientId}")
    private String firebaseClientId;

    @Value("${firebase.clientEmail}")
    private String firebaseClientEmail;

    @Value("${firebase.privateKey}")
    private String firebasePrivateKey;

    @Value("${firebase.privateKeyId}")
    private String firebasePrivateKeyId;

    @Value("${firebase.projectId}")
    private String firebaseProjectId;

    private String buildFirebaseOptionsString() {
        return "{"
                + "\"type\": \"" + firebaseType + "\","
                + "\"project_id\": \"" + firebaseProjectId + "\","
                + "\"private_key_id\": \"" + firebasePrivateKeyId + "\","
                + "\"private_key\": \"" + firebasePrivateKey + "\","
                + "\"client_email\": \"" + firebaseClientEmail + "\","
                + "\"client_id\": \"" + firebaseClientId + "\""
                + "}";
    }

    public void initFirebaseApp() {
        String firebaseOptionsString = buildFirebaseOptionsString();
        InputStream serviceAccount = new ByteArrayInputStream(firebaseOptionsString.getBytes());
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.out.println("FirebaseApp 读取：" + e);
            throw new RuntimeException(e);
        }
    }
}
