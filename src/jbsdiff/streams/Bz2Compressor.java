package jbsdiff.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

public class Bz2Compressor implements StreamCompressor {

    public InputStream compressStream(InputStream in) throws IOException {
        return new BZip2CompressorInputStream(in);
    }

    public OutputStream compressStream(OutputStream out) throws IOException {
        return new BZip2CompressorOutputStream(out, 9);
    }
}
