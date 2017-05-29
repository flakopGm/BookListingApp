package com.example.joni.booklistingapp;

import android.net.Uri;

/*
 * Clase @Libro con una Uri para la imagen de la portada, un Título, autor y/o autores, fecha de
 * publicación, páginas totales del libro en cuestión el idioma y la web donde obtener más info o
 * el propio libro.
 */

public class Libro {

    private Uri portadaLibro;
    private String tituloLibro;
    private String autors;
    private String fechaPublicacion;
    private String paginasTotalesLibro;
    private String idiomaLibro;
    private String webLibro;

    public Libro(Uri linkPortada, String tituloLibro, String autors,
                 String PublicacionLibro, String paginasTotales, String idioma, String webLibro) {

        this.portadaLibro = linkPortada;
        this.tituloLibro = tituloLibro;
        this.autors = autors;
        this.fechaPublicacion = PublicacionLibro;
        this.paginasTotalesLibro = paginasTotales;
        this.idiomaLibro = idioma;
        this.webLibro = webLibro;
    }

    public Uri getPortadaLibro() {
        return portadaLibro;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public String getAutors() {
        return autors;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getPaginasTotalesLibro() {
        return paginasTotalesLibro;
    }

    public String getIdiomaLibro() {
        return idiomaLibro;
    }

    public String getWebLibro() {
        return webLibro;
    }
}
