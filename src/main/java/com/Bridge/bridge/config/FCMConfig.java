package com.Bridge.bridge.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FCMConfig {

    private final ClassPathResource firebaseResource = new ClassPathResource("firebase/bridge-950ec-firebase-adminsdk-o692d-8249202110.json");

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                        firebaseResource.getInputStream()))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        else {
            return FirebaseApp.getApps().get(0);
        }
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        return FirebaseMessaging.getInstance(firebaseApp());
    }
}
