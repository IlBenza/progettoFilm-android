package it.alessandro.vendramini.applicazioneprogettofilm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.activities.DettaglioActivity;
import it.alessandro.vendramini.applicazioneprogettofilm.activities.FilmPreferitiActivity;
import it.alessandro.vendramini.applicazioneprogettofilm.activities.MainActivity;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.AggiungiPreferitoDialogFragment;
import it.alessandro.vendramini.applicazioneprogettofilm.util.Singleton;

public class FilmPreferitoAdapter extends RecyclerView.Adapter<FilmViewHolder> {

    public static final String httpsHead = "https://image.tmdb.org/t/p/w500/";

    private Context context;
    private FragmentManager fragmentManager;
    private List<Film> listaFilm = new ArrayList<>();
    private int itemSelezionato = -1;

    public FilmPreferitoAdapter(Context context) {
        this.context = context;
    }

    public void setListaFilm(List<Film> listaFilm) {
        this.listaFilm = listaFilm;
    }

    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Faccio l'nflate e assegno le variabili
        View convertView = LayoutInflater.from(context).inflate(R.layout.elenco_film_layout, parent, false);

        FilmViewHolder filmViewHolder = new FilmViewHolder(convertView);
        filmViewHolder.textView_nomeFilm = convertView.findViewById(R.id.textView_nomeFilm);
        filmViewHolder.imageView_fotoCopertina = convertView.findViewById(R.id.imageView_fotoCopertina);
        filmViewHolder.textView_dataRilascio = convertView.findViewById(R.id.textView_dataRilascio);
        filmViewHolder.textView_valutazione = convertView.findViewById(R.id.textView_valutazione);
        filmViewHolder.layout_singoloFilm = convertView.findViewById(R.id.layout_singoloFilm);

        fragmentManager = ((FilmPreferitiActivity) context).getSupportFragmentManager();

        return filmViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FilmViewHolder holder, final int position) {

        Film filmAttuale = listaFilm.get(position);

        holder.textView_nomeFilm.setText(filmAttuale.getTitolo());

        //Cambio immagine nel caso fosse vuota
        if (filmAttuale.getImmaginePrimoPoster() != null){
            Glide.with(context).load(httpsHead + filmAttuale.getImmaginePrimoPoster()).into(holder.imageView_fotoCopertina);
        } else {
            Glide.with(context).load(R.drawable.ic_no_image_512p).into(holder.imageView_fotoCopertina);
        }

        holder.textView_dataRilascio.setText(filmAttuale.getDataRilascio());

        if(filmAttuale.getValutazione() >= 7.0) {
            holder.textView_valutazione.setBackgroundResource(R.drawable.rounded_values_green);
        } else if (filmAttuale.getValutazione() >= 6.0) {
            holder.textView_valutazione.setBackgroundResource(R.drawable.rounded_values_yellow);
        } else if (filmAttuale.getValutazione() >= 0.1) {
            holder.textView_valutazione.setBackgroundResource(R.drawable.rounded_values_red);
        } else {
            holder.textView_valutazione.setBackgroundResource(R.drawable.rounded_values_black);
        }

        if(filmAttuale.getValutazione() != 10.0){
            holder.textView_valutazione.setText(filmAttuale.getValutazione().toString());
        } else {
            holder.textView_valutazione.setText("10.");
        }

        //Click sul singolo oggetto
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int itemPrecedente = itemSelezionato;
                itemSelezionato = position;

                notifyItemChanged(itemPrecedente);
                notifyItemChanged(position);

                Intent intent = new Intent(context, DettaglioActivity.class);
                intent.putExtra(Singleton.IMAGE_KEY, listaFilm.get(position).getImmagineSecondoPoster());
                intent.putExtra(Singleton.TITLE_KEY, listaFilm.get(position).getTitolo());
                intent.putExtra(Singleton.DESCRIPTION_KEY, listaFilm.get(position).getDescrizione());
                context.startActivity(intent);
            }
        });

        //Long click
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AggiungiPreferitoDialogFragment dialogFragment = new AggiungiPreferitoDialogFragment(listaFilm.get(position).getTitolo(), "Vuoi rimuovere questo film dalla sezione preferiti?️", listaFilm.get(position).getIdFilm());
                dialogFragment.show(fragmentManager, AggiungiPreferitoDialogFragment.class.getName());
                return true;
            }
        });
    }

    @Override
    public long getItemId(int position) {
        if (position >= listaFilm.size())
            return 0;
        return listaFilm.get(position).getIdFilm();
    }

    @Override
    public int getItemCount() {
        return listaFilm.size();
    }
}
