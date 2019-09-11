package upc.stakeholdersrecommender.domain.Schemas;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchReturnSchemaTest {

    @Test
    public void createTest() {
        System.out.println("createTest");
        BatchReturnSchema instance = new BatchReturnSchema(1);
        assertTrue(1 == instance.getProcessed_items());
    }

    @Test
    public void testSetProcessedItems() {
        System.out.println("testGetProcessedItems");
        BatchReturnSchema instance = new BatchReturnSchema(1);
        instance.setProcessed_items(3);
        assertTrue(3 == instance.getProcessed_items());
    }


}
