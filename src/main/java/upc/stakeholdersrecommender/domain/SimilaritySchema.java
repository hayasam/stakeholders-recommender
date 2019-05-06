package upc.stakeholdersrecommender.domain;

import java.io.Serializable;

public class SimilaritySchema implements Serializable {

    String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
