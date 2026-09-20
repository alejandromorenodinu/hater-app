package com.example.bodrioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Post> listaPosts;
    private ArrayList<Post> listaOriginal;
    private PostAdapter adapter;
    private String firebase = "Firebase";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aqui se usa la clase AplicarTema.
        AplicarTema.aplicarTema(this);
        // Aqui se usa la funcion aplicarTamañoLetra para cambiar el tamaño de la letra del activity
        aplicarTamanoLetra();
        // Lo mismo que aplicarTamañoLetra pero con el idioma
        aplicarIdioma();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Este prefs sirve para saber si mostrar o no el tutorial. En el caso que el usuario lo haya visto,
        // No mostrara el tutorial.
        SharedPreferences prefs =
                getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        boolean visto = prefs.getBoolean("tutorial_visto", false);
        if (!visto) {
            startActivity(new Intent(this, TutorialActivity.class));
            finish();
            return;
        }
        recyclerView = findViewById(R.id.recyclerPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPosts = new ArrayList<>();
        listaOriginal = new ArrayList<>();
        adapter = new PostAdapter(listaPosts, this);
        recyclerView.setAdapter(adapter);
        SearchView searchView = findViewById(R.id.searchView);
        // Aqui el SearchView llama a la funcion Filtrar cada que se introduce texto.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrar(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrar(newText);
                return true;
            }
        });
        // Funcion que devuelve los Posts de Firebase
        leerPosts();
        // Listeners que envian a otras Views.
        findViewById(R.id.btnAddPost).setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.favoritos).setOnClickListener(v ->
                startActivity(new Intent(this, FavoritosActivity.class)));
        findViewById(R.id.reviewed).setOnClickListener(v ->
                startActivity(new Intent(this, ReviewedActivity.class)));
        findViewById(R.id.ajustes).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }
    private void leerPosts() {
        // Pilla la BDD de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://reviewsnegativas-ec0fc-default-rtdb.europe-west1.firebasedatabase.app"
        );
        // Pilla los Posts de la Firebase
        DatabaseReference postsRef = database.getReference("Posts");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            // Este metodo se ejecuta cuando Firebase devuelve los datos
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Se limpia la lista de Posts, tanto la lista principal
                // Como la lista original.
                listaPosts.clear();
                listaOriginal.clear();

                // Aqui se añaden los posts de la Firebase a las listas de Posts.
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.id = postSnapshot.getKey();
                        listaPosts.add(post);
                        listaOriginal.add(post);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            // Si no pongo este metodo, el OnDataChange se queja, pero realmente no hace nada
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    // Funcion para aplicar el tamaño de la letra de los SharedPreferences
    private void aplicarTamanoLetra() {
        // Consigo las prefs y saco el valor en la variable fontScale
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        float fontScale = Float.parseFloat(prefs.getString("tamañoLetra", "10")) / 10;
        // Aqui consigo la configuracion de la App para cambiarle el tamaño de la letra por la escala
        // Que esta guardada en los SharedPreferences
        Configuration config = getResources().getConfiguration();
        config.fontScale = fontScale;
        getResources().updateConfiguration(
                config,
                getResources().getDisplayMetrics()
        );
    }
    private void aplicarIdioma() {
        // Consigo las prefs y saco el valor en la variable idioma
        SharedPreferences prefs = getSharedPreferences("mis_preferencias", MODE_PRIVATE);
        String idioma = prefs.getString("idioma", "Español");

        // Inicializo una variable Locale para añadirle un valor dependiendo del idioma
        // que esta en los SharedPreferences
        Locale locale;
        if (idioma.equals("English")) {
            locale = new Locale("en");
        } else if (idioma.equals("Català")) {
            locale = new Locale("ca");
        } else {
            locale = new Locale("es");
        }
        // Aqui le asigno el idioma
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
    // Esta funcion es complementaria al SearchView, basicamente filtra
    // Dependiendo el texto
    private void filtrar(String texto) {
        // Si el texto del SearchView esta vacio, muestra la lista de Posts original,
        // que es la que devuelve la Firebase
        if (texto.isEmpty()) {
            adapter.actualizarLista(listaOriginal);
            return;
        }

        // Si no es el caso, entonces crea un nuevo Array
        ArrayList<Post> listaFiltrada = new ArrayList<>();
        // Entonces por cada post de la lista original, si contiene un fragmento
        // de texto del SearchView en su titulo, lo añade a la lista filtrada
        for (Post post : listaOriginal) {

            if (post.titulo.toLowerCase().contains(texto.toLowerCase()) ||
                    post.descripcionCorta.toLowerCase().contains(texto.toLowerCase())) {

                listaFiltrada.add(post);
            }
        }
        // Entonces luego actualiza el adapter con la lista filtrada.
        adapter.actualizarLista(listaFiltrada);
    }
}
