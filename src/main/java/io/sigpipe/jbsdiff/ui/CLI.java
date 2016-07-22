/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package io.sigpipe.jbsdiff.ui;

import java.io.File;

/**
 * Provides a simple command line interface for the io.sigpipe.jbsdiff tools.
 *
 * @author malensek
 */
public class CLI {

    private CLI() { }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Not enough parameters!");
            printUsage();
        }

        String compression = System.getProperty("jbsdiff.compressor", "bzip2");
        compression = compression.toLowerCase();

        try {
            String command = args[0].toLowerCase();
            File oldFile = new File(args[1]);
            File newFile = new File(args[2]);
            File patchFile = new File(args[3]);

            if ("diff".equals(command)) {
                FileUI.diff(oldFile, newFile, patchFile, compression);
            } else if ("patch".equals(command)) {
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
        String usage = String.format("" +
                "Usage: command <oldfile> <newfile> <patchfile>%n%n" +

                "Commands:%n" +
                "    diff%n" +
                "    patch%n%n" +

                "Use the jbsdiff.compressor property to select a different " +
                "compression scheme:%n" +
                "    java -Djbsdiff.compressor=gz -jar jbsdiff-*.jar diff " +
                "a.bin b.bin patch.gz%n%n" +

                "Supported compression schemes: bzip2 (default), gz, pack200, xz.%n%n" +
                "The compression algorithm used will be detected automatically during %n" +
                "patch operations.  NOTE: algorithms other than bzip2 are incompatible %n" +
                "with the reference implementation of bsdiff!");

        System.out.println(usage);
        System.exit(1);
    }
}
