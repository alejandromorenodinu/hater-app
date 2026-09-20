package com.example.bodrioapp;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavoritosActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Post> listaPosts;
    PostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AplicarTema.aplicarTema(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);
        recyclerView = findViewById(R.id.recyclerFavoritos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPosts = new ArrayList<>();
        adapter = new PostAdapter(listaPosts, this);
        recyclerView.setAdapter(adapter);
        cargarFavoritos();
    }
    // Funcion que carga los posts favoritos, directamente desde la BDD SQLite
    private void cargarFavoritos() {
        // Aqui pilla la BDD local
        BodrioDb dbBodrio = new BodrioDb(this);
        // Y creo un cursor con una raw query para que me devuelva el ID de los posts favoritos
        // Esto para luego filtrarlo en la Firebase por IDs.
        Cursor cursor = dbBodrio.getReadableDatabase().rawQuery("SELECT id FROM favoritos", null);
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://reviewsnegativas-ec0fc-default-rtdb.europe-west1.firebasedatabase.app"
        );
        // En este while, va filtrando uno por uno los Posts favoritos.
        while (cursor.moveToNext()) {
            // Pilla el string del ID del cursor, que recordemos, devolvio una query
            String id = cursor.getString(0);
            // Aqui, en la "tabla" Posts (que bueno, es un JSON, no una tabla SQL pero se entiende)
            // va buscando en la BDD de Firebase el post que coincida con el ID.
            database.getReference("Posts")
                    .child(id)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        Post post = snapshot.getValue(Post.class);
                        if (post != null) {
                            post.id = id;
                            listaPosts.add(post);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        cursor.close();
    }
}
