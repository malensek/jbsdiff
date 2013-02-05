package jbsdiff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import jbsdiff.sort.SuffixSort;
import jbsdiff.sort.SearchResult;

/**
 *
 * @author malensek
 */
public class Diff {

    public static void diff(byte[] oldBytes, byte[] newBytes, OutputStream out)
    throws CompressorException, InvalidHeaderException, IOException {
        diff(oldBytes, newBytes, out, CompressorStreamFactory.BZIP2);
    }

    public static void diff(byte[] oldBytes, byte[] newBytes, OutputStream out,
            String compression)
    throws CompressorException, InvalidHeaderException, IOException {
        CompressorStreamFactory compressor = new CompressorStreamFactory();

        int[] I = new int[oldBytes.length + 1];
        int[] V = new int[oldBytes.length + 1];
        SuffixSort.qsufsort(I, V, oldBytes);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        CountingOutputStream countOut = new CountingOutputStream(byteOut);
        OutputStream patchOut =
            compressor.createCompressorOutputStream(compression, countOut);

        SearchResult result = null;
        int scan = 0, len = 0;
        int lastScan = 0, lastPos = 0, lastOffset = 0;
        int oldScore = 0, scsc = 0;
        int s, Sf, lenf, Sb, lenb;
        int overlap, Ss, lens;

        byte[] db = new byte[newBytes.length + 1];
        byte[] eb = new byte[newBytes.length + 1];
        int dblen = 0, eblen = 0;

        while (scan < newBytes.length) {
            oldScore=0;

            for (scsc = scan += len; scan < newBytes.length; scan++) {
                result = SuffixSort.search(I,
                        oldBytes, 0,
                        newBytes, scan,
                        0, oldBytes.length);
                len = result.length;

                for ( ; scsc < scan + len; scsc++) {
                    if ((scsc + lastOffset < oldBytes.length) &&
                            (oldBytes[scsc + lastOffset] == newBytes[scsc]))
                        oldScore++;
                }

                if (((len == oldScore) && (len != 0)) || (len > oldScore + 8)) {
                    break;
                }

                if ((scan + lastOffset < oldBytes.length) &&
                        (oldBytes[scan + lastOffset] == newBytes[scan]))
                    oldScore--;
            }

            if ((len != oldScore) || (scan == newBytes.length)) {
                s = 0; Sf = 0; lenf = 0;
                for (int i = 0; (lastScan + i < scan) &&
                        (lastPos + i < oldBytes.length); ) {
                    if (oldBytes[lastPos + i] == newBytes[lastScan + i]) {
                        s++;
                    }

                    i++;
                    if (s * 2 - i > Sf * 2 - lenf) {
                        Sf = s;
                        lenf = i;
                    }
                }

                lenb = 0;
                if (scan < newBytes.length) {
                    s = 0;
                    Sb = 0;
                    for (int i = 1; (scan >= lastScan + i) &&
                            (result.position >= i); i++) {
                        if (oldBytes[result.position - i] ==
                                newBytes[scan - i]) {
                            s++;
                        }
                        if (s * 2 - i > Sb * 2 - lenb) {
                            Sb = s;
                            lenb = i;
                        }
                    }
                }

                if (lastScan + lenf > scan - lenb) {
                    overlap = (lastScan + lenf) - (scan - lenb);
                    s = 0;
                    Ss = 0;
                    lens = 0;
                    for (int i=0; i < overlap; i++) {
                        if (newBytes[lastScan + lenf - overlap + i] ==
                                oldBytes[lastPos + lenf - overlap + i]) {
                            s++;
                        }
                        if (newBytes[scan - lenb + i]==
                                oldBytes[result.position - lenb + i]) {
                            s--;
                        }
                        if (s > Ss) {
                            Ss = s;
                            lens = i + 1;
                        }
                    }
                    lenf += lens - overlap;
                    lenb -= lens;
                }

                for (int i = 0; i < lenf; i++) {
                    db[dblen + i] |= (newBytes[lastScan + i] -
                            oldBytes[lastPos + i]);
                }
                for (int i = 0; i < (scan - lenb) - (lastScan + lenf); i++) {
                    eb[eblen + i] = newBytes[lastScan + lenf + i];
                }

                dblen += lenf;
                eblen += (scan - lenb) - (lastScan + lenf);

                ControlBlock control = new ControlBlock();
                control.setDiffLength(lenf);
                control.setExtraLength((scan - lenb) - (lastScan + lenf));
                control.setSeekLength((result.position - lenb) -
                        (lastPos + lenf));
                control.write(patchOut);

                lastScan = scan - lenb;
                lastPos = result.position - lenb;
                lastOffset = result.position - scan;

            }
        }
        /* Done writing control blocks */
        patchOut.close();

        Header header = new Header();
        header.setControlLength(countOut.getCount());

        patchOut =
            compressor.createCompressorOutputStream(compression, countOut);
        patchOut.write(db);
        patchOut.close();
        header.setDiffLength(countOut.getCount() - header.getControlLength());

        patchOut =
            compressor.createCompressorOutputStream(compression, countOut);
        patchOut.write(eb);
        patchOut.close();

        header.setOutputLength(newBytes.length);

        header.write(out);
        out.write(byteOut.toByteArray());
    }
}
