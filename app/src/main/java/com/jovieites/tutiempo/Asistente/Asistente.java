package com.jovieites.tutiempo.Asistente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Asistente {
    static String stream = null;

    public Asistente(){

    }

    public String getDatosHTTP(String urlString){
        try{
            URL url = new URL("https://" + urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setReadTimeout(1000);
            if(httpURLConnection.getResponseCode() == 200);
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String linea;
                while ((linea = reader.readLine())!=null)
                    stringBuilder.append(linea);
                stream = stringBuilder.toString();
                httpURLConnection.disconnect();
            }

        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return stream;
    }
}
