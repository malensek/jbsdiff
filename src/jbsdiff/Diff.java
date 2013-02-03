package jbsdiff;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 *
 * @author malensek
 */
public class Diff {
    public static void main(String[] args) throws Exception {
        File oldFile = new File(args[0]);
        File newFile = new File(args[1]);
        File patchFile = new File(args[2]);

        Diff d = new Diff();
        d.diff(oldFile, newFile, patchFile);
    }

    public void diff(File oldFile, File newFile, File patchFile)
    throws Exception{
        FileInputStream oldIn = new FileInputStream(oldFile);
        byte[] oldBytes = new byte[(int) oldFile.length()];
        oldIn.read(oldBytes);

        int[] I = new int[oldBytes.length + 1];
        int[] V = new int[oldBytes.length + 1];
        SuffixSort.qsufsort(I, V, oldBytes);

        FileInputStream newIn = new FileInputStream(newFile);
        byte[] newBytes = new byte[(int) newFile.length()];
        newIn.read(newBytes);

        ByteArrayOutputStream fOut = new ByteArrayOutputStream();
        CountingOutputStream countOut = new CountingOutputStream(fOut);
        OutputStream patchOut = new BZip2CompressorOutputStream(countOut, 9);

        SearchResult result = null;
        int scan = 0, len = 0;
        int lastScan = 0, lastPos = 0, lastOffset = 0;
        int oldScore = 0, scsc = 0;
        int s, Sf, lenf, Sb, lenb;
        int overlap, Ss, lens;

        byte[] db = new byte[newBytes.length + 1];
        byte[] eb = new byte[newBytes.length + 1];
        int dblen = 0, eblen = 0;

        int ctr = 0;
        while (scan < newBytes.length) {
            oldScore=0;

            for (scsc = scan += len; scan < newBytes.length; scan++) {
                result = SuffixSort.search(I,
                        oldBytes, 0,
                        newBytes, scan,
                        0, oldBytes.length);
                //System.out.println(result);
                len = result.length;

                for (;scsc<scan+len;scsc++) {
                    if ((scsc+lastOffset<oldBytes.length) &&
                            (oldBytes[scsc+lastOffset] == newBytes[scsc]))
                        oldScore++;
                }

                if (((len==oldScore) && (len!=0)) ||
                        (len>oldScore+8)) break;

                if ((scan+lastOffset<oldBytes.length) &&
                        (oldBytes[scan+lastOffset] == newBytes[scan]))
                    oldScore--;
            }

            if ((len!=oldScore) || (scan==newBytes.length)) {
                s=0;Sf=0;lenf=0;
                for (int i=0;(lastScan+i<scan)&&(lastPos+i<oldBytes.length);) {
                    if (oldBytes[lastPos+i]==newBytes[lastScan+i]) s++;
                    i++;
                    if (s*2-i>Sf*2-lenf) { Sf=s; lenf=i; };
                }

                lenb=0;
                if (scan<newBytes.length) {
                    s=0;Sb=0;
                    for (int i=1;(scan>=lastScan+i)&&(result.position>=i);i++) {
                        if (oldBytes[result.position-i]==newBytes[scan-i]) s++;
                        if (s*2-i>Sb*2-lenb) { Sb=s; lenb=i; };
                    }
                }

                if (lastScan+lenf>scan-lenb) {
                    overlap=(lastScan+lenf)-(scan-lenb);
                    s=0;Ss=0;lens=0;
                    for (int i=0;i<overlap;i++) {
                        if (newBytes[lastScan+lenf-overlap+i]==
                                oldBytes[lastPos+lenf-overlap+i]) s++;
                        if (newBytes[scan-lenb+i]==
                                oldBytes[result.position-lenb+i]) s--;
                        if (s>Ss) { Ss=s; lens=i+1; };
                    }

                    lenf += lens - overlap;
                    lenb -= lens;
                };

                for(int i=0;i<lenf;i++) {
                    db[dblen + i] |= (newBytes[lastScan + i] -
                            oldBytes[lastPos + i]);
                }
                for(int i = 0; i < (scan - lenb) - (lastScan + lenf); i++) {
                    eb[eblen + i] = newBytes[lastScan + lenf + i];
                }

                dblen+=lenf;
                eblen+=(scan-lenb)-(lastScan+lenf);

                Offset.writeOffset(lenf, patchOut);
                Offset.writeOffset((scan - lenb) - (lastScan + lenf), patchOut);
                Offset.writeOffset((result.position - lenb) - (lastPos + lenf), patchOut);
                ctr++;

                lastScan=scan-lenb;
                lastPos=result.position-lenb;
                lastOffset=result.position-scan;
            }
        }
        System.out.println(ctr);
        patchOut.close();
        System.out.println(countOut.getCount());

        Header header = new Header();
        header.setControlLength(countOut.getCount());

        patchOut = new BZip2CompressorOutputStream(countOut, 9);
        patchOut.write(db);
        patchOut.close();
        header.setDiffLength(countOut.getCount() - header.getControlLength());

        patchOut = new BZip2CompressorOutputStream(countOut, 9);
        patchOut.write(eb);
        patchOut.close();

        header.setOutputLength(newBytes.length);

        //header.write(headerOut);

        fOut.close();

        FileOutputStream f = new FileOutputStream(patchFile);
        header.write(f);
        f.write(fOut.toByteArray());
        f.close();
        //int controlLength = 
//        byte[] controlData = byteOut.toByteArray();
//
//        //Header header = new Header(controlData.length
//        //
//        ByteArrayOutputStream diffBytesOut = new ByteArrayOutputStream();
//        CountingOutputStream cOut = new CountingOutputStream(diffBytesOut);
//        BZip2CompressorOutputStream diffOut = new BZip2CompressorOutputStream(cOut);
//        diffOut.write(db);
//        diffOut.close();
//        System.out.println(cOut.getCount());
//        byte[] compressedDiffBlock = diffBytesOut.toByteArray();
//        System.out.println(compressedDiffBlock.length);
//
//        pOut.write(compressedDiffBlock);
//
//        //header.write(pOut);
//        pOut.write(controlData);
//        pOut.close();
    }
}
