package com.example.joni.booklistingapp;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Métodos auxiliares relacionados con la solicitud y recepción de datos de terremotos de USGS.
 */
public final class QueryUtils {

    /**
     * Etiqueta de los mensajes de registro.
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Cree un constructor privado porque nadie debería crear un objeto {@link QueryUtils}.
     * Esta clase sólo está destinada a mantener variables estáticas y métodos, a los que se puede
     * acceder Directamente del nombre de clase QueryUtils (y no se necesita una instancia de objeto
     * de QueryUtils).
     */
    private QueryUtils() {
    }

    public static List<Libro> recogerDatosLibro(String requestUrl) {
        // Creacion objeto url.
        URL url = createUrl(requestUrl);

        // Realizar solicitud HTTP a la dirección URL y recibir una respuesta JSON
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        //Extraiga los campos relevantes de la respuesta JSON y cree un objeto {@link Terremoto}
        List<Libro> earthquake = extractBookInfo(jsonResponse);

        return earthquake;
    }

    /**
     * Devuelve el nuevo objeto URL de la cadena de caracteres.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Realiza una solicitud HTTP a la URL dada y devuelve una cadena como respuesta.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // Si la URL es nula, vuelva pronto.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Si la solicitud fue satisfactoria (código de respuesta 200),
            // Luego leer el flujo de entrada y analizar la respuesta.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convierta el {@link InputStream} en un String que contenga el Respuesta JSON entera
     * del servidor.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Devuelve una lista de objetos {@link Libro} que se ha creado a partir del
     * análisis de una respuesta JSON.
     */
    public static List<Libro> extractBookInfo(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        // Crear una ArrayList vacía que podemos comenzar a agregar terremotos.
        List<Libro> listaLibros = new ArrayList<>();

        // Intente analizar el JSON_RESPONSE. Si hay un problema con la forma en que se
        // formatea el JSON, se lanzará un objeto de excepción JSONException. Coge la excepción
        // para que la aplicación no se bloquee e imprima el mensaje de error en los registros.
        try {
            JSONObject lista = new JSONObject(bookJSON);
            JSONArray listaArray = lista.getJSONArray("items");

            for (int i = 0; i < listaArray.length(); i++) {

                JSONObject libro = listaArray.getJSONObject(i);
                JSONObject atributosLibros = libro.getJSONObject("volumeInfo");
                JSONObject linkPortada = atributosLibros.getJSONObject("imageLinks");
                String urilinkLibro = linkPortada.getString("thumbnail");
                Uri imgUri = Uri.parse(urilinkLibro);
                String titulo = atributosLibros.getString("title");
                JSONArray autors = atributosLibros.getJSONArray("authors");
                String autores = "";
                for (int x = 0; x < autors.length(); x++) {
                    autores = autores.concat(autors.getString(x) + "\n");
                }
                String fecha = atributosLibros.getString("publishedDate");
                String año = fecha.substring(0, 4);
                Integer paginas = atributosLibros.getInt("pageCount");
                String idioma = atributosLibros.getString("language");
                String web = atributosLibros.getString("previewLink");

                Libro currentLibro = new Libro(imgUri, titulo, autores, año, paginas.toString(), idioma, web);
                listaLibros.add(currentLibro);
            }
        } catch (JSONException e) {

            //Si se produce un error al ejecutar cualquiera de las instrucciones anteriores en el
            // bloque "try", tome la excepción aquí, para que la aplicación no se bloquee. Imprima
            // un mensaje de registro con el mensaje de la excepción.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // Devolver la lista de libros.
        return listaLibros;
    }

}