package upc.stakeholdersrecommender.domain.rilogging;
import java.util.*;

public class LogArray {
    private List<Log> logs;

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public void log() {
        Map<String,List<Log>> logged=new HashMap<>();
        for (Log l:this.logs) {
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
    }
}
