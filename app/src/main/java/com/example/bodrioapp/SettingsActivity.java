package com.example.bodrioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    EditText editTamañoLetra;
    Spinner spinnerIdioma;
    Spinner spinnerTemas;
    Button btnGuardar;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aplicarTema();

        setContentView(R.layout.activity_settings);

        editTamañoLetra = findViewById(R.id.editTamañoLetra);
        spinnerIdioma = findViewById(R.id.spinnerIdioma);
        spinnerTemas = findViewById(R.id.spinnerTemas);
        btnGuardar = findViewById(R.id.btnGuardar);
        prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);

        btnGuardar.setOnClickListener(v -> guardarPreferencias());
    }

    // Aplica un tema con setTheme, con el valor que se consigue de las SharedPreferences.
    private void aplicarTema() {
        int tema = getSharedPreferences("mis_preferencias", MODE_PRIVATE)
                .getInt("tema", 0);

        if (tema == 0) {
            setTheme(R.style.Base_Theme_BodrioApp);
        } else if (tema == 1) {
            setTheme(R.style.Theme_BodrioApp_Azul);
        } else if (tema == 2) {
            setTheme(R.style.Theme_BodrioApp_Rojo);
        } else if (tema == 3) {
            setTheme(R.style.Theme_BodrioApp_Verde);
        }
    }

    // Aqui guarda todas las preferencias:
    // Tamaño de letra, idioma y el tema de la App
    private void guardarPreferencias() {
        String tamano = editTamañoLetra.getText().toString();
        if (tamano.isEmpty()) tamano = "10";

        String idioma = spinnerIdioma.getSelectedItem().toString();
        int posicionTema = spinnerTemas.getSelectedItemPosition();

        prefs.edit()
                .putString("tamañoLetra", tamano)
                .putString("idioma", idioma)
                .putInt("tema", posicionTema)
                .apply();
        finish();
        startActivity(getIntent());
    }
}
