package upc.stakeholdersrecommender.domain.Schemas;

import upc.stakeholdersrecommender.domain.SimilaritySchema;

import java.io.Serializable;
import java.util.List;

public class CorpusSchema implements Serializable {

    List<String> corpus;

    public List<String> getCorpus() {
        return corpus;
    }

    public void setCorpus(List<String> thing) {
        this.corpus = thing;
    }

    public List<String> getThing() {
        return corpus;
    }
}
