package com.jovieites.tutiempo.Common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static String API_CLAVE = "14929bdb5ddab89d7680bbe52f6c74af";
    public static String API_ENLACE = "api.openweathermap.org/data/2.5/weather";

    public static String peticionApiCiudad(String ciudad){
        StringBuilder stringBuilder = new StringBuilder(API_ENLACE);
        stringBuilder.append(String.format("?q=%s&APPID=%s&units=metric", ciudad, API_CLAVE));
        return stringBuilder.toString();
    }

    public static String peticionApiCoord(String latitud, String longitud){
        StringBuilder stringBuilder = new StringBuilder(API_ENLACE);
        stringBuilder.append(String.format("?lat=%s&lon=%s&APPID=%s&units=metric", latitud, longitud, API_CLAVE));
        return stringBuilder.toString();
    }

    public static String timeStampUnixAFechaYHora(double timeStampUnix){
        DateFormat formatoFecha = new SimpleDateFormat("HH:mm");
        Date fecha = new Date();
        fecha.setTime((long)timeStampUnix*1000);
        return formatoFecha.format(fecha);
    }

    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png", icon);
    }

    public static String getFechaActual(){
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fecha = new Date();
        return formatoFecha.format(fecha);
    }
}
