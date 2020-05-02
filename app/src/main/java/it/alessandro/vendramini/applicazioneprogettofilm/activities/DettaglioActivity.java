package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.util.Singleton;

public class DettaglioActivity extends AppCompatActivity {

    private ImageView imageView_fotoCopertinaDue;
    private TextView textView_titolo;
    private TextView getTextView_descrizione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio);

        imageView_fotoCopertinaDue = findViewById(R.id.imageView_fotoCopertinaDue);
        textView_titolo = findViewById(R.id.textView_titolo);
        getTextView_descrizione = findViewById(R.id.textView_descrizione);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra(Singleton.IMAGE_KEY) && getIntent().hasExtra(Singleton.TITLE_KEY) && getIntent().hasExtra(Singleton.DESCRIPTION_KEY)){
            String testo_fotoCopertinaDue = getIntent().getStringExtra(Singleton.IMAGE_KEY);
            String testo_titolo = getIntent().getStringExtra(Singleton.TITLE_KEY);
            String testo_descrizione = getIntent().getStringExtra(Singleton.DESCRIPTION_KEY);

            Log.d(Singleton.LOG_TAG, "Ecco la scritta:"+testo_fotoCopertinaDue);

            //Cambio immagine nel caso fosse vuota
            if (!testo_fotoCopertinaDue.equals("https://image.tmdb.org/t/p/w500/null")){
                Glide.with(this).load(testo_fotoCopertinaDue).into(imageView_fotoCopertinaDue);
            } else {
                Log.d(Singleton.LOG_TAG, "SONO QUI");
                Glide.with(this).load(R.drawable.ic_no_image_512p).into(imageView_fotoCopertinaDue);
            }

            textView_titolo.setText(testo_titolo);

            if(testo_descrizione.equals("")){
                getTextView_descrizione.setText("Non è presente alcuna descrizione del film: " + testo_titolo);
            } else {
                getTextView_descrizione.setText(testo_descrizione);
            }

        }
    }

}
