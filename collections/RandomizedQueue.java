import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private final int initialCapacity = 8; // start at a reasonable size
    private Item[] items;
    private int count;

    // construct an empty randomized queue
    public RandomizedQueue() {
        items = (Item[]) new Object[initialCapacity];
        count = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return count;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item to enqueue cannot be null.");
        }

        // resize when needed
        if (count == items.length) {
            resize(2 * items.length);
        }

        // add the item
        items[count++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        throwIfEmpty();

        Item[] itemsCopy = copy(items); // we don't the padded nulls to be shuffled so create a copy
        StdRandom.shuffle(itemsCopy); // shuffle the copy then ...
        items = copy(itemsCopy); // re-copy back to original

        // remove the item
        Item item = items[--count];
        items[count] = null;

        // resize when needed
        if (count > 0 && count == items.length / 4) {
            resize(items.length / 2);
        }

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        throwIfEmpty();
        return items[StdRandom.uniformInt(count)];
    }

    private void throwIfEmpty() {
        if (count == 0) {
            throw new NoSuchElementException("No items in queue");
        }
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < count; i++) {
            copy[i] = items[i];
        }
        items = copy;
    }

    // create a copy of items with no extra elements
    private Item[] copy(Item[] source) {
        Item[] copy = (Item[]) new Object[count];
        for (int i = 0; i < count; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private Item[] itemsCopy;
        private Item current;
        private int position;
        private int copyCount;

        public RandomizedQueueIterator() {
            if (count == 0) {
                current = null;
                return;
            }
            itemsCopy = copy(items); // copy the items so we can independently shuffle them
            copyCount = count; // close over count as we copied the original items
            StdRandom.shuffle(itemsCopy); // shuffle the items so we can randomly iterate over them

            position = 0;
            current = itemsCopy[position];
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (current == null) {
                throw new NoSuchElementException("No items to iterate.");
            }

            Item item = current;
            position++;
            current = position == copyCount ? null : itemsCopy[position];
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        assert queue.isEmpty();
        assert queue.size() == 0;

        int count = 0;
        for (Integer val : queue) {
            count++;
        }
        assert count == 0 : "Iterating over empty collection should not increment count";

        Integer[] testValues = { 0, 1, 2, 3, 4 };
        for (Integer val : testValues) {
            queue.enqueue(val);
        }
        assert !queue.isEmpty();
        assert queue.size() == 5;

        for (Integer val : queue) {
            assert intInArray(val, testValues);
        }
        assert queue.size() == 5; // iterating does not change size of collection

        Integer sample;
        for (int i = 0; i < 20; i++) {
            sample = queue.sample();
            assert queue.size() == 5 : "Sample should not remove item.";
            assert intInArray(sample, testValues) : "Sample should return a test value.";
        }

        count = queue.size();
        for (int i = 0; i < count; i++) {
            Integer dequedVal = queue.dequeue();
            assert intInArray(dequedVal, testValues);
        }
        assert queue.size() == 0;
    }

    private static boolean intInArray(Integer searchValue, Integer[] values) {
        for (Integer val : values) {
            if (searchValue.equals(val)) {
                return true;
            }
        }
        return false;
    }
}