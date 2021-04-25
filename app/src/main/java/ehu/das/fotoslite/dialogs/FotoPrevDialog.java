package ehu.das.fotoslite.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ehu.das.fotoslite.R;
import ehu.das.fotoslite.fragments.LoginFragment;
import ehu.das.fotoslite.fragments.MenuFragment;
import ehu.das.fotoslite.services.ConnectionDBServer;

/**
 * Esta clase es un dialog que muestra un previsualización de la foto capturada con la cámara y te pregunta si la deseas subir al servidor o no.
 */

public class FotoPrevDialog extends DialogFragment {


    public FotoPrevDialog() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Creamos los botones de Si y No
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.foto_prev, null);
        alert.setCancelable(false);
        alert.setView(contentView);
        ImageView iv = contentView.findViewById(R.id.fotoPrev);
        // La foto está en la uri de MenuFragment, se ha hecho así porque para que al girar la pantalla no
        // falle, dado que el constructor tiene que estar vacío
        iv.setImageURI(MenuFragment.uri);
        alert.setTitle("¿Subir esta foto?");
        alert.setPositiveButton("Subir", (dialog, which) -> {
            upload();
        });
        alert.setNegativeButton("Cancelar", (dialog, which) -> dismiss());
        return alert.create();
    }

    public void upload() {
        // Sube la foto al servidor de firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Le pone de nombre la hora actual
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + ".jpg";
        // Firebase no permite hacer consultas de las fotos que tiene el usuario
        // por eso en una tabla sql se guarda las fotos asociadas y sus nombres
        upload_database(nombrefich);
        // Las fotos del usuario se guardan en 'nombreUsuario/nombreFoto'
        StorageReference spaceRef = storageRef.child(LoginFragment.usuario + "/" + nombrefich);
        spaceRef.putFile(MenuFragment.uri);
    }

    public void upload_database(String nombreFich) {
        FusedLocationProviderClient cliente = LocationServices.getFusedLocationProviderClient(getActivity());
        // Cogemos la ubicación antes de subir la foto
        cliente.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // En cuanto se tenga la ubicación se añaden los metadatos de la foto al servidor
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();
                    String parametros = "username=" + LoginFragment.usuario + "&image_name=" + nombreFich + "&latitude=" + latitud + "&longitude=" + longitud;
                    Data datos = new Data.Builder()
                            .putString("parametros",parametros)
                            .putString("fichero", "add_image")
                            .putString("method", "GET")
                            .build();
                    OneTimeWorkRequest trabajoPuntual =
                            new OneTimeWorkRequest.Builder(ConnectionDBServer.class).setInputData(datos).build();
                    WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);
                } else {
                    Toast.makeText(getContext(), "No se ha podido obtener la localización", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
