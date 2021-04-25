package ehu.das.fotoslite.lists;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ehu.das.fotoslite.R;
import ehu.das.fotoslite.lists.ElViewHolder;

/**
 * Define el adaptador del recycler view y se encarga de poner la imagen en cada uno de los elementos de la lista.
 */

public class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder> {

    private ArrayList<String> lasimagenes;
    private Context context;

    public ElAdaptadorRecycler(ArrayList<String> imagenes, Context context)
    {
        lasimagenes = imagenes;
        this.context = context;
    }

    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.foto_lista,null);
        ElViewHolder evh = new ElViewHolder(elLayoutDeCadaItem);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder holder, int position) {
        // Por cada foto obtener su referencia en firebase y cargarla en el elemento de la lista
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(lasimagenes.get(position));
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(holder.laimagen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lasimagenes.size();
    }

}