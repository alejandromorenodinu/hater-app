package com.example.bodrioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class TutorialActivity extends AppCompatActivity {

    // Esta clase es la Activity a modo de tutorial.

    // Sencillamente contine un ViewPager y un Button
    private ViewPager2 viewPager;
    private Button btnEmpezar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        AplicarTema.aplicarTema(this);

        viewPager = findViewById(R.id.viewPagerTutorial);
        btnEmpezar = findViewById(R.id.btnEmpezar);

        // Aqui le a un array las imagenes del ViewPager
        int[] imagenes = {
                R.drawable.tutorial1,
                R.drawable.tutorial2,
                R.drawable.tutorial3,
                R.drawable.tutorial4,
                R.drawable.tutorial5
        };

        // Y en el adapter del ViewPager le asigno las fotos
        TutorialAdapter adapter = new TutorialAdapter(imagenes);
        viewPager.setAdapter(adapter);

        // Aqui si le das al boton, edita las SharedPreferences para añadir un boolean
        // para indicar si has visto el tutorial. Una vez visto, ya no vuelve a salir.
        btnEmpezar.setOnClickListener(v -> {
            SharedPreferences prefs =
                    getSharedPreferences("mis_preferencias", MODE_PRIVATE);

            prefs.edit().putBoolean("tutorial_visto", true).apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
