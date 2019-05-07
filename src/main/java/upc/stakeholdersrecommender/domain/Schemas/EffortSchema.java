package upc.stakeholdersrecommender.domain.Schemas;

import java.io.Serializable;
import java.util.List;

public class EffortSchema implements Serializable {

    public List<Integer> one;
    public List<Integer> two;
    public List<Integer> three;
    public List<Integer> four;
    public List<Integer> five;

    public List<Integer> getOne() {
        return one;
    }

    public void setOne(List<Integer> one) {
        this.one = one;
    }

    public List<Integer> getTwo() {
        return two;
    }

    public void setTwo(List<Integer> two) {
        this.two = two;
    }

    public List<Integer> getThree() {
        return three;
    }

    public void setThree(List<Integer> three) {
        this.three = three;
    }

    public List<Integer> getFour() {
        return four;
    }

    public void setFour(List<Integer> four) {
        this.four = four;
    }

    public List<Integer> getFive() {
        return five;
    }

    public void setFive(List<Integer> five) {
        this.five = five;
    }
}
