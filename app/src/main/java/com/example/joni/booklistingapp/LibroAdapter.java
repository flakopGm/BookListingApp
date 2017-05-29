package com.example.joni.booklistingapp;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/*
 * Adaptador para la clase Libro.
 */

public class LibroAdapter extends ArrayAdapter<Libro> {

    public LibroAdapter(Context context, List<Libro> libros) {
        super(context, 0, libros);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Compruebe si se está reutilizando una vista existente, de lo contrario infle la vista
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);

            // Libro actual.
            Libro currentLibro = getItem(position);
            // Se localiza el ImageView y se establece la imagen dada por el link del libro mediante
            // el método @getPortadaLibro.
            ImageView portada = (ImageView) listItemView.findViewById(R.id.portada);
            Picasso.with(getContext()).load(currentLibro.getPortadaLibro()).into(portada);
            //Se localiza el TextView donde se alojará el título del Libro y se establece mediante
            // getTituloLibro.
            TextView titulo = (TextView) listItemView.findViewById(R.id.titulo);
            titulo.setText(currentLibro.getTituloLibro());
            //Se localiza el TextView donde se alojará el o los autores del Libro y se establece
            // el texto mediante getAutors, posteriormente se establece en mayúscula..
            TextView autores = (TextView) listItemView.findViewById(R.id.autors);
            autores.setText(currentLibro.getAutors().toUpperCase());
            /*Se localizan los TextViews donde se alojará la fehca de publicación, las páginas que
            * contiene y el idioma del Libro y se establece los string adecuados.
            * Mediante el método.append agregamos a cada definición su valor correspondiente.
            */
            TextView añoPublicacion = (TextView) listItemView.findViewById(R.id.fecha_publicacion);
            añoPublicacion.setText(R.string.año_publicacion);
            añoPublicacion.append(" " + currentLibro.getFechaPublicacion());
            TextView pagTotales = (TextView) listItemView.findViewById(R.id.paginas_libro);
            pagTotales.setText(R.string.paginas);
            pagTotales.append(" " + currentLibro.getPaginasTotalesLibro());
            TextView idioma = (TextView) listItemView.findViewById(R.id.idioma);
            idioma.setText(R.string.idioma);
            idioma.append(" " + currentLibro.getIdiomaLibro());
        }
        return listItemView;
    }
}