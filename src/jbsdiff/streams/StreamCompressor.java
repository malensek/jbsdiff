
package jbsdiff.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface that allows different compression schemes to be selected at
 * runtime.  Classes implementing this interface simply decorate an existing
 * stream and then return the decorated stream.
 *
 * @author malensek
 */
public interface StreamCompressor {

    /**
     * Decorates the specified {@link InputStream} and returns it.
     */
    public InputStream compressStream(InputStream in) throws IOException;

    /**
     * Decorates the specified {@link OutputStream} and returns it.
     */
    public OutputStream compressStream(OutputStream out) throws IOException;

}
