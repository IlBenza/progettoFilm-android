package it.alessandro.vendramini.applicazioneprogettofilm.data.model;

public class Results {

    private Film[] results;
    private int page;
    private int total_page;

    public Film[] getResults() {
        return results;
    }

    public int getPage() {
        return page;
    }

    public int getTotal_page() {
        return total_page;
    }
}
