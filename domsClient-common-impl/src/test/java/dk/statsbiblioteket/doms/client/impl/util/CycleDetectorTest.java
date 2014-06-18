package dk.statsbiblioteket.doms.client.impl.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class CycleDetectorTest {

    @Test
    public void testIsCycling() throws Exception {
        CycleDetector cycleDetector = new CycleDetector();
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        Object d = new Object();
        Object e = new Object();
        Object f = new Object();
        Object g = new Object();
        Object h = new Object();
        Object i = new Object();
        Object j = new Object();
        Object k = new Object();

        Object[] array1 = new Object[] {a,b,c,d,e,f,e,a,f,e,a};   //repeated once
        String[] arrays = new String[] {"a","b","c","d","e","f","e","a","f","e","a"};

        assertTrue(cycleDetector.isCycling(arrays, 0));
        assertTrue(cycleDetector.isCycling(array1, 0));
        assertFalse(cycleDetector.isCycling(array1, 1));

        Object[] array2 = new Object[] {a,b,c,a,b,d,i,j,k,d,i,j,k,d,i,j,k}; //repeated twice

        assertTrue(cycleDetector.isCycling(array2, 0));
        assertTrue(cycleDetector.isCycling(array2, 1));
        assertFalse(cycleDetector.isCycling(array2, 2));
        assertFalse(cycleDetector.isCycling(array2, 3));


    }
}
