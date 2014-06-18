package dk.statsbiblioteket.doms.client.impl.util;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by csr on 6/17/14.
 */
public class CycleDetector {

    /**
     * Returns true if the end of a list of objects is cycling and has repeated more
     * than a given number of times. Examples:
     *
     *  {a b c d e f d e f}
     *
     *  will return true if called with repetitions=0 because it has a repeated once. With
     *  repetitions>0 it will return false.
     *
     * @param objects
     * @param repetitions
     * @return
     */
    public boolean isCycling(Object[] objects, int repetitions) {
        int totalObjects = objects.length;
        //
        // in repetitions = 0 then we are looking for a maximum cycle
        // length of totalObjects/2 etc .
        //
        int maxCycleLength = totalObjects/(repetitions + 2);
        List<Object[]> possibleCycles = new ArrayList<Object[]>();
        for (int cycleLength = 1; cycleLength <= maxCycleLength; cycleLength++ ) {
            for (int cycleNumber = 0; cycleNumber <= repetitions + 1; cycleNumber++) {
                int from = totalObjects - (cycleNumber+1)*cycleLength;
                Object[] possibleCycle = Arrays.copyOfRange(objects, from , from + cycleLength);
                possibleCycles.add(cycleNumber, possibleCycle);
            }
            boolean foundCycle = true;
            for (int cycleNumber = 0; cycleNumber <= repetitions; cycleNumber++) {
                foundCycle = foundCycle && Arrays.deepEquals(possibleCycles.get(cycleNumber), possibleCycles.get(cycleNumber+1));
            }
            if (foundCycle) {
                return true;
            }
        }
        return false;
    }


}
