package com.example.joni.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
 * Clase Principal.
 */

public class MainActivity extends AppCompatActivity {

    private int CONSULTA_GOOGLE_BOOK = R.string.api_book_google;
    private LibroAdapter adapter;
    private EditText editText;
    private ProgressBar progressBar;
    private TextView emptyView;
    private String busquedaCriterio;
    private ConnectivityManager cm;
    private ImageView imagenFondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Localizamos el ImageView de fondo y definimos su imagen.
        imagenFondo = (ImageView) findViewById(R.id.imagen_buscador);
        imagenFondo.setImageResource(R.drawable.libros_variados);
        // Localizamos el ListView para definirle el @adapter, pasarle la vista vacía y la
        // escucha para los items click
        final ListView listaLibros = (ListView) findViewById(R.id.list);
        adapter = new LibroAdapter(this, new ArrayList<Libro>());
        listaLibros.setAdapter(adapter);
        emptyView = (TextView) findViewById(R.id.empty_view);
        listaLibros.setEmptyView(emptyView);
        listaLibros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adapter.getItem
                        (position).getWebLibro()));
                startActivity(intent);
            }
        });
        // Obtener una referencia al ConnectivityManager para comprobar el estado de la
        // conectividad de red.
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Inicialmente localizamos el EditText, el ProgressBar y el botón de buscar, posteriormente
        // se agrega la escucha para el botón de buscar.
        editText = (EditText) findViewById(R.id.edit_text);
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        Button buttonBuscar = (Button) findViewById(R.id.boton_buscar);
        buttonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ocultación del teclado.
                View view = getCurrentFocus();
                view.clearFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                /* Inicialmente ocultamos la imagen de fondo, posteriormente creamos un
                    StringBuilder para poder pasar la url completa con el criterio del usuario a
                    buscar, también procedemos a ocultar el progressBar
                 */
                imagenFondo.setVisibility(View.GONE);
                StringBuilder builder = new StringBuilder();
                String busqueda = editText.getText().toString();
                String apiBook = getString(CONSULTA_GOOGLE_BOOK);
                builder.append(apiBook).append(busqueda);
                busquedaCriterio = builder.toString();
                progressBar.setVisibility(View.VISIBLE);
                if (progressBar.getVisibility() == View.VISIBLE) {
                    emptyView.setVisibility(View.GONE);
                }
                // Obtenga detalles sobre la red de datos predeterminada activa actualmente
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                // Si hay una conexión de red, busque datos
                if (networkInfo != null && networkInfo.isConnected()) {
                    LibroAsyncTask asyncTask = new LibroAsyncTask();
                    asyncTask.execute(busquedaCriterio);
                } else {
                    //Si no vacia el adaptador, oculta el progressBar y muestra el text para
                    // la empty View.
                    adapter.clear();
                    progressBar.setVisibility(View.GONE);
                    emptyView.setText(R.string.no_internet);
                }
            }
        });
        /* Localización del botón borrar y definición de escucha. Vaciamos el EditText el
           adaptador, el String de la búsqueda y ocultamos el resto de elementos y sólo mostramos
           la imagen de fondo.
         */
        Button botonBorrar = (Button) findViewById(R.id.boton_borrar);
        botonBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                adapter.clear();
                busquedaCriterio = "";
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                imagenFondo.setVisibility(View.VISIBLE);
            }
        });
    }

    // AsyncTask
    private class LibroAsyncTask extends AsyncTask<String, Void, List<Libro>> {

        @Override
        protected List<Libro> doInBackground(String... params) {
            //No realice la solicitud si no hay URL o si la primera URL es nula.
            if (params.length < 1 || params[0] == null) {
                return null;
            }
            List<Libro> libros = QueryUtils.recogerDatosLibro(params[0]);

            return libros;
        }

        protected void onPostExecute(List<Libro> data) {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.coincidencias);
            // Borre el adaptador de datos de terremotos previos
            adapter.clear();
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
                Toast.makeText(MainActivity.this, R.string.mas_info, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
