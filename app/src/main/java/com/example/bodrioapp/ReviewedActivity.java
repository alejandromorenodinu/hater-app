package com.example.bodrioapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ReviewedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Post> listaPosts;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AplicarTema.aplicarTema(this);
        setContentView(R.layout.activity_reviewed);

        recyclerView = findViewById(R.id.recyclerReviewed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaPosts = new ArrayList<>();
        adapter = new PostAdapter(listaPosts, this);
        recyclerView.setAdapter(adapter);

        cargarValorados();
    }

    private void cargarValorados() {
        // Esta funcion carga los posts valorados de la BDD de SQLite.

        // Aqui pilla la BDD y hace un cursor con una raw query.
        BodrioDb dbBodrio = new BodrioDb(this);
        Cursor cursor = dbBodrio.getReadableDatabase().rawQuery("SELECT id FROM valorados", null);

        // Pilla la BDD de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://reviewsnegativas-ec0fc-default-rtdb.europe-west1.firebasedatabase.app"
        );

        // Basicamente aqui mira por cada valor devuelto de la query del cursor
        while (cursor.moveToNext()) {
            // Aqui pilla el ID del post
            String id = cursor.getString(0);
            // Y aqui devuelve el Post de la Firebase que coincida con el ID.
            database.getReference("Posts").child(id)
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
