package jbsdiff;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Patch {

    public static void patch(byte[] old, byte[] patch, OutputStream out)
    throws Exception {
        /* Read bsdiff header */
        InputStream headerIn = new ByteArrayInputStream(patch);
        Header header = new Header(headerIn);
        headerIn.close();

        /* Set up InputStreams for various offsets in the patch file */
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
            controlIn = new BZip2InputStream(controlIn);
            dataIn = new BZip2InputStream(dataIn);
            extraIn = new BZip2InputStream(extraIn);

            /* Start patching */
            int newPointer = 0, oldPointer = 0;
            int[] controlBlock = new int[3];
            int outputLength = header.getOutputLength();
            byte[] output = new byte[outputLength];
            int read;

            while (newPointer < outputLength) {

                /* Read control block */
                for (int i = 0; i <= 2; ++i) {
                    controlBlock[i] = Offset.readOffset(controlIn);
                }

                /* Read diff string */
                read = dataIn.read(output, newPointer, controlBlock[0]);
                if (read < controlBlock[0]) {
                    throw new IOException("Corrupt patch");
                }

                /* Add old data to diff string */
                for (int i = 0; i < controlBlock[0]; ++i) {
                    if ((oldPointer + i >= 0) && oldPointer + i < old.length) {
                        output[newPointer + i] += old[oldPointer + i];
                    }
                }

                /* Adjust pointers */
                newPointer += controlBlock[0];
                oldPointer += controlBlock[0];

                /* Sanity-check */
//                if (newPointer + controlBlock[1] > outLen)
//                    err("Corrupt patch\n");

                /* Read extra string */
                read = extraIn.read(output, newPointer, controlBlock[1]);
                if (read < controlBlock[1]) {
                    System.out.println("cb = " + controlBlock[1] + "read = " + read);
                    throw new IOException("Corrupt patch");
                }

                /* Adjust pointers */
                newPointer += controlBlock[1];
                oldPointer += controlBlock[2];
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
}
