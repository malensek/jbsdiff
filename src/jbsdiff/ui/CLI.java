package jbsdiff.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import jbsdiff.Patch;

/**
 * Provides a simple command line interface for the jbsdiff tools.
 *
 * @author malensek
 */
public class CLI {

    public CLI() { }

    public void diff(File oldFile, File newFile, File patchFile) {

    }

    public void patch(File oldFile, File newFile, File patchFile)
    throws Exception {
        System.out.println(oldFile.getName());
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

//    private static StreamCompressor getCompressor() {
//
//    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Not enough parameters!");
            System.out.println();
            printUsage();
        }

        CLI cli = new CLI();

        try {
            String command = args[0].toLowerCase();
            File oldFile = new File(args[1]);
            File newFile = new File(args[2]);
            File patchFile = new File(args[3]);

            if (command.equals("diff")) {
                cli.diff(oldFile, newFile, patchFile);
            } else if(command.equals("patch")) {
                cli.patch(oldFile, newFile, patchFile);
            } else {
                printUsage();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void printUsage() {
        String nl = System.lineSeparator();
        String usage = "" +
            "Usage: [diff|patch] oldfile newfile patchfile" + nl +
            nl +
            "The jbsdiff.compressor property can be used to change " +
            "compression schemes:" + nl +
            "java -jar jbsdiff.jar -Djbsdiff.compressor=bz2 " +
            "a.bin b.bin c.patch" + nl +
            nl +
            "Supported compression schemes: bz2 (default), gz, xz" + nl;

        System.out.println(usage);
        System.exit(1);
    }
}
