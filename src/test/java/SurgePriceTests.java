import gilded.rose.expands.utils.SurgePriceUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class SurgePriceTests {

    @Test
    public void verifySurgePriceCalculationTest() {
        // 10% Increase
        assertEquals(11, SurgePriceUtil.applySurgeMultiplier(10, 110));

        // 50% Increase
        assertEquals(225, SurgePriceUtil.applySurgeMultiplier(150, 150));

        // Price should double
        assertEquals(20, SurgePriceUtil.applySurgeMultiplier(10, 200));
    }
}
