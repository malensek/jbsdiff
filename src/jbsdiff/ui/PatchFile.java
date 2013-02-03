package jbsdiff.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import jbsdiff.Patch;

public class PatchFile {
    public static void main(String[] args) throws Exception {

        if (args.length < 3) {
            /* Let wrapper scripts know there was a usage violation */
            System.exit(-1);
        }

        try {

            File oldFile = new File(args[0]);
            File newFile = new File(args[1]);
            File patchFile = new File(args[2]);

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

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
