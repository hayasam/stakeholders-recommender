package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;
import java.util.List;

public class BERTResult implements Serializable {


    Integer id;
    List<List<Double>> results;
    Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<List<Double>> getResults() {
        return results;
    }

    public void setResults(List<List<Double>> resuls) {
        this.results = resuls;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
