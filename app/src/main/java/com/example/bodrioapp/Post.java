package com.example.bodrioapp;

public class Post {

    // Esta clase es importante ya que es la clase en la que
    // se basan los posts.

    public String id;
    public String titulo;
    public String descripcionCorta;
    public String contenido;
    public double promedio;
    public Post() { }
    public Post(String titulo, String descripcionCorta, String contenido, double promedio) {
        this.titulo = titulo;
        this.descripcionCorta = descripcionCorta;
        this.contenido = contenido;
        this.promedio = promedio;
    }
}
