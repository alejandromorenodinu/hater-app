package com.example.bodrioapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AplicarTema.aplicarTema(this);
        setContentView(R.layout.activity_post_detail);
        TextView txtTitulo = findViewById(R.id.txtTituloDetalle);
        TextView txtDescripcion = findViewById(R.id.txtDescripcionDetalle);
        TextView txtContenido = findViewById(R.id.txtContenidoDetalle);
        TextView txtPromedio = findViewById(R.id.txtPromedioDetalle);
        // Aqui agarro del intent los valores del post para mostrarlo.
        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");
        String contenido = getIntent().getStringExtra("contenido");
        double promedio = getIntent().getDoubleExtra("promedio", 0);
        String id = getIntent().getStringExtra("id");
        txtTitulo.setText(titulo);
        txtDescripcion.setText(descripcion);
        txtContenido.setText(contenido);
        txtPromedio.setText("Valoración: " + promedio);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button btnValorar = findViewById(R.id.btnValorar);
        Button btnFavorito = findViewById(R.id.btnFavorito);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        // En el boton favorito, hago que agarre la BDD local y añada el id del post
        // en la tabla de los favoritos
        btnFavorito.setOnClickListener(v -> {
            BodrioDb dbBodrio = new BodrioDb(this);
            SQLiteDatabase db = dbBodrio.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("titulo", titulo);
            db.insertWithOnConflict(
                    "favoritos",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );
            // cuando añade el post en la bdd, reproduce este sonido.
            mediaPlayer = MediaPlayer.create(this, R.raw.guardar);
            mediaPlayer.start();
            // ademas que sale un toast para avisar que se guardo.
            Toast.makeText(this, "Se ha guardado la reseña: " + titulo, Toast.LENGTH_SHORT).show();
        });
        // boton para valorar una reseña
        btnValorar.setOnClickListener(v -> {

            // aqui coge el valor del RatingBar
            float valoracion = ratingBar.getRating();
            if (valoracion == 0) return;

            // Para luego conectar con la BDD de Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance(
                    "https://reviewsnegativas-ec0fc-default-rtdb.europe-west1.firebasedatabase.app"
            );

            // Y pilla referencia de los comentarios.
            DatabaseReference comentariosRef = database.getReference("Comentarios");

            // Y genera un ID para un nuevo comentario
            String comentarioId = comentariosRef.push().getKey();

            // HashMap para el comentario. Basicamente con la key siendo la ID del post
            // y el value el valor del RatingBar
            HashMap<String, Object> comentario = new HashMap<>();
            comentario.put("id", id);
            comentario.put("Valoracion", (int) valoracion);

            // Aqui hace un insert a la BDD de Firebase. En el caso de que
            // se haya podido hacer, entonces lo guarda tambien en la BDD local
            // de SQLite en la tabla de posts reseñados.
            comentariosRef.child(comentarioId).setValue(comentario)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Reseña guardada", Toast.LENGTH_SHORT).show();
                        BodrioDb dbBodrio = new BodrioDb(this);
                        SQLiteDatabase db = dbBodrio.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("id", id);
                        values.put("valoracion", (int) valoracion);
                        db.insertWithOnConflict(
                                "valorados",
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE
                        );
                        recalcularPromedio(id);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    });
            // Luego aqui se reproduce un sonido.
            mediaPlayer = MediaPlayer.create(this, R.raw.estrella);
            mediaPlayer.start();
        });
        // Boton que sirve para llamar a otras activities.
        btnCompartir.setOnClickListener(v -> {
            // Este es el texto que se enviara
            String textoCompartir = titulo + "\n\n" +
                    descripcion + "\n\n" +
                    contenido;

            // Este intent basicamente hara el action de Send para
            // enviar el texto a cualquier App que permita correos electronicos.
            // En este caso, me interesa entonces usar un type llamado rfc822, que es el
            // que se usa para enviar correos electronicos.
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            // Aqui le asigno los valores.
            intent.putExtra(Intent.EXTRA_EMAIL, textoCompartir);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Mira esta reseña");
            intent.putExtra(Intent.EXTRA_TEXT, titulo + "\n\n" + descripcion);
            // Y aqui es cuando empieza la activity para compartir.
            startActivity(Intent.createChooser(intent, "Compartir reseña"));
        });

    }
    private void recalcularPromedio(String id) {
        // Esta funcion sirve apra recalcular el promedio antes de subirlo a la Firebase
        // Ya que la Firebase no puede hacer calculos automaticos por si sola.
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://reviewsnegativas-ec0fc-default-rtdb.europe-west1.firebasedatabase.app"
        );
        // Pillo los comentarios
        DatabaseReference comentariosRef = database.getReference("Comentarios");
        DatabaseReference postRef = database.getReference("Posts").child(id);
        comentariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Suma es la suma de todos los ratings
                int suma = 0;
                // Y cantidad es la cantidad de reseñas totales.
                int cantidad = 0;
                for (DataSnapshot comentarioSnap : snapshot.getChildren()) {
                    // Basicamente aqui pillo el post por id
                    Object postIdObj = comentarioSnap.child("id").getValue();
                    // Y su valoracion en cuestion
                    Object valoracionObj = comentarioSnap.child("Valoracion").getValue();
                    String postIdFirebase = String.valueOf(postIdObj);
                    // Convierto la valoracion en un Int para luego sumarlo, y añado 1 a la cantidad.
                    int valoracion = Integer.parseInt(String.valueOf(valoracionObj));
                    if (postIdFirebase.equals(id)) {
                        suma += valoracion;
                        cantidad++;
                    }
                }
                // Aqui ya es cuando recalcula.
                if (cantidad > 0) {
                    double promedio = (double) suma / cantidad;
                    postRef.child("promedio").setValue(promedio);
                    postRef.child("cantidadValoraciones").setValue(cantidad);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
