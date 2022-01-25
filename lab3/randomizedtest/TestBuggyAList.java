package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */

public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> bug = new BuggyAList<>();
        AListNoResizing<Integer> a1 = new AListNoResizing<>();
        bug.addLast(4);
        a1.addLast(4);
        bug.addLast(5);
        a1.addLast(5);
        bug.addLast(6);
        a1.addLast(6);

        assertEquals(bug.size(), a1.size());
        assertEquals(bug.removeLast(), a1.removeLast());
        assertEquals(bug.removeLast(), a1.removeLast());
        assertEquals(bug.removeLast(), a1.removeLast());
    }

    @Test
    public void randomizedTest() {
        for (int i = 0; i < 100; i++) {
            randomizedTest2();
        }
    }

    public void randomizedTest2() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");

            } else if (operationNumber == 1) {
                //size
                int size = correct.size();
                int bug_size = broken.size();
                System.out.println("size: " + size + " bug_size: " + bug_size);
                assertEquals(size, bug_size);
            } else if (operationNumber == 2) {
                //getLast
                if (correct.size() == 0) continue;
                int last = correct.getLast();
                int bug_last = broken.getLast();
                System.out.println("last: " + last + " bug_last: " + bug_last);
                assertEquals(last, bug_last);

            } else if (operationNumber == 3) {
                //removeLast
                if (correct.size() == 0) continue;
                int last = correct.removeLast();
                int bug_last = broken.removeLast();
                System.out.println("removed " + last + " removed from buggy " + bug_last);
                assertEquals(last, bug_last);
            }



        }
    }
}
