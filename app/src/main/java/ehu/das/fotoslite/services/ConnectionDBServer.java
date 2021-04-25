package ehu.das.fotoslite.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Clase genérica para hacer las peticiones al servidor, hecha principalmente con parámetros para su usabilidad.
 */

public class ConnectionDBServer extends Worker {

    public ConnectionDBServer(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Recibe el método, los parámetros y el fichero del servidor al que hacer la petición
        HttpURLConnection urlConnection = null;
        String parametros = getInputData().getString("parametros");
        String fichero = getInputData().getString("fichero");
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/acejudo001/WEB/" + fichero + ".php";
        if (getInputData().getString("method").equals("GET")){
            direccion += "?" + parametros;
        }
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod(getInputData().getString("method"));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();
            // Realiza la conexión
            int statusCode = urlConnection.getResponseCode();
            Data datos = null;
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();
                // Devuelve el resultado en texto
                datos = new Data.Builder().putString("result", result).build();
            }
            return ListenableWorker.Result.success(datos);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }
}
