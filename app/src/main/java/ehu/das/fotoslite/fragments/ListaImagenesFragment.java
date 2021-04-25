package ehu.das.fotoslite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ehu.das.fotoslite.services.ConnectionDBServer;
import ehu.das.fotoslite.lists.ElAdaptadorRecycler;
import ehu.das.fotoslite.R;

/**
 * Este fragment mostrará una lista con las imágenes que le pertenecen al usuario.
 */

public class ListaImagenesFragment extends Fragment {

    public ListaImagenesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_imagenes, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Obtener las imagenes del usuario
        getImagenes();
        // Volver al menu
        ImageButton ib = getActivity().findViewById(R.id.volverButton);
        ib.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_listaImagenesFragment_to_menuFragment);
        });
    }

    public void getImagenes() {
        // Petición al servidor de las imágenes
        String parametros = "username=" + LoginFragment.usuario;
        Data datos = new Data.Builder()
                .putString("parametros",parametros)
                .putString("fichero", "get_images")
                .putString("method", "GET")
                .build();
        OneTimeWorkRequest trabajoPuntual =
                new OneTimeWorkRequest.Builder(ConnectionDBServer.class).setInputData(datos).build();
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                .observe(getActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        // Lista de las imágenes del usuario en formato JSON
                        String result = status.getOutputData().getString("result");
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayList<String> lista = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            String nombre = null;
                            try {
                                nombre = jsonArray.getJSONObject(i).getString("image_name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            lista.add(LoginFragment.usuario + "/" + nombre);
                        }
                        // Se crea el adaptador a partir de las fotos
                        setAdapter(lista);
                    }
                });
        WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);
    }

    public void setAdapter(ArrayList<String> nombres) {
        // Crea un recycler view con las fotos del usuario
        RecyclerView rv = getActivity().findViewById(R.id.imagenesRV);
        ElAdaptadorRecycler adaptadorRecycler = new ElAdaptadorRecycler(nombres, getActivity().getApplicationContext());
        rv.setAdapter(adaptadorRecycler);
        GridLayoutManager elLayoutRejillaIgual= new GridLayoutManager(getActivity(),2, GridLayoutManager.VERTICAL,false);
        rv.setLayoutManager(elLayoutRejillaIgual);
    }
}