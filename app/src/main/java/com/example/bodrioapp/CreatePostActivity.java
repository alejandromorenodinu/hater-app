package com.example.bodrioapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePostActivity extends AppCompatActivity {
    // Esta clase sirve para crear nuevos Posts en el Firebase
    private EditText edtTitulo, edtDescripcion, edtContenido;
    private Button btnPublicar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtContenido = findViewById(R.id.edtContenido);
        btnPublicar = findViewById(R.id.btnPublicar);

        btnPublicar.setOnClickListener(v -> publicarPost());
    }

    private void publicarPost() {

        // Aqui pilla los valores de titulo, descripcion y contenido de los TextViews
        String titulo = edtTitulo.getText().toString().trim();
        String descripcion = edtDescripcion.getText().toString().trim();
        String contenido = edtContenido.getText().toString().trim();

        // Aqui lanza un Toast para señalizar si hay un error (tipo que falte algun campo)
        // o si se ha publicado la reseña correctamente
        if (titulo.isEmpty() || descripcion.isEmpty() || contenido.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "Se ha publicado tu reseña", Toast.LENGTH_LONG).show();
        }

        // Aqui pilla la Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://reviewsnegativas-ec0fc-default-rtdb.europe-west1.firebasedatabase.app"
        );

        // Aqui pilla los Posts
        DatabaseReference postsRef = database.getReference("Posts");
        // Aqui genera un id para el post
        String postId = postsRef.push().getKey();
        // Para luego crear un nuevo post
        Post post = new Post(titulo, descripcion, contenido, 0);
        // Y asi subirlo a la Firebase con la id generada y el post creado.
        postsRef.child(postId).setValue(post)
                // Se sube en el caso de que no haya error
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Post publicado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                // Saltara error si, por ejemplo, la Firebase esta offline (me ha pasado por lo de los 30 dias esos)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al publicar", Toast.LENGTH_SHORT).show()
                );
    }
}
