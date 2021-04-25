package ehu.das.fotoslite.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ehu.das.fotoslite.dialogs.FotoPrevDialog;
import ehu.das.fotoslite.R;

import static android.app.Activity.RESULT_OK;

/**
 * En este fragment estarán los botones que lleven a todos los demás.
 */

public class MenuFragment extends Fragment {

    public static Uri uri = null;

    public MenuFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Actions de los botones
        Button upload = getActivity().findViewById(R.id.subir_foto);
        upload.setOnClickListener(v -> {upload();});
        Button ver_fotos = getActivity().findViewById(R.id.ver_fotos);
        ver_fotos.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_menuFragment_to_listaImagenesFragment);});
        Button verMapa = getActivity().findViewById(R.id.botonMapa);
        verMapa.setOnClickListener(v -> {
            Navigation.findNavController(getView()).navigate(R.id.action_menuFragment_to_mapsFragment);
        });
        Button cerrarSesion = getActivity().findViewById(R.id.cerrarSesion);
        cerrarSesion.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_menuFragment_to_loginFragment));
    }

    public void upload() {
        // Utiliza el intent para sacar una foto desde la camara y la guarda donde indica el content procider propio
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        File directorio = getActivity().getFilesDir();
        File fichImg = null;
        Uri uriimagen = null;
        try {
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
            uriimagen = FileProvider.getUriForFile(getActivity(), "ehu.das.entrega2.provider", fichImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Permisos de camara
        boolean permiso = comprobarPermisos();
        if (permiso) {
            Intent elIntentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            elIntentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
            uri = uriimagen;
            startActivityForResult(elIntentFoto, 1);
        }
    }

    public boolean comprobarPermisos() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    1);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Al obtener la foto de la camara se crea un dialogo para previsualizarla
            FotoPrevDialog fp = new FotoPrevDialog();
            fp.show(getActivity().getSupportFragmentManager(), "fotoPrev");
        }
    }
}