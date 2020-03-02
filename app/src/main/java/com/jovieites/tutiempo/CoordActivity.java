package com.jovieites.tutiempo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jovieites.tutiempo.Asistente.Asistente;
import com.jovieites.tutiempo.Common.Common;
import com.jovieites.tutiempo.Modelo.OpenWeatherMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;



import java.lang.reflect.Type;


public class CoordActivity extends AppCompatActivity implements LocationListener {
    TextView txtViewCiudad, txtViewUltActualizacion, txtViewDescripcion, txtViewHumedad, txtViewHora, txtViewCelsius;

    ImageView imageView;

    Button btFav;

    LocationManager gestorLocal;

    String proveedor;

    static double latitud, longitud;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MI_PERMISO = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_coord, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        boolean toret = false;

        switch( menuItem.getItemId() ) {
            case R.id.idMenuItem1:
                CoordActivity.this.finish();
                toret = true;
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }

        return toret;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_display_screen);


        //Asociamos los textView, el imageView y el botón

        txtViewCiudad = (TextView)findViewById(R.id.txtViewCiudad);
        txtViewUltActualizacion = (TextView)findViewById(R.id.txtViewUltActualizacion);
        txtViewDescripcion = (TextView)findViewById(R.id.txtViewDescripcion);
        txtViewHumedad = (TextView)findViewById(R.id.txtViewHumedad);
        txtViewHora = (TextView)findViewById(R.id.txtViewHora);
        txtViewCelsius = (TextView)findViewById(R.id.txtViewCelsius);
        imageView = (ImageView)findViewById(R.id.ImageView);
        btFav = (Button)findViewById(R.id.btFav);

        //Obtenemos la localización del dispositivo

        gestorLocal = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        proveedor = gestorLocal.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CoordActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MI_PERMISO);
        }
        Location local = gestorLocal.getLastKnownLocation(proveedor);
        if (local == null) {
            Log.e("TAG", "Sin Localizacion");
        } else {
            latitud = local.getLatitude();
            longitud = local.getLongitude();

            gestorLocal.requestLocationUpdates(proveedor, 1000, 100, this );
            new GetTiempo().execute(Common.peticionApiCoord(String.valueOf(latitud),String.valueOf(longitud)));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CoordActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MI_PERMISO);
        }
        gestorLocal.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CoordActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MI_PERMISO);
            gestorLocal.requestLocationUpdates(proveedor, 1000, 100, this);
        }

        btFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent datosRetornar = new Intent();
                datosRetornar.putExtra("ciudad", String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
                CoordActivity.this.setResult(Activity.RESULT_OK, datosRetornar);
                CoordActivity.this.finish();
            }
        });
        btFav.setEnabled( true );
    }

    @Override
    public void onLocationChanged(Location local) {
        latitud = local.getLatitude();
        longitud = local.getLongitude();

        new GetTiempo().execute(Common.peticionApiCoord(String.valueOf(latitud),String.valueOf(longitud)));
    }

    @Override
    public void onStatusChanged(String proveedor, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String proveedor) {

    }

    @Override
    public void onProviderDisabled(String proveedor) {

    }

    private class GetTiempo extends AsyncTask<String,Void,String> {

        ProgressDialog progressDialog = new ProgressDialog(CoordActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Espere, por favor...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Asistente http = new Asistente();
            stream = http.getDatosHTTP(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains("Error: Not Found City")){
                progressDialog.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            progressDialog.dismiss();

            txtViewCiudad.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            txtViewUltActualizacion.setText(String.format("Última actualización: %s", Common.getFechaActual()));;
            txtViewDescripcion.setText(String.format("Previsión: " + "%s",openWeatherMap.getWeather().get(0).getDescription()));
            txtViewHumedad.setText("Humedad: " + String.format("%d%%",openWeatherMap.getMain().getHumidity()));
            txtViewHora.setText(String.format("Salida/Puesta Sol: " + "%s/%s",Common.timeStampUnixAFechaYHora(openWeatherMap.getSys().getSunrise()),Common.timeStampUnixAFechaYHora(openWeatherMap.getSys().getSunset())));
            txtViewCelsius.setText(String.format("Temperatura: " + "%.2f °C",openWeatherMap.getMain().getTemp()));
            Picasso.get()
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);

        }
    }
}
