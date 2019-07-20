package upc.stakeholdersrecommender.domain.rilogging;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class LogArray {
    private List<Log> logs;

    public LogArray(){}

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void log() throws JsonProcessingException {
        Map<String,List<Log>> logged=new HashMap<>();
        for (Log l:this.logs) {
            if (l.getBody()!=null)
            if (!logged.containsKey(l.getBody().getUsername())) {
                ArrayList<Log> list=new ArrayList<>();
                list.add(l);
                logged.put(l.getBody().getUsername(),list);
            }
            else {
                List<Log> list=logged.get(l.getBody().getUsername());
                list.add(l);
                logged.put(l.getBody().getUsername(),list);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        for (List<Log> l:logged.values()) {
            String res = mapper.writeValueAsString(l);
            System.out.println(res);
        }
    }
}
