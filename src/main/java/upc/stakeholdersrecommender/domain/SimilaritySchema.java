package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class SimilaritySchema implements Serializable {

    String from;
    String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
