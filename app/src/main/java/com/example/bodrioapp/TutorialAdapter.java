package com.example.bodrioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.ViewHolder> {

    private int[] imagenes;

    public TutorialAdapter(int[] imagenes) {
        this.imagenes = imagenes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutorial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(imagenes[position]);
    }

    @Override
    public int getItemCount() {
        return imagenes.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageTutorial);
        }
    }
}
