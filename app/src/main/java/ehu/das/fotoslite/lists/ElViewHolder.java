package ehu.das.fotoslite.lists;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ehu.das.fotoslite.R;

/**
 * Define los elementos de cada elemento de la lista
 */

public class ElViewHolder extends RecyclerView.ViewHolder {

    public ImageView laimagen;

    public ElViewHolder(@NonNull View itemView) {
        super(itemView);
        laimagen = itemView.findViewById(R.id.foto);
    }
}