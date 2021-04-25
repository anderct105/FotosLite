package ehu.das.fotoslite.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

/**
 * Clase para gestionar el servicio Firebase.
 */

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {

    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getNotification() != null) {
        }
    }
    
    public String getToken() throws IOException {
        return FirebaseInstanceId.getInstance().getToken();
    }

}
