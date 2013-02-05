package jbsdiff.ui;

import java.io.File;

/**
 * Provides a simple command line interface for the jbsdiff tools.
 *
 * @author malensek
 */
public class CLI {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Not enough parameters!");
            System.out.println();
            printUsage();
        }

        String compression = System.getProperty("jbsdiff.compressor", "bzip2");

        try {
            String command = args[0].toLowerCase();
            File oldFile = new File(args[1]);
            File newFile = new File(args[2]);
            File patchFile = new File(args[3]);

            if (command.equals("diff")) {
                FileUI.diff(oldFile, newFile, patchFile, compression);
            } else if(command.equals("patch")) {
                FileUI.patch(oldFile, newFile, patchFile);
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
"Usage: [diff|patch] oldfile newfile patchfile" + nl + nl +

"The jbsdiff.compressor property can be used to select a different " + nl +
"compression scheme at runtime:" + nl + nl +

"    java -Djbsdiff.compressor=gz -jar jbsdiff.jar diff a.bin b.bin patch.gz" +
nl + nl +
"Supported compression schemes: bzip2 (default), gz, pack200, xz." + nl + nl +
"The compression algorithm used will be detected automatically during " + nl +
"patch operations.  NOTE: algorithms other than bzip2 are incompatible " + nl +
"with the reference implementation of bsdiff!";

        System.out.println(usage);
        System.exit(1);
    }
}
