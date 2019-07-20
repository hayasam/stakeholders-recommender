package upc.stakeholdersrecommender.domain.rilogging;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Log {
    private Body body;
    private Header header;
    private String event_type;

    public Log(){}

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }
}
