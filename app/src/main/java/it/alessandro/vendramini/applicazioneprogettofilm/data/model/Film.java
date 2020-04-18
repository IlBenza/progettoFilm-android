package it.alessandro.vendramini.applicazioneprogettofilm.data.model;

import com.google.gson.annotations.SerializedName;

public class Film {

    @SerializedName("id")
    private long idFilm;
    @SerializedName("title")
    private String titolo;
    @SerializedName("poster_path")
    private String immaginePrimoPoster;
    @SerializedName("backdrop_path")
    private String immagineSecondoPoster;
    @SerializedName("vote_average")
    private Double valutazione;
    @SerializedName("release_date")
    private String dataRilascio;
    @SerializedName("overview")
    private String descrizione;

    public long getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(long idFilm) {
        this.idFilm = idFilm;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getImmaginePrimoPoster() {
        return "https://image.tmdb.org/t/p/w500/" + immaginePrimoPoster;
    }

    public void setImmaginePrimoPoster(String immaginePrimoPoster) {
        this.immaginePrimoPoster = immaginePrimoPoster;
    }

    public String getImmagineSecondoPoster() {
        return "https://image.tmdb.org/t/p/w500/" + immagineSecondoPoster;
    }

    public void setImmagineSecondoPoster(String immagineSecondoPoster) {
        this.immagineSecondoPoster = immagineSecondoPoster;
    }

    public Double getValutazione() {
        return valutazione;
    }

    public void setValutazione(Double valutazione) {
        this.valutazione = valutazione;
    }

    public String getDataRilascio() {
        return dataRilascio;
    }

    public void setDataRilascio(String dataRilascio) {
        this.dataRilascio = dataRilascio;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
