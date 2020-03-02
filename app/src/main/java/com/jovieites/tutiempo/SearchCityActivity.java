package com.jovieites.tutiempo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jovieites.tutiempo.Asistente.Asistente;
import com.jovieites.tutiempo.Common.Common;
import com.jovieites.tutiempo.Modelo.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

public class SearchCityActivity extends AppCompatActivity {
    TextView txtViewCiudad, txtViewUltActualizacion, txtViewDescripcion, txtViewHumedad, txtViewHora, txtViewCelsius;

    ImageView imageView;

    Button btFav;

    String ciudad;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        boolean toret = false;

        switch( menuItem.getItemId() ) {
            case R.id.idMenuItem1:
                SearchCityActivity.this.finish();
                toret = true;
                break;
            case R.id.idMenuItem2:
                Intent intent2 = new Intent(this, CoordActivity.class);
                this.startActivity(intent2);
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

        ciudad= getIntent().getStringExtra("ciudad");

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Invocamos la API con el string ciudad

        new GetTiempo().execute(Common.peticionApiCiudad(ciudad));

        btFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent datosRetornar = new Intent();
                datosRetornar.putExtra("ciudad", String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
                SearchCityActivity.this.setResult(Activity.RESULT_OK, datosRetornar);
                SearchCityActivity.this.finish();
            }
        });
        btFav.setEnabled( true );
    }

    private class GetTiempo extends AsyncTask<String,Void,String> {

        ProgressDialog progressDialog = new ProgressDialog(SearchCityActivity.this);


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
