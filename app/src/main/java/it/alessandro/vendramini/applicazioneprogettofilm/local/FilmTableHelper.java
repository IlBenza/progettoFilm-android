package it.alessandro.vendramini.applicazioneprogettofilm.local;

import android.provider.BaseColumns;

public class FilmTableHelper implements BaseColumns {
    public static final String TABLE_NAME = "film";
    public static final String ID_FILM = "id_film";
    public static final String TITOLO = "titolo";
    public static final String VALUTAZIONE = "valutazione";
    public static final String DATA_RILASCIO = "data_rilascio";
    public static final String DESCRIZIONE = "descrizione";

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            ID_FILM + " INTEGER PRIMARY KEY , " +
            TITOLO + " TEXT , " +
            VALUTAZIONE + " FLOAT , " +
            DATA_RILASCIO + " TEXT , " +
            DESCRIZIONE + " TEXT ) ;";
}
