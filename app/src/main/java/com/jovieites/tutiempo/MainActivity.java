package com.jovieites.tutiempo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.jovieites.tutiempo.ListItem.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    protected static final int COD_ADD = 100;

    public static final String SHPREFS = "sharedPrefs";

    private ArrayAdapter<Item> adaptadorItems;

    private ArrayList<Item> items;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){

        boolean toret = false;

        switch( menuItem.getItemId() ) {
            case R.id.idMenuItem2:
                Intent intent2 = new Intent(this, CoordActivity.class);
                this.startActivityForResult(intent2, COD_ADD);
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
        setContentView(R.layout.activity_main);



        ListView listaFav = (ListView) this.findViewById( R.id.listaFav);

        this.items = new ArrayList<Item>();
        this.adaptadorItems = new ArrayAdapter<Item>(this, android.R.layout.simple_selectable_list_item, this.items);
        listaFav.setAdapter(this.adaptadorItems);

        listaFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent datosRetornar = new Intent(MainActivity.this, SearchCityActivity.class);
                Item item = MainActivity.this.adaptadorItems.getItem(i);
                datosRetornar.putExtra("ciudad", item.getCiudad());
                startActivity(datosRetornar);
            }
        });
        this.registerForContextMenu(listaFav);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo cmi) {
            if (view.getId() == R.id.listaFav) {
                this.getMenuInflater().inflate(R.menu.context_menu, contextMenu);
                contextMenu.setHeaderTitle(R.string.app_name);
            }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        boolean toret = false;

        switch (menuItem.getItemId()) {
            case R.id.context_delete:
                int pos = ( (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo()).position ;
                    MainActivity.this.items.remove(pos);
                    MainActivity.this.adaptadorItems.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Item Eliminado",Toast.LENGTH_SHORT).show();
                    toret = true;
                    break;
                }

                return toret;
        }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == COD_ADD && resultCode == Activity.RESULT_OK){
            Item item = new Item( data.getExtras().getString("ciudad").toString());
            boolean check = true;
            for (int i = 0; i < this.items.size(); i++){
                if (this.items.get(i).getCiudad().equals(item.getCiudad())){
                    check = false;
                }
            } if(check == true) {

                SharedPreferences PREFS = getSharedPreferences(SHPREFS, MODE_PRIVATE);
                SharedPreferences.Editor EDPREFS = PREFS.edit();
                Set<String> FAVS = PREFS.getStringSet("favoritos", new HashSet<String>());
                this.adaptadorItems.add(item);
                FAVS.add(item.toString());
                EDPREFS.putStringSet("favoritos", FAVS);
                EDPREFS.apply();
                this.adaptadorItems.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Añadido con éxito", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void buscarCiudad(View v) {
        final EditText edCiudad = (EditText) findViewById(R.id.edCiudad);
        final Button btCiudad = (Button) this.findViewById( R.id.btCiudad);

        if (!edCiudad.getText().toString().equalsIgnoreCase("") ) {
            Intent datosRetornar = new Intent(getBaseContext(), SearchCityActivity.class);
            datosRetornar.putExtra("ciudad", edCiudad.getText().toString());
            startActivityForResult(datosRetornar, COD_ADD);
        }
        else {
            Toast.makeText(this, "Introduzca término de Búsqueda", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor EDPREFS = getSharedPreferences(SHPREFS, MODE_PRIVATE).edit();

        Set<String> FAVS = new HashSet<>();

        for(Item item: items){
            FAVS.add(item.toString());
        }

        EDPREFS.putStringSet("faviritos", FAVS);
        EDPREFS.apply();



    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences PREFS = getSharedPreferences(SHPREFS, MODE_PRIVATE);

        Set<String> FAVS = PREFS.getStringSet("favoritos", new HashSet<String>());

        this.adaptadorItems.clear();

        for(String str: FAVS){
            this.adaptadorItems.add(new Item(str));
        }

    }

}
