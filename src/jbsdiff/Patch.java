package jbsdiff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jbsdiff.streams.Bz2Compressor;
import jbsdiff.streams.StreamCompressor;

/**
 * This class provides functionality for using an old file and a patch to
 * generate a new file using the bsdiff patching algorithm.
 *
 * @author malensek
 */
public class Patch {

    public static void patch(byte[] old, byte[] patch, OutputStream out)
    throws Exception {
        patch(old, patch, out, new Bz2Compressor());
    }

    /**
     * Using an old file and its accompanying patch, this method generates a new
     * (updated) file and writes it to an {@link OutputStream}.
     */
    public static void patch(byte[] old, byte[] patch, OutputStream out,
            StreamCompressor compressor)
    throws Exception {
        /* Read bsdiff header */
        InputStream headerIn = new ByteArrayInputStream(patch);
        Header header = new Header(headerIn);
        headerIn.close();

        /* Set up InputStreams for reading different regions of the patch */
        InputStream controlIn, dataIn, extraIn;
        controlIn = new ByteArrayInputStream(patch);
        dataIn = new ByteArrayInputStream(patch);
        extraIn = new ByteArrayInputStream(patch);

        try {
            /* Seek to the correct offsets in each stream */
            controlIn.skip(Header.HEADER_SIZE);
            dataIn.skip(Header.HEADER_SIZE + header.getControlLength());
            extraIn.skip(Header.HEADER_SIZE + header.getControlLength() +
                    header.getDiffLength());

            /* Set up compressed streams */
            controlIn = compressor.compressStream(controlIn);
            dataIn = compressor.compressStream(dataIn);
            extraIn = compressor.compressStream(extraIn);

            /* Start patching */
            int newPointer = 0, oldPointer = 0;
            byte[] output = new byte[header.getOutputLength()];
            while (newPointer < output.length) {

                ControlBlock control = new ControlBlock(controlIn);

                /* Read diff string */
                read(dataIn, output, newPointer, control.getDiffLength());

                /* Add old data to diff string */
                for (int i = 0; i < control.getDiffLength(); ++i) {
                    if ((oldPointer + i >= 0) && oldPointer + i < old.length) {
                        output[newPointer + i] += old[oldPointer + i];
                    }
                }

                newPointer += control.getDiffLength();
                oldPointer += control.getDiffLength();

                /* Copy the extra string to the output */
                read(extraIn, output, newPointer, control.getExtraLength());

                newPointer += control.getExtraLength();
                oldPointer += control.getSeekLength();
            }

            out.write(output);

        } catch (Exception e) {
            throw e;
        } finally {
            controlIn.close();
            dataIn.close();
            extraIn.close();
        }
    }

    /**
     * Reads data from an InputStream, and throws an {@link IOException} if
     * fewer bytes were read than requested.  Since the lengths of data in a
     * bsdiff patch are explicitly encoded in the control blocks, reading less
     * than expected is an unrecoverable error.
     *
     * @param in InputStream to read from
     * @param dest byte array to read data into
     * @param off offset in dest to write data at
     * @param len length of the read
     */
    private static void read(InputStream in, byte[] dest, int off, int len)
    throws IOException {
        if (len == 0) {
            /* We don't need to do anything */
            return;
        }

        int read = in.read(dest, off, len);
        if (read < len) {
            throw new IOException("Corrupt patch; bytes expected = " + len +
                    " bytes read = " + read);
        }
    }
}
