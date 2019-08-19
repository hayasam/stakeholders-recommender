package upc.stakeholdersrecommender.domain.rilogging;

public class Log {
    private Body body;
    private Header header;
    private String event_type;

    public Log() {
    }

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

    public Integer getUnixTime() {
        return body.getUnixTime();
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }


    public String getDescriptionOrName() {
        String toRet = "";
        if (body.getInnerText() != null && !body.getInnerText().equals("")) {
            toRet = body.getInnerText();
        } else if (body.getValue() != null && !body.getValue().equals("")) {
            toRet = body.getValue();
        }
        return toRet;
    }

    public boolean isDescription() {
        return body.getSrcElementclassName().equals("note-placeholder") || body.getSrcElementclassName().equals("note-editable or-description-active");
    }


    public boolean isName() {
        return body.getSrcElementclassName().equals("or-requirement-title form-control");
    }
}
