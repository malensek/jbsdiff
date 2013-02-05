package jbsdiff.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jbsdiff.Diff;
import jbsdiff.InvalidHeaderException;
import jbsdiff.Patch;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * Provides an interface for working with bsdiff files on disk.
 *
 * @author malensek
 */
public class FileUI {

    public static void diff(File oldFile, File newFile, File patchFile)
    throws CompressorException, FileNotFoundException, InvalidHeaderException,
            IOException {
        diff(oldFile, newFile, patchFile, CompressorStreamFactory.BZIP2);
    }

    public static void diff(File oldFile, File newFile, File patchFile,
            String compression)
    throws CompressorException, FileNotFoundException, InvalidHeaderException,
            IOException {
        FileInputStream oldIn = new FileInputStream(oldFile);
        byte[] oldBytes = new byte[(int) oldFile.length()];
        oldIn.read(oldBytes);
        oldIn.close();

        FileInputStream newIn = new FileInputStream(newFile);
        byte[] newBytes = new byte[(int) newFile.length()];
        newIn.read(newBytes);
        newIn.close();

        FileOutputStream out = new FileOutputStream(patchFile);
        Diff.diff(oldBytes, newBytes, out, compression);
        out.close();
    }

    public static void patch(File oldFile, File newFile, File patchFile)
    throws CompressorException, FileNotFoundException, InvalidHeaderException,
            IOException {
        FileInputStream oldIn = new FileInputStream(oldFile);
        byte[] oldBytes = new byte[(int) oldFile.length()];
        oldIn.read(oldBytes);
        oldIn.close();

        FileInputStream patchIn = new FileInputStream(patchFile);
        byte[] patchBytes = new byte[(int) patchFile.length()];
        patchIn.read(patchBytes);
        patchIn.close();

        FileOutputStream out = new FileOutputStream(newFile);
        Patch.patch(oldBytes, patchBytes, out);
        out.close();
    }
}
