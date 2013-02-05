package jbsdiff;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    /** Bytes to seek forward after completing the control block directives. */
    private int seekLength;

    public ControlBlock() { }

    /**
     * Read a bsdiff control block from an input stream.
     */
    public ControlBlock(InputStream in) throws IOException {
        diffLength = Offset.readOffset(in);
        extraLength = Offset.readOffset(in);
        seekLength = Offset.readOffset(in);
    }

    public ControlBlock(int diffLength, int extraLength, int seekLength) {
        this.diffLength = diffLength;
        this.extraLength = extraLength;
        this.seekLength = seekLength;
    }

    /**
     * Writes a ControlBlock to an OutputStream.
     */
    public void write(OutputStream out) throws IOException {
        Offset.writeOffset(diffLength, out);
        Offset.writeOffset(extraLength, out);
        Offset.writeOffset(seekLength, out);
    }

    @Override
    public String toString() {
        return diffLength + ", " + extraLength + ", " + seekLength;
    }

    public int getDiffLength() {
        return diffLength;
    }

    public void setDiffLength(int length) {
        diffLength = length;
    }

    public int getExtraLength() {
        return extraLength;
    }

    public void setExtraLength(int length) {
        extraLength = length;
    }

    public int getSeekLength() {
        return seekLength;
    }

    public void setSeekLength(int length) {
        seekLength = length;
    }

}
