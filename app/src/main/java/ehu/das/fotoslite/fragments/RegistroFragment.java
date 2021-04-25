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

import ehu.das.fotoslite.R;
import ehu.das.fotoslite.services.ConnectionDBServer;

/**
 * Permite registrar un nuevo usuario
 */
public class RegistroFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Establece listeners
        TextView volverLogin = getActivity().findViewById(R.id.registro_a_inicio_sesion);
        volverLogin.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_registroFragment_to_loginFragment));
        Button registrarse = getActivity().findViewById(R.id.registrar);
        registrarse.setOnClickListener(v -> registrar());
    }

    public void registrar() {
        // Lee y valida los campos y en caso correcto los añade
        EditText campoNombre = getActivity().findViewById(R.id.usuarioRegistro);
        EditText campoEmail = getActivity().findViewById(R.id.emailRegistro);
        EditText campoContra = getActivity().findViewById(R.id.contraseñaRegistro);
        EditText campoRepitaContra = getActivity().findViewById(R.id.repiteContraseñaRegistro);
        String nombre = campoNombre.getText().toString().trim();
        String email = campoEmail.getText().toString().trim();
        String contra = campoContra.getText().toString().trim();
        String repitaContra = campoRepitaContra.getText().toString().trim();
        if (formularioValido(nombre, email, contra, repitaContra)) {
            if (contra.equals(repitaContra)) {
                registrarUsuario(nombre, email, contra);
            } else {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Ese usuario o email ya existe", Toast.LENGTH_LONG).show();
        }
    }

    private void registrarUsuario(String nombre, String email, String contrasena) {
        // Realiza un post al servidor para añadir el nuevo usuario, en caso de que se pueda
        String parametros = "username=" + nombre + "&password=" + contrasena + "&email=" + email;
        Data datos = new Data.Builder()
                .putString("parametros", parametros)
                .putString("fichero", "registro")
                .putString("method", "POST")
                .build();
        OneTimeWorkRequest trabajoPuntual =
                new OneTimeWorkRequest.Builder(ConnectionDBServer.class).setInputData(datos).build();
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                .observe(getActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String result = status.getOutputData().getString("result");
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(result);
                            if ((boolean) json.get("success")) {
                                Navigation.findNavController(getView()).navigate(R.id.action_registroFragment_to_loginFragment);
                                Toast.makeText(getContext(), "Usuario registrado!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "El usuario ya existe!", Toast.LENGTH_LONG).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
        WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);
    }

    private boolean formularioValido(String nombre, String email, String contrasena, String repitacontrasena) {
        // Valida el formulario y en caso de algun fallo muestra un toast
        boolean valido = true;
        if (nombre.equals("") || email.equals("") || contrasena.equals("") || repitacontrasena.equals("")) {
            valido = false;
            Toast.makeText(getContext(), "No puede haber campos vacíos", Toast.LENGTH_LONG).show();
        } else if (contrasena.length() < 6 || contrasena.length() > 20) {
            valido = false;
            Toast.makeText(getContext(), "La contraseña debe tener entre 6 y 20 caracteres", Toast.LENGTH_LONG).show();
        } else if (!email.contains("@")) {
            valido = false;
            Toast.makeText(getContext(), "Introduce un email válido", Toast.LENGTH_LONG).show();
        }
        return valido;
    }
}