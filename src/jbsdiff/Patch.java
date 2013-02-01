package jbsdiff;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Patch {

    private final File oldFile;
    private final File newFile;
    private final File patchFile;

    public Patch(File oldFile, File newFile, File patchFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
        this.patchFile = patchFile;
    }

    public static void main(String[] args) throws Exception {
        File oldFile = new File(args[0]);
        File newFile = new File(args[1]);
        File patchFile = new File(args[2]);

        Patch p = new Patch(oldFile, newFile, patchFile);
        p.patch();
    }

    public void patch() throws Exception {
        /* Read bsdiff header */
        InputStream headerIn = new DataInputStream(new FileInputStream(patchFile));
        Header header = new Header(headerIn);
        headerIn.close();

        /* Set up InputStreams for various offsets in the patch file */
        InputStream controlIn, dataIn, extraIn;
        controlIn = new DataInputStream(new FileInputStream(patchFile));
        dataIn = new DataInputStream(new FileInputStream(patchFile));
        extraIn = new DataInputStream(new FileInputStream(patchFile));

        InputStream oldIn;
        oldIn = new FileInputStream(oldFile);

        try {
            /* Seek to the correct offsets in each stream */
            controlIn.skip(Header.HEADER_SIZE);
            dataIn.skip(Header.HEADER_SIZE + header.controlLength);
            extraIn.skip(Header.HEADER_SIZE + header.controlLength +
                    header.diffLength);

            /* Set up compressed streams */
            controlIn = new BZip2InputStream(controlIn);
            System.out.println(Header.HEADER_SIZE + header.controlLength);
            dataIn = new BZip2InputStream(dataIn);
            extraIn = new BZip2InputStream(extraIn);

            /* Start patching */
            int newPointer = 0;
            int[] controlBlock = new int[3];
            byte[] output = new byte[header.outLength];
            int read;

            while (newPointer < header.outLength) {

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
                byte[] old = new byte[controlBlock[0]];
                oldIn.read(old);
                for (int i = 0; i < controlBlock[0]; ++i) {
                    //if ((oldPointer + i >= 0) && (oldPointer + i < old.length)) {
                        //output[newPointer + i] += old[oldPointer + i];
                        output[newPointer + i] += old[i];
                    //}
                }

                /* Adjust pointer */
                newPointer += controlBlock[0];

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
                oldIn.skip(controlBlock[2]);
            }

            FileOutputStream fOut = new FileOutputStream(newFile);
            fOut.write(output);
            fOut.close();

        } catch (Exception e) {
            throw e;
        } finally {
            controlIn.close();
            dataIn.close();
            extraIn.close();

            oldIn.close();
        }
    }
}
