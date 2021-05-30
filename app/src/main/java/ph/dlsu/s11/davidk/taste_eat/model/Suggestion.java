package ph.dlsu.s11.davidk.taste_eat.model;

import java.io.Serializable;

public class Suggestion implements Serializable {

    private String id;
    private String suggestor;
    private String date;
    private String suggestion;

    public Suggestion(){

    }

    public Suggestion(String id, String suggestor, String date, String suggestion){
        this.id = id;
        this.suggestor = suggestor;
        this.date = date;
        this.suggestion = suggestion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSuggestor() {
        return suggestor;
    }

    public void setSuggestor(String suggestor) {
        this.suggestor = suggestor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
