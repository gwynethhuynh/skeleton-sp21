package deque;

import java.util.Iterator;

class ArrayDeque<T> {

    public int size;
    public int nextStart;
    public int nextEnd;
    private T[] items;
    private int capacity;

    //Creates an empty array deque.
    public ArrayDeque() {
        capacity = 8;
        items = (T []) new Object[capacity];
        size = 0;
        nextStart = 2; //arbitrary index
        nextEnd = 3; //arbitrary index

    }

    //resizes array deque
    private void resize() {
        T[] a = (T []) new Object[capacity * 2];
        int start = Math.floorMod(nextStart + 1, capacity);
        int end = Math.floorMod(nextEnd - 1, capacity);
        System.arraycopy(items, start , a, 0, size - start);
        System.arraycopy(items, 0, a, size - start, end + 1);
        capacity = capacity * 2;
    }

    //Adds an item of type T to the front of the deque.
    public void addFirst (T item) {
        //Resizes array deque.
        if (size == capacity) {
           resize();
        }
        items[nextStart] = item;
        nextStart = Math.floorMod(nextStart - 1, capacity); //floorMod
        size += 1;
    }

    //Adds an item of type T to the back of the deque.
    public void addLast(T item) {
        if (size == capacity) {
            resize();
        }
        items[nextEnd] = item;
        nextStart = Math.floorMod(nextEnd + 1, capacity); //floorMod
        size += 1;
    }

    //Returns true if the deque is empty, false otherwise.
    public boolean isEmpty() {
        return size == 0;
    }

    //Returns the number of items in the deque.
    public int size() {
        return size;
    }

    //Prints the items in the deque from first to last, separated by a space.
    // 2 4 6 8
    //Fix index offset.
    public void printDeque() {
        int start = Math.floorMod(nextStart + 1, capacity);
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                System.out.print(items[i]);
            } else {
                System.out.print(items[i] + " ");
            }
        }
    }

    //Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int start = Math.floorMod(nextStart + 1, capacity);
        nextStart = start;
        size -= 1;
        return items[start];
    }

    //Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int end = Math.floorMod(nextEnd - 1, capacity);
        nextEnd = end;
        size -= 1;
        return items[end];
    }

    //Gets the item at the given index. If no such item exists, returns null.
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        int curr = Math.floorMod(nextStart + index + 1, capacity);
        return items[curr];

    }

    //Returns an iterator that iterates over the deque objects.
    public Iterator<T> iterator() {
        System.out.println("NOT IMPLEMENTED");
        return null;
    }

    //Returns whether or not the parameter o is equal to the Deque (contains the same contents)
    public boolean equals(Object o) {
        System.out.println("NOT IMPLEMENTED");
        return false;
    }
}