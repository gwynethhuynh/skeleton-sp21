package deque;

import java.util.Iterator;

class LinkedListDeque<T> {

    private TNode sentinel;
    private T item;
    private int size;

    public class TNode {
        public T item;
        public TNode next;
        public TNode previous;

        public TNode(T i, TNode n, TNode p) {
            item = i;
            next = n;
            previous = p;
        }
    }

    //Creates an empty linked list deque.
    public LinkedListDeque() {
        sentinel = new TNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
        size = 0;

    }

    //Uses recursion to get item from linked list deque.
    public T getRecursive(int index) {
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, TNode tn) {
        if (index == 0) {
            return tn.item;
        }
        return getRecursiveHelper(index - 1, tn.next);

    }


    //Adds an item of type T to the front of the deque.
    public void addFirst (T item) {
        TNode first = new TNode(item, sentinel.next, sentinel);
        sentinel.next.previous = first;
        sentinel.next = first;
        size += 1;

    }

    //Adds an item of type T to the back of the deque.
    public void addLast(T item) {
        TNode last = new TNode(item, sentinel, sentinel.previous);
        sentinel.previous.next = last;
        sentinel.previous = last;
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
    public void printDeque() {
        TNode current = sentinel.next;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(current.item);
            current = current.next;
        }

    }

    //Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        TNode first = sentinel.next;
        TNode second = first.next;
        second.previous = sentinel;
        sentinel.next = second;
        size -= 1;
        return first.item;
    }

    //Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        TNode last = sentinel.previous;
        TNode penultimate = last.previous;
        penultimate.next = sentinel;
        sentinel.previous = penultimate;
        size -= 1;
        return last.item;
    }

    //Gets the item at the given index. If no such item exists, returns null.
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        TNode current = sentinel.next; // index 0
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;

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

    public static void main(String[] args) {
        LinkedListDeque<String> d = new LinkedListDeque<>();
        d.addFirst("Jasper");
        d.addFirst("Gwynie");
        d.addLast("Kai");
        d.printDeque();

    }
}