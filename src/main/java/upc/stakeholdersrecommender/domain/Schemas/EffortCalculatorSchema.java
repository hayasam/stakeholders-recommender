package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;


@ApiModel(description = "Class representing the hours each effort took.")
public class EffortCalculatorSchema implements Serializable {
    @ApiModelProperty(notes = "List of hours for effort 1.", example = "1", required = true)
    public List<Integer> one;
    @ApiModelProperty(notes = "List of hours effort 2.", example = "2", required = true)
    public List<Integer> two;
    @ApiModelProperty(notes = "List of hours effort 3.", example = "3", required = true)
    public List<Integer> three;
    @ApiModelProperty(notes = "List of hours effort 4.", example = "4", required = true)
    public List<Integer> four;
    @ApiModelProperty(notes = "List of hours effort 5.", example = "5", required = true)
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
