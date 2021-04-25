package ehu.das.fotoslite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ehu.das.fotoslite.services.ConnectionDBServer;
import ehu.das.fotoslite.R;

/**
 * Fragment encargado de hacer el login utilizando una base de datos remota.
 */

public class LoginFragment extends Fragment {

    public static String usuario = "prueba";

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button login = getActivity().findViewById(R.id.login);
        TextView registrarse = getActivity().findViewById(R.id.registrarse);
        registrarse.setOnClickListener(v -> {Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_registroFragment);});
        // Al iniciar sesión buscar en la base de datos si lo ha hecho bien
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Petición al servidor para verificar si se han introducido bien los datos
                EditText campoNombre = getActivity().findViewById(R.id.usuarioRegistro);
                String nombreUsuario = campoNombre.getText().toString();
                EditText campoContra = getActivity().findViewById(R.id.contraseñaRegistro);
                String contrasena = campoContra.getText().toString();
                String parametros = "username=" + nombreUsuario +"&password="+ contrasena;
                Data datos = new Data.Builder()
                        .putString("parametros",parametros)
                        .putString("fichero", "login")
                        .putString("method", "POST")
                        .build();
                OneTimeWorkRequest trabajoPuntual =
                        new OneTimeWorkRequest.Builder(ConnectionDBServer.class).setInputData(datos).build();
                WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                        .observe(getActivity(), status -> {
                            if (status != null && status.getState().isFinished()) {
                                // Se obtiene el boolean que indica si se ha iniciado sesión
                                String result = status.getOutputData().getString("result");
                                try {
                                    JSONParser parser = new JSONParser();
                                    JSONObject json = (JSONObject) parser.parse(result);
                                    if ((boolean)json.get("success")) {
                                        // Se ha iniciado sesión
                                        // Se guarda el nombre de usuario en una variable general para más adelante
                                        usuario = nombreUsuario;
                                        Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_menuFragment);
                                    } else {
                                        Toast.makeText(getContext(), "Datos incorrectos", Toast.LENGTH_LONG).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);

            }
        });
    }
}