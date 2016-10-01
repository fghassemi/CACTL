/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.ut.ece.cactlmodelchecker.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashkan
 */
public class IOUtils {

    public static final String FILE_DIRECTORY = "/home/ashkan/"; // TODO should be refactored and changed to a relative path!

    public static void writeOnDisk(String content, String fileName, String path) {
        try {
            File file = new File(path + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't write the content on disk, " + 
                    "an exception occured while doing so", ex);
        }
    }
    
    public static String readFile(String fileName, String path) {
        try {
            return new Scanner(new File(path+fileName)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Couldn't read the file from disk, " + 
                    "an exception occured while doing so", ex);
        }
    }
    
}
