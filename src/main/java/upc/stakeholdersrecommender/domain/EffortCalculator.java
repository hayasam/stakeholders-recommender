package upc.stakeholdersrecommender.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upc.stakeholdersrecommender.domain.Schemas.EffortCalculatorSchema;
import upc.stakeholdersrecommender.domain.Schemas.SetEffortSchema;
import upc.stakeholdersrecommender.entity.Effort;
import upc.stakeholdersrecommender.repository.EffortRepository;

import java.util.List;

@Service
public class EffortCalculator {

    @Autowired
    EffortRepository effortRepository;

    public void effortCalc(EffortCalculatorSchema eff, String id) {
        Effort aux=effortRepository.getOne(id);
        if (aux!=null) {
            effortRepository.delete(aux);
        }
        Integer total;
        Double[] effort=new Double[5];
        total = getTotal(eff.getOne(),1);
        effort[0]=total.doubleValue()/eff.getOne().size();

        total = getTotal(eff.getTwo(),2);
        effort[1]=total.doubleValue()/eff.getTwo().size();

        total = getTotal(eff.getThree(),3);
        effort[2]=total.doubleValue()/eff.getThree().size();

        total = getTotal(eff.getFour(),4);
        effort[3]=total.doubleValue()/eff.getFour().size();

        total = getTotal(eff.getFive(),5);
        effort[4]=total.doubleValue()/eff.getFive().size();

        Effort effortMap=new Effort();
        effortMap.setEffort(effort);
        effortMap.setId(id);
        effortRepository.save(effortMap);
    }

    public void setEffort(SetEffortSchema set,String id) {
        Effort aux=effortRepository.getOne(id);
        if (aux!=null) {
            effortRepository.delete(aux);
        }
        Double[] effort= new Double[5];
        effort[0]=set.getOne().doubleValue();
        effort[1]=set.getTwo().doubleValue();
        effort[2]=set.getThree().doubleValue();
        effort[3]=set.getFour().doubleValue();
        effort[4]=set.getFive().doubleValue();
        Effort effortMap=new Effort();
        effortMap.setId(id);
        effortRepository.save(effortMap);

    }

    private Integer getTotal(List<Integer> list, Integer def) {
        Integer total;
        total = 0;
        if (list==null || list.size()==0) total=def;
        else {
            for (Integer i : list) {
                total += i;
            }
        }
        return total;
    }

}
