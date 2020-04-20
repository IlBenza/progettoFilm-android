package it.alessandro.vendramini.applicazioneprogettofilm.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class FilmViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_nomeFilm;
    public ImageView imageView_fotoCopertina;
    public TextView textView_valutazione;
    public TextView textView_dataRilascio;
    public ConstraintLayout layout_singoloFilm;

    public FilmViewHolder(@NonNull View itemView) {
        super(itemView);

    }
}
