package ehu.das.fotoslite;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import ehu.das.fotoslite.services.AlarmManagerBroadcastRecevier;

/**
 * Actividad principal.
 */

public class MainActivity extends AppCompatActivity {

    // Lista con las uris de las fotos del dispositivo
    public static ArrayList<Uri> misFotos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (misFotos.size() == 0) {
            // Se cargan las fotos del dispositivo en segundo plano para no ralentizar el dispositivo
            Runnable cargarImagenes = new Thread() {
                @Override
                public void run() {
                    getImagenes();
                    Log.i("tamano", String.valueOf(misFotos.size()));
                }
            };
            cargarImagenes.run();
        }
        // Se registra el broadcast receiver
        IntentFilter filter = new IntentFilter("android.permission.MAIN");
        AlarmManagerBroadcastRecevier receiver = new AlarmManagerBroadcastRecevier();
        this.registerReceiver(receiver, filter);
        // Para evitar un error
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        // Se piden varios permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED},
                    7);
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    3);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    4);
        }
        // Se registra al usuario en el topico 'all' para poder enviarle así notificaciones
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }


    public void getImagenes() {
        // Utiliza un content provider para obtener los paths y las fotos del dispositivo
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.RELATIVE_PATH, MediaStore.Images.Media.DISPLAY_NAME}, null, null, null);
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            String name = cursor.getString(1);
            File f;
            // Se crea el path
            if (path.lastIndexOf("/") == path.length() - 1) {
                f = new File(Environment.getExternalStorageDirectory() + "/" + path  + name);
            } else {
                f = new File(Environment.getExternalStorageDirectory() + "/" + path  + "/" + name);
            }
            // Se comprueba que el path esté bien creado
            if (f.exists()) {
                misFotos.add(Uri.fromFile(f));
            }
        }
        cursor.close();
    }
}