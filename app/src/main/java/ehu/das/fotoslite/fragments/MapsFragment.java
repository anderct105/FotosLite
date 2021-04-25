package ehu.das.fotoslite.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import ehu.das.fotoslite.services.ConnectionDBServer;
import ehu.das.fotoslite.R;

/**
 * Fragment que muestra un mapa con las ubicaciones en las que el usuario ha sacado foto.
 */

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Se realiza una petición de las ubicaciones de las fotos del usuario
            String parametros = "username=" + LoginFragment.usuario;
            Data datos = new Data.Builder()
                    .putString("parametros", parametros)
                    .putString("fichero", "get_locations")
                    .putString("method", "GET")
                    .build();
            OneTimeWorkRequest trabajoPuntual =
                    new OneTimeWorkRequest.Builder(ConnectionDBServer.class).setInputData(datos).build();
            WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(trabajoPuntual.getId())
                    .observe(getActivity(), status -> {
                        if (status != null && status.getState().isFinished()) {
                            // Devuelve un array con pares longitud y latitud de cada foto
                            String result = status.getOutputData().getString("result");
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    // Se añade un punto al mapa por cada elemento de la lista
                                    double latitude = jsonArray.getJSONObject(i).getDouble("latitude");
                                    double longitude = jsonArray.getJSONObject(i).getDouble("longitude");
                                    LatLng point = new LatLng(latitude, longitude);
                                    googleMap.addMarker(new MarkerOptions().position(point));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            // Pone un marcador en la ubicación actual
            WorkManager.getInstance(getActivity()).enqueue(trabajoPuntual);
            FusedLocationProviderClient cliente = LocationServices.getFusedLocationProviderClient(getActivity());
            cliente.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitud = location.getLatitude();
                        double longitud = location.getLongitude();
                        LatLng point = new LatLng(latitud, longitud);
                        googleMap.addMarker(new MarkerOptions().position(point).title("Your location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    }
                }
            });
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}

