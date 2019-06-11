package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;

public class BatchReturnSchema implements Serializable {
    private Integer processed_items;


    public BatchReturnSchema(Integer res) {
        this.processed_items = res;
    }

    public Integer getProcessed_items() {
        return processed_items;
    }

    public void setProcessed_items(Integer processed_items) {
        this.processed_items = processed_items;
    }
}
