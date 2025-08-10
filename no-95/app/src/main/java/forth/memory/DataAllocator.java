package forth.memory;

/** Minimal dictionary allocator for DATA space. */
public final class DataAllocator {
    private final Address.Segment segment;
    private long here;          // byte offset within DATA space
    private final int cellSize; // e.g., 8 on 64-bit
    public DataAllocator(Address.Segment segment, long start, int cellSize) {
        this.segment = segment;
        this.here = start;
        this.cellSize = cellSize;
    }
    public DataAllocator(Address.Segment segment, long start) {
        this.segment = segment;
        this.here = start;
        this.cellSize = 1;
    }

    /** Align HERE to k bytes (k power-of-two). */
    public long alignHere(int k) {
        if (k == 1) return here; // no alignment needed
        if (k < 1 || (k & (k - 1)) != 0) throw new IllegalArgumentException("k must be power of two");
        here = (here + (k - 1)) & -k;
        return here;
    }
    /** Ensure cell alignment, then allot n bytes and return the start address. */
    public Address allot(int n) {
        alignHere(cellSize); // rule 2: created data is cell-aligned
        long start = here;
        here = Math.addExact(here, n);
        return this.segment == Address.Segment.DATA ? Address.data(start, cellSize) : Address.literal(start, n);   // immutable, segment-relative
    }
    public long here() { return here; }
}
