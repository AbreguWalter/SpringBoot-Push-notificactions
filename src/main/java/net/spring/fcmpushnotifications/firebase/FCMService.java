package net.spring.fcmpushnotifications.firebase;

import com.google.firebase.messaging.*;
import net.spring.fcmpushnotifications.model.PushNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    private Logger logger = LoggerFactory.getLogger(FCMService.class);

    //enviar mensaje
    public void sendMessage(Map<String, String> data, PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithData(data, request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message with data. Topic: " + request.getTopic() + ", " + response);
    }

    //enviar mensaje sin datos
    public void sendMessageWithoutData(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithoutData(request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message without data. Topic: " + request.getTopic() + ", " + response);
    }

    //enviar mensaje simbolito
    public void sendMessageToToken(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageToToken(request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message to token. Device token: " + request.getToken() + ", " + response);
    }

    //enviar y obtener una respuesta
    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    //obtener un mensaje preconfigurado para el simbolo
    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
                .build();
    }

    //obtener un mensaje preconfigurado sin datos
    private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
                .build();
    }

    //obtener un mensaje preconfigurado con datos
    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).putAllData(data).setTopic(request.getTopic())
                .build();
    }

    //obtener un generador de mensajes preconfigurado
    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        /*return Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(
                        new Notification(request.getTitle(), request.getMessage()));*/
        return Message.builder()
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setCategory(request.getTopic()).setThreadId(request.getTopic()).build()).build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(request.getTopic())
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder().setSound(NotificationParameter.SOUND.getValue())
                                .setColor(NotificationParameter.COLOR.getValue()).setTag(request.getTopic()).build()).build())
                .setNotification(
                        new Notification(request.getTitle(), request.getMessage()));
    }

    //Obtener configuracion android
    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound(NotificationParameter.SOUND.getValue())
                        .setColor(NotificationParameter.COLOR.getValue()).setTag(topic).build()).build();
    }

    //Obtener configuracion Ios
    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }









}
