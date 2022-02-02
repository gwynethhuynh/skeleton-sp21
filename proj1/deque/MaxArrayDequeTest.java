package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;


public class MaxArrayDequeTest {

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    public static Comparator<String> getStrComparator() {
        return new StringComparator();
    }

    public static class Cat {
        public int size;
        public String name;

        public Cat(String name,int size) {
            this.name = name;
            this.size = size;
        }

        public static class SizeComparator implements Comparator<Cat> {
            @Override
            public int compare(Cat a, Cat b) {
                return a.size - b.size;
            }
        }

        public static class NameComparator implements Comparator<Cat> {
            @Override
            public int compare(Cat a, Cat b) {
                return a.name.compareTo(b.name);
            }
        }

    }

    public static Comparator<Cat> getSizeComparator(){
        return new Cat.SizeComparator();
    }

    public static Comparator<Cat> getNameComparator(){
        return new Cat.NameComparator();
    }



    @Test
    public void MaxNameTest() {
        MaxArrayDeque<String> mad = new MaxArrayDeque(getStrComparator());
        mad.addLast("kai");
        mad.addLast("gwynie");
        mad.addLast("apple");
        mad.addLast("jasper");
        assertEquals("kai", mad.max());
    }

    @Test
    public void CatTest() {
        MaxArrayDeque<Cat> mad1 = new MaxArrayDeque(getSizeComparator());
        mad1.addLast(new Cat("Kai", 101));
        mad1.addLast(new Cat("gwynie", 99));
        mad1.addLast(new Cat("zasper", 10));
        assertEquals("Kai", mad1.max().name);
        assertEquals("zasper", mad1.max(getNameComparator()).name);
    }

}
