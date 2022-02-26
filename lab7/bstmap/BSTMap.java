package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
    private BSTNode<K, V> root;
    private int size;
    public BSTMap() {
        root = null;
        this.size = 0;
    }

    private class BSTNode<K extends Comparable, V> {
        public K key;
        public V value;
        public BSTNode left;
        public BSTNode right;
        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return containsKeyRec(key, root);
    }

    private boolean containsKeyRec(K key, BSTNode node) {
        if (node == null) {
            return false;
        }
        if (node.key.equals(key)) {
            return true;
        } else {
            if (node.key.compareTo(key) < 0) {
                return containsKeyRec(key, node.left);
            } else {
                return containsKeyRec(key, node.right);
            }
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getRec(key, root);
    }

    private V getRec(K key, BSTNode node) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return (V) node.value;
        } else {
            if (node.key.compareTo(key) < 0) {
                return (V) getRec(key, node.left);
            } else {
                return (V) getRec(key, node.right);
            }
        }
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return this.size;
    }

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        this.size += 1;
        root = this.putRec(key, value, root);
    }

    public BSTNode putRec(K key, V value, BSTNode node) {
        BSTNode<K, V> newNode = new BSTNode(key, value);
        if (node == null) {
            node = newNode;
        } else {
            if (node.key.compareTo(key) < 0) {
                node.left = putRec(key, value, node.left);
            } else {
                node.right = putRec(key, value, node.right);
            }
        }
        return node;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        if (this.containsKey(key)) {
            V value = this.get(key);
            root = removeRec(key, this.root);
            return value;
        } else {
            return null;
        }
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        V val = this.get(key);
        if (val == null) {
            return null;
        } else {
            root = removeRec(key, this.root);
            return val;
        }
    }

    private BSTNode removeRec(K key, BSTNode node) {
        if (node.key.equals(key)) {
            //if it's a leaf
            if (node.left == null && node.right == null) {
                node = null;
                //1 child
            } else if {
                //1 child
                BSTNode lowNode;
                if (node.left == null) {
                    lowNode = node.right;
                } else {
                    lowNode = node.left;
                }
                node = lowNode;
            //2 children
            }


        }
        return node;
    }

    public V removeLeaf(K key, V value, BSTNode node) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return (V) node.value;
        } else {
            if (node.key.compareTo(key) < 0) {
                return (V) getRec(key, node.left);
            } else {
                return (V) getRec(key, node.right);
            }
        }
    }

    public

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /* Prints out BSTMap in order of increasing Key */
    public void printInOrder() {
        throw new UnsupportedOperationException();
    }
}
