package ehu.das.fotoslite.widget;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Random;

import ehu.das.fotoslite.MainActivity;
import ehu.das.fotoslite.R;
import ehu.das.fotoslite.services.AlarmManagerBroadcastRecevier;

/**
 * Implementación del widget que visualiza una foto del dispositivo de forma aleatoria.
 */
public class MisFotosWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views1 = new RemoteViews(context.getPackageName(), R.layout.mis_fotos_widget);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mis_fotos_widget);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), obtenerFotoRandom());
            views.setImageViewBitmap(R.id.miFoto, bitmap);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true);
            views.setImageViewBitmap(R.id.miFoto, resized);
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.updateAppWidget(appWidgetId, views);
            System.out.println("Cargada imagen");
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static Uri obtenerFotoRandom() {
        try {
            int index = new Random().nextInt(MainActivity.misFotos.size());
            return MainActivity.misFotos.get(index);
        } catch (IllegalArgumentException iae) {
            System.out.println("No hay fotos");
        }
        return null;
    }

    @SuppressLint("ShortAlarm")
    @Override
    public void onEnabled(Context context) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastRecevier.class);
        intent.setAction("android.intent.action.RECEIVE_BOOT_COMPLETED");
        PendingIntent pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000 , pi);
    }
}