package upc.stakeholdersrecommender.domain.Schemas;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KeywordReturnSchemaTest {

    @Test
    public void setRequirementTest() {
        System.out.println("requirementTest");
        KeywordReturnSchema instance = new KeywordReturnSchema();
        instance.setRequirement("1");
        assertTrue(instance.getRequirement().equals("1"));
    }

    @Test
    public void setSkillsTest() {
        System.out.println("setSkillsTest");
        KeywordReturnSchema instance = new KeywordReturnSchema();
        List<String> skill = new ArrayList<>();
        instance.setSkills(skill);
        assertEquals(skill, instance.getSkills());
    }


}
