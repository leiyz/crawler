package com.pmp.crawler;

import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by clouder on 5/5/16.
 */
public class FileUtilitys {
    public static void writeToHtmlFile(Elements elements, Path filepath) {
        elements.stream().forEach( x -> {
            try {
                BufferedWriter writer = Files.newBufferedWriter( filepath, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW );
                writer.write( "<head>\n" +
                        "<META http-equiv=Content-Type content=\"text/html; charset=utf-8\">\n" +
                        "</head>" );
                writer.newLine();
                writer.append( x.html() );
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } );
    }
}
