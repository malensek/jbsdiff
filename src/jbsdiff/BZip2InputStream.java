package jbsdiff;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.IOException;
import java.io.InputStream;

public class BZip2InputStream extends BZip2CompressorInputStream {
    public BZip2InputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    public int read(byte[] dest, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        } else {
            return super.read(dest, off, len);
        }
    }
}
