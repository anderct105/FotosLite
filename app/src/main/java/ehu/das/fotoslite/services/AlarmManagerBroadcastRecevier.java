package ehu.das.fotoslite.services;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.RemoteViews;

import java.io.IOException;

import ehu.das.fotoslite.widget.MisFotosWidget;
import ehu.das.fotoslite.R;

/**
 * Alarma utilizada en el widget para actualizar la imagen que se muestra.
 */

public class AlarmManagerBroadcastRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.mis_fotos_widget);
        Bitmap bitmap = null;
        try {
            // Obtener bitmap correspondiente a la uri
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), MisFotosWidget.obtenerFotoRandom());
            remoteViews.setImageViewBitmap(R.id.miFoto, bitmap);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        // Hacerla más pequeña para que no de error
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true);
        remoteViews.setImageViewBitmap(R.id.miFoto, resized);
        System.out.println("Cargada imagen 2");
        // Actualizar el widget
        ComponentName tipowidget = new ComponentName(context, MisFotosWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(tipowidget, remoteViews);
        manager.updateAppWidget(tipowidget, remoteViews);
    }
}
