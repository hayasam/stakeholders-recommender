package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;
import java.util.List;

public class BERTSchema implements Serializable {


    Integer id;
    List<String> texts;
    Boolean is_tokenized;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public Boolean getIs_tokenized() {
        return is_tokenized;
    }

    public void setIs_tokenized(Boolean is_tokenized) {
        this.is_tokenized = is_tokenized;
    }
}
