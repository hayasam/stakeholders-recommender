package upc.stakeholdersrecommender.domain.rilogging;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

public class Body {

    private String value;
    @JsonDeserialize(using=StupidValueDeserializer.class)
    private String srcElementclassName;
    private String username;
    private String userId;
    private String requirementId;
    private String timestamp;
    private String innerText;
    private String projectId;

    public Body(){}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSrcElementclassName() {
        return srcElementclassName;
    }

    public void setSrcElementclassName(String srcElementclassName) {
        this.srcElementclassName = srcElementclassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getInnerText() {
        return innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }

    static class StupidValueDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken jsonToken = p.getCurrentToken();
            if (jsonToken == JsonToken.VALUE_STRING) {
                return p.getValueAsString();
            }
            return null;
        }
        public StupidValueDeserializer(){
            super();
        }
    }
}
