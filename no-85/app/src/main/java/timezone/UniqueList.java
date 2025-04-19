package timezone;

import java.util.*;

public class UniqueList<E> extends AbstractList<E> {
    private final Set<E> set;
    private final List<E> list;

    public UniqueList(Comparator<? super E> sortable) {
        super();
        this.set = new LinkedHashSet<>();
        this.list = new SortedList<>(sortable);
    }
    public UniqueList() {
        super();
        this.set = new LinkedHashSet<>();
        this.list = new ArrayList<>();
    }

    @Override
    public boolean add(E element) {
        if (set.add(element)) { // Fügt nur hinzu, wenn es nicht bereits existiert
            list.add(element);
            return true;
        }
        return false;
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public E remove(int index) {
        E element = list.remove(index);
        set.remove(element);
        return element;
    }

    @Override
    public boolean remove(Object o) {
        if (set.remove(o)) {
            list.remove(o);
            return true;
        }
        return false;
    }
}