package upc.stakeholdersrecommender.domain.rilogging;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import upc.stakeholdersrecommender.domain.Requirement;

import java.util.*;

public class LogArray {
    /*

     * Per cada skill: 60% reqs assignats, 30% reqs editas, 10% visualitzats*
     * Per reqs que realment s’han modificat (un attribut del req o el seu text), computar skills reqs, computar temps que s’ha usar per modificar-lo, i afegir skills tenint en compte que:
      * pes_skill al req tenint en compte el temps en que vas editar, pes_skill per tots els reqs que s’han modificat amb aquella skill, temps_edició.*
      *
      * Per reqs que només s’han consultat, computar skills reqs, computar temps que s’ha usar per veure’l, i afegir skills tenint en compte que:
       * pes_skill al req tenint en compte el temps en que vas visualitzar, pes_skill per tots els reqs que s’han visualitzat amb aquella skill, temps_visualització.
       * Considerem com a visualitzats els reqs no editats.
     */


    private List<Log> logs;

    public LogArray(){}

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }


}
