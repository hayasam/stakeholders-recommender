package upc.stakeholdersrecommender.domain.Schemas;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OutputKeywordExtraction implements Serializable {

    List<ExtractedRequirement> list = new ArrayList<ExtractedRequirement>();

    public List<ExtractedRequirement> getList() {
        return list;
    }

    public void setList(List<ExtractedRequirement> list) {
        this.list = list;
    }

    public void addExtractedRequirement(ExtractedRequirement keys) {
        list.add(keys);
    }
}
