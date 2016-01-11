/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.deployer.api;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
//TODO(pablo.kraan): OSGi - move this class to another module
public class FileCompressor
{

    public File compress(String srcDir, String zipFile)
    {
        try
        {
            //create object of FileOutputStream
            FileOutputStream fout = new FileOutputStream(zipFile);

            //create object of ZipOutputStream from FileOutputStream
            ZipOutputStream zout = new ZipOutputStream(fout);

            //create File object from source directory
            File fileSource = new File(srcDir);

            addDirectory(zout, fileSource);

            //close the ZipOutputStream
            zout.close();
        }
        catch (IOException ioe)
        {
            //TODO(pablo.kraan): OSGi - use a better exception here
            throw new RuntimeException("Error creating zip file" + ioe);
        }

        return new File(zipFile);
    }

    private static void addDirectory(ZipOutputStream zout, File fileSource)
    {

        //get sub-folder/files list
        File[] files = fileSource.listFiles();

        System.out.println("Adding directory " + fileSource.getName());

        for (int i = 0; i < files.length; i++)
        {
            //if the file is directory, call the function recursively
            if (files[i].isDirectory())
            {
                addDirectory(zout, files[i]);
                continue;
            }

                        /*
                         * we are here means, its file and not directory, so
                         * add it to the zip file
                         */

            try
            {
                System.out.println("Adding file " + files[i].getName());

                //create byte buffer
                byte[] buffer = new byte[1024];

                //create object of FileInputStream
                FileInputStream fin = new FileInputStream(files[i]);

                zout.putNextEntry(new ZipEntry(files[i].getName()));

                                /*
                                 * After creating entry in the zip file, actually
                                 * write the file.
                                 */
                int length;

                while ((length = fin.read(buffer)) > 0)
                {
                    zout.write(buffer, 0, length);
                }

                                /*
                                 * After writing the file to ZipOutputStream, use
                                 *
                                 * void closeEntry() method of ZipOutputStream class to
                                 * close the current entry and position the stream to
                                 * write the next entry.
                                 */

                zout.closeEntry();

                //close the InputStream
                fin.close();

            }
            catch (IOException ioe)
            {
                System.out.println("IOException :" + ioe);
            }
        }

    }

    public static void zip(File directory, File zipfile) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        } finally {
            res.close();
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    private static void copy(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            copy(in, out);
        } finally {
            out.close();
        }
    }
}
