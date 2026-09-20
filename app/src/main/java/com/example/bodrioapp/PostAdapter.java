package com.example.bodrioapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Este es el Adapter de los Posts.

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> listaPosts;
    private Context context;

    public PostAdapter(ArrayList<Post> listaPosts, Context context) {
        this.listaPosts = listaPosts;
        this.context = context;
    }

    // Aqui hace un Inflate con los posts.
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = listaPosts.get(position);

        holder.txtTitulo.setText(post.titulo);
        holder.txtDescripcion.setText(post.descripcionCorta);
        holder.estrellas.setText(String.format("%.2f", post.promedio));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("id", post.id);
            intent.putExtra("titulo", post.titulo);
            intent.putExtra("descripcion", post.descripcionCorta);
            intent.putExtra("contenido", post.contenido);
            intent.putExtra("promedio", post.promedio);

            context.startActivity(intent);
        });
    }
    // Aqui actualiza la lista con la lista que recibe. Se usa en el MainActivity.
    public void actualizarLista(ArrayList<Post> nuevaLista) {
        listaPosts = nuevaLista;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaPosts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo, txtDescripcion, estrellas;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            estrellas = itemView.findViewById(R.id.estrellas);
        }
    }
}
