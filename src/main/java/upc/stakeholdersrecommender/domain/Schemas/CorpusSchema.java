package upc.stakeholdersrecommender.domain.Schemas;

import upc.stakeholdersrecommender.domain.SimilaritySchema;

import java.io.Serializable;
import java.util.List;

public class CorpusSchema implements Serializable {

    List<SimilaritySchema> corpus;

    public List<SimilaritySchema> getCorpus() {
        return corpus;
    }

    public void setCorpus(List<SimilaritySchema> thing) {
        this.corpus = thing;
    }

    public List<SimilaritySchema> getThing() {
        return corpus;
    }
}
