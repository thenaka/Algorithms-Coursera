import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node<Item> first;
    private Node<Item> last;
    private int count;

    // construct an empty deque
    public Deque() {
        count = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // return the number of items on the deque
    public int size() {
        return count;
    }

    // add the item to the front
    public void addFirst(Item item) {
        throwIfNull(item);

        Node<Item> temp = new Node<Item>(item);
        if (count == 0) {
            first = temp;
            last = temp;
        } else {
            temp.next = first;
            first.prev = temp;
            first = temp;
        }
        count++;
    }

    // add the item to the back
    public void addLast(Item item) {
        throwIfNull(item);

        Node<Item> temp = new Node<Item>(item);
        if (count == 0) {
            first = temp;
            last = temp;
        } else {
            temp.prev = last;
            last.next = temp;
            last = temp;
        }
        count++;
    }

    private void throwIfNull(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null item");
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {
        throwIfEmpty();
        Node<Item> temp = first;
        if (count == 1) {
            first = null;
            last = null;
        } else {
            first = first.next;
            first.prev = null;
        }
        count--;

        return temp.value;
    }

    // remove and return the item from the back
    public Item removeLast() {
        throwIfEmpty();
        Node<Item> temp = last;
        if (count == 1) {
            first = null;
            last = null;
        } else {
            last = last.prev;
            last.next = null;
        }
        count--;
        return temp.value;
    }

    private void throwIfEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot remove an item from an empty deque");
        }
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node<Item> current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (current == null) {
                throw new NoSuchElementException("No next element.");
            }

            Item item = current.value;
            current = current.next;
            return item;
        }
    }

    private class Node<Item> {
        Item value;
        Node<Item> next;
        Node<Item> prev;

        public Node(Item item) {
            value = item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        assert deque.isEmpty();
        assert deque.count == 0;

        deque.addFirst(4);
        assert !deque.isEmpty();
        assert deque.count == 1;

        deque.addFirst(3);
        assert deque.count == 2;

        deque.addFirst(2);
        assert deque.count == 3;

        deque.addFirst(1);
        assert deque.count == 4;

        deque.addFirst(0);
        assert deque.count == 5;

        deque.addLast(5);
        assert deque.count == 6;

        deque.addLast(6);
        assert deque.count == 7;

        Integer correctVal = 0;
        for (Integer val : deque) {
            assert val.equals(correctVal);
            correctVal++;
        }

        Integer last = deque.removeLast();
        assert last == 6;
        assert deque.count == 6;

        Integer first = deque.removeFirst();
        assert first == 0;
        assert deque.count == 5;

        correctVal = 1; // 0 was removed so start at 1
        for (Integer val : deque) {
            assert val.equals(correctVal);
            correctVal++;
        }

        last = deque.removeLast();
        assert last == 5;
        assert deque.count == 4;

        first = deque.removeFirst();
        assert first == 1;
        assert deque.count == 3;

        last = deque.removeLast();
        assert last == 4;
        assert deque.count == 2;

        last = deque.removeLast();
        assert last == 3;
        assert deque.count == 1;

        first = deque.removeFirst();
        assert first == 2;
        assert deque.count == 0;
    }
}