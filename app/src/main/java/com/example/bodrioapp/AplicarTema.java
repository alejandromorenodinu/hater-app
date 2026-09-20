package com.example.bodrioapp;

import android.content.Context;

public class AplicarTema {
    // Esta clase es una clase auxiliar para no tener que repetir codigo de una funcion a cada rato.
    // Basicamente esta clase sirve para aplicar el tema a toda la App.
    public static void aplicarTema(Context context) {
        // Aqui pilla el numero de posicion de la lista de temas
        int tema = context.getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE)
                .getInt("tema", 0);
        // Y luego dependiendo de la posicion, asigna un tema a la App.
        if (tema == 1) {
            context.setTheme(R.style.Theme_BodrioApp_Azul);
        } else if (tema == 2) {
            context.setTheme(R.style.Theme_BodrioApp_Rojo);
        } else if (tema == 3) {
            context.setTheme(R.style.Theme_BodrioApp_Verde);
        } else {
            context.setTheme(R.style.Base_Theme_BodrioApp);
        }
    }
}
