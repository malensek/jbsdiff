package jbsdiff;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a bsdiff Control Block.  Control blocks consist of a set of
 * triples (x, y, z) meaning:
 * <ul>
 * <li>Add x bytes from the old file to x bytes from the diff block</li>
 * <li>Copy y bytes from the extra block</li>
 * <li>Seek forwards in the old file by z bytes</li>
 * </ul>
 *
 * @author malensek
 */
class ControlBlock {

    /** Length of the patch diff block */
    private int diffLength;

    /** Length of the patch extra block */
    private int extraLength;

    /** Number of bytes to seek after completing the directives in the control
        block */
    private int seekLength;

    public ControlBlock(InputStream in) throws IOException {
        diffLength = Offset.readOffset(in);
        extraLength = Offset.readOffset(in);
        seekLength = Offset.readOffset(in);
    }

    public int getDiffLength() {
        return diffLength;
    }

    public int getExtraLength() {
        return extraLength;
    }

    public int getSeekLength() {
        return seekLength;
    }
}
