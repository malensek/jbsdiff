package jbsdiff;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A passthrough OutputStream implementation that counts (and can report) the
 * number of bytes that have been written to the stream.
 *
 * @author malensek
 */
public class CountingOutputStream extends OutputStream {

    private OutputStream out;
    private int counter;

    public CountingOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        counter++;
        out.write(b);
    }

    /**
     * Retrieves the number of bytes that have been written to this stream so
     * far.
     */
    public int getCount() {
        return counter;
    }
}
