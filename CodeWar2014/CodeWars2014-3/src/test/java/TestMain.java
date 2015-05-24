import com.hp.ov.nmc.codewars.Main;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: kiniak
 * Date: 9/12/14
 * Time: 7:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestMain {

    @Test
    public void testFor20(){
        Main obj = new Main();
        Assert.assertEquals("", Arrays.toString(obj.findAllCombinationsOfNIntoThreeBins(20)));
    }

    @Test
    public void testFor10(){
        Main obj = new Main();
        Assert.assertEquals("", Arrays.toString(obj.findAllCombinationsOfNIntoThreeBins(10)));
    }

    @Test
    public void testFor5(){
        Main obj = new Main();
        Assert.assertEquals("", Arrays.toString(obj.findAllCombinationsOfNIntoThreeBins(5)));
    }

    @Test
    public void testForMaxInt(){
        Main obj = new Main();
        Assert.assertEquals("", Arrays.toString(obj.findAllCombinationsOfNIntoThreeBins(Integer.MAX_VALUE )));
    }

    /*@Test
    public void testAllOptimizationTill25(){
        Main obj = new Main();
        for(int i=1 ; i <= Integer.MAX_VALUE ; i++){
            if(!Arrays.equals(obj.findAllCombinationsOfNIntoThreeBins(i), obj.findAllCombinationsOfNIntoThreeBinsOptimized(i))){
                System.out.println("Missed: " + i);
            }
        }
    }*/
}
