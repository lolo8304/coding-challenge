package forth.memory;

import java.util.Objects;

/**
 * Address: small, immutable, segment-relative, measured in BYTES.
 * <p>
 * Rules honored:
 * 1) Byte-based addresses + CHAR+/CELL+ helpers.
 * 2) Created DATA is cell-aligned; provide ALIGNED and allocator-side alignHere(k).
 * 3) Literal strings live in a read-only pool and push (c-addr u); S" via SLITERAL.
 * 4) Immutable value; segment-relative so segments can relocate.
 * 5) No enforcement here; mutability/alignment are enforced in the MemorySpace.
 *
 * @param segment     which memory space
 * @param offsetBytes byte offset within that space (0..2^56-1)
 * @param alignBytes  required alignment for safe access (power of two, >=1)
 * @param lengthBytes optional: when this denotes a buffer (e.g., S")
 */
public record Address(Segment segment, long offsetBytes, int alignBytes, Integer lengthBytes) {
    // ---------------------------------------------------------------------------
    // Segments: at minimum DATA and LITERAL; add CODE/etc. if you need.
    // ---------------------------------------------------------------------------
    public enum Segment {DATA, LITERAL}

    // Packing layout: [seg:8 bits][offset:56 bits]
    public static final int SEGMENT_BITS = 8;
    private static final int OFFSET_BITS = Long.SIZE - SEGMENT_BITS; // 56
    private static final long OFFSET_MASK = (1L << OFFSET_BITS) - 1;

    public Address {
        if (segment == null) throw new NullPointerException("segment");
        if (offsetBytes < 0 || (offsetBytes & ~OFFSET_MASK) != 0)
            throw new IllegalArgumentException("offset out of range");
        if (alignBytes < 1 || (alignBytes & (alignBytes - 1)) != 0)
            throw new IllegalArgumentException("align must be power of two");
        if (lengthBytes != null && lengthBytes < 0) throw new IllegalArgumentException("length must be >= 0");
    }

    // ----------------------------------------------------------------------------
    // Factories
    // ----------------------------------------------------------------------------

    /**
     * DATA-space address. Created objects are cell-aligned.
     */
    public static Address data(long offsetBytes, int cellSize) {
        return new Address(Segment.DATA, offsetBytes, cellSize, null);
    }

    /**
     * LITERAL-space address. Strings are byte-aligned; carry length for (c-addr u).
     */
    public static Address literal(long offsetBytes, Integer lengthBytes) {
        return new Address(Segment.LITERAL, offsetBytes, 1, lengthBytes);
    }

    /**
     * General factory (use sparingly).
     */
    public static Address of(Segment seg, long offsetBytes, int alignBytes, Integer lengthBytes) {
        return new Address(seg, offsetBytes, alignBytes, lengthBytes);
    }

    public int index() {
        return (int) offsetBytes; // LITERAL space is also byte-addressable
    }
    public int index(int inc) {
        return (int) offsetBytes + inc; // LITERAL space is also byte-addressable
    }

    // ----------------------------------------------------------------------------
    // Forth-style helpers (immutably return new Address)
    // ----------------------------------------------------------------------------

    /**
     * CHAR+ (n chars) — byte step.
     */
    public Address charPlus(long n) {
        return withOffset(Math.addExact(offsetBytes, n));
    }

    /**
     * CHARS (n) — convert char count to bytes (same in this VM).
     */
    public static long chars(long n) {
        return n;
    }

    /**
     * CELL+ (n cells).
     */
    public Address cellPlus(int cellSize, long nCells) {
        return withOffset(Math.addExact(offsetBytes, Math.multiplyExact(nCells, (long) cellSize)));
    }

    /**
     * CELLS (n) — convert cell count to bytes.
     */
    public static long cells(int cellSize, long n) {
        return Math.multiplyExact(n, (long) cellSize);
    }

    /**
     * ALIGNED — round this address up to k-byte alignment (k power-of-two).
     */
    public Address aligned(int k) {
        if (k < 1 || (k & (k - 1)) != 0) throw new IllegalArgumentException("k must be power of two");
        long a = (offsetBytes + (k - 1)) & -k;
        return new Address(segment, a, Math.max(alignBytes, k), lengthBytes);
    }

    // ----------------------------------------------------------------------------
    // Pack/unpack to a single 64-bit value for the Forth data stack
    // ----------------------------------------------------------------------------
    public long toLong() {
        int segId = segment.ordinal(); // fits in 8 bits
        return ((long) segId << OFFSET_BITS) | (offsetBytes & OFFSET_MASK);
    }

    public static Address fromLong(long packed, int defaultAlign) {
        int segId = (int) (packed >>> OFFSET_BITS) & ((1 << SEGMENT_BITS) - 1);
        long off = packed & OFFSET_MASK;
        Segment seg = decodeSegment(segId);
        int align = Math.max(1, defaultAlign);
        return new Address(seg, off, align, null);
    }

    private static Segment decodeSegment(int id) {
        Segment[] v = Segment.values();
        return (id >= 0 && id < v.length) ? v[id] : Segment.DATA;
    }

    // ----------------------------------------------------------------------------
    // Value semantics
    // ----------------------------------------------------------------------------
    private Address withOffset(long o) {
        if ((o & ~OFFSET_MASK) != 0) throw new IllegalArgumentException("resulting offset out of range");
        return new Address(segment, o, alignBytes, lengthBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address a = (Address) o;
        return offsetBytes == a.offsetBytes &&
                alignBytes == a.alignBytes &&
                segment == a.segment &&
                (Objects.equals(lengthBytes, a.lengthBytes));
    }

    @Override
    public int hashCode() {
        int r = segment.hashCode();
        r = 31 * r + Long.hashCode(offsetBytes);
        r = 31 * r + alignBytes;
        r = 31 * r + (lengthBytes == null ? 0 : lengthBytes.hashCode());
        return r;
    }

    @Override
    public String toString() {
        return "Address{seg=" + segment + ", off=" + offsetBytes + ", align=" + alignBytes +
                (lengthBytes != null ? ", len=" + lengthBytes : "") + "}";
    }
}
