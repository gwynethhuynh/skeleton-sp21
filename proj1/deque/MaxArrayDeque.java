package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator cmp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.cmp = c;
    }

    public T max() {
        return (T) max(this.cmp);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        } else {
            T max_item = get(0);
            for (int i = 1; i < size(); i++) {
                if (c.compare(max_item, get(i)) < 0) {
                    max_item = get(i);
                }
            }
            return max_item;
        }
    }

}
