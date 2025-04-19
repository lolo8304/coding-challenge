package timezone;

import java.util.*;

public class SortedList<E> extends AbstractList<E> {
    private final NavigableSet<E> set;

    public SortedList(Comparator<? super E> comparator) {
        this.set = new TreeSet<>(comparator);
    }

    @Override
    public boolean add(E element) {
        return set.add(element); // Fügt das Element hinzu und hält die Sortierung aufrecht
    }

    @Override
    public E get(int index) {
        return new ArrayList<>(set).get(index); // Konvertiert in eine Liste für den Indexzugriff
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return set.iterator();
    }
}