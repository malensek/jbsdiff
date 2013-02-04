package jbsdiff.sort;

/**
 * Represents a binary search result for a string of bytes, containing the
 * longest match found and the position in the sorted suffix array.
 *
 * @author malensek
 */
public class SearchResult {

    /** Number of matched bytes */
    public int length;

    /** Position of the result in the suffix array */
    public int position;

    public SearchResult(int length, int position) {
        this.length = length;
        this.position = position;
    }

    @Override
    public String toString() {
        return new String("length = " + length + ", position = " + position);
    }
}
