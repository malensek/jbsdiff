package jbsdiff;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Data structure that encapsulates a bsdiff header.  The header is composed of
 * 8-byte fields, starting with the magic number "BSDIFF40."
 *
 * 0: BSDIFF40
 * 8: length of control block
 * 16: length of the diff block
 * 24: size of the output file
 *
 * @author malensek
 */

class Header {

    /** Size of the Header, in bytes.  4 fields * 8 bytes = 32 bytes */
    public static final int HEADER_SIZE = 32;

    /** Magic number to mark the start of a bsdiff header. */
    public static final String HEADER_MAGIC = "BSDIFF40";

    private String magic;
    private int controlLength;
    private int diffLength;
    private int outLength;

    public Header() { }

    public Header(InputStream in) throws IOException, InvalidHeaderException {
        InputStream headerIn = new DataInputStream(in);
        byte[] buf = new byte[8];

        headerIn.read(buf);
        magic = new String(buf);
        if (!magic.equals("BSDIFF40")) {
            throw new InvalidHeaderException("Header missing magic number");
        }

        controlLength = Offset.readOffset(headerIn);
        diffLength = Offset.readOffset(headerIn);
        outLength = Offset.readOffset(headerIn);

        verify();
    }

    public Header(int controlLength, int diffLength, int outLength)
    throws InvalidHeaderException {
        this.controlLength = controlLength;
        this.diffLength = diffLength;
        this.outLength = outLength;

        verify();
    }

    /**
     * Verifies the values of the header fields.
     */
    private void verify() throws InvalidHeaderException {
        if (controlLength < 0) {
            throw new InvalidHeaderException("control block length",
                    controlLength);
        }

        if (diffLength < 0) {
            throw new InvalidHeaderException("diff block length", diffLength);
        }

        if (outLength < 0) {
            throw new InvalidHeaderException("output file length", outLength);
        }
    }

    public int getControlLength() {
        return controlLength;
    }

    public void setControlLength(int length) throws InvalidHeaderException {
        controlLength = length;
        verify();
    }

    public int getDiffLength() {
        return diffLength;
    }

    public void setDiffLength(int length) throws InvalidHeaderException {
        diffLength = length;
        verify();
    }

    public int getOutputLength() {
        return outLength;
    }

    public void setOutputLength(int length) throws InvalidHeaderException {
        outLength = length;
        verify();
    }

    /**
     * Writes the Header to an OutputStream.
     */
    public void write(OutputStream out) throws IOException {
        out.write(HEADER_MAGIC.getBytes());
        Offset.writeOffset(controlLength, out);
        Offset.writeOffset(diffLength, out);
        Offset.writeOffset(outLength, out);
    }

    @Override
    public String toString() {
        String s = "";

        s += magic + "\n";
        s += "control bytes = " + controlLength + "\n";
        s += "diff bytes = " + diffLength + "\n";
        s += "output size = " + outLength + "\n";

        return s;
    }
}
