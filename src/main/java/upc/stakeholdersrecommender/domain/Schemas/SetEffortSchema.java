package upc.stakeholdersrecommender.domain.Schemas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Class representing the mapping between effort and hours that is to be set.")
public class SetEffortSchema implements Serializable {
    @ApiModelProperty(notes = "Hours for effort 1.", example = "[1,1,1,1]", required = true)
    public Integer one;
    @ApiModelProperty(notes = "Hours for effort 2.", example = "[2,2,2,2]", required = true)
    public Integer two;
    @ApiModelProperty(notes = "Hours for effort 3.", example = "[3,3,3,3]", required = true)
    public Integer three;
    @ApiModelProperty(notes = "Hours for effort 4.", example = "[4,4,4,4]", required = true)
    public Integer four;
    @ApiModelProperty(notes = "Hours for effort 5.", example = "[5,5,5,5]", required = true)
    public Integer five;

    public Integer getOne() {
        return one;
    }

    public void setOne(Integer one) {
        this.one = one;
    }

    public Integer getTwo() {
        return two;
    }

    public void setTwo(Integer two) {
        this.two = two;
    }

    public Integer getThree() {
        return three;
    }

    public void setThree(Integer three) {
        this.three = three;
    }

    public Integer getFour() {
        return four;
    }

    public void setFour(Integer four) {
        this.four = four;
    }

    public Integer getFive() {
        return five;
    }

    public void setFive(Integer five) {
        this.five = five;
    }
}
