package org.devel.bookowl.util;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import org.devel.bookowl.entity.BookFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileExplorerUtil {

    private static final String TAG = "FileExplorerUtil";

    public static ArrayList<BookFile> scanFile(File path, String ignore) {

        String ignorePath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ignore;

        ArrayList<BookFile> files = new ArrayList<BookFile>();

        // Checks whether path exists
        if (path.exists()) {

            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return (filename.endsWith(".epub") || sel.isDirectory())
                            && !sel.isHidden() ;

                }
            };

            String[] fList = path.list(filter);

            if (fList != null) {

                for (int i = 0; i < fList.length; i++) {

                    // Convert into file path
                    File sel = new File(path, fList[i]);

                    // Set drawables

                    if (sel.isDirectory()) {

                        // ignore app folder
                        if (!ignorePath.equals(sel.getAbsolutePath()))
                            files.addAll(scanFile(sel, ignore));

                    } else {
                        //TODO: add file
                        BookFile item = new BookFile();
                        item.setFilename(sel.getName());
                        item.setFilepath(sel.getAbsolutePath());
                        files.add(item);

                    }
                }
            }

        } else {
            Log.e(TAG, "path does not exist");
        }

        return files;
    }

    public static boolean createDirectory(String folderName) {

        boolean response = false;

        File f = new File(
                Environment.getExternalStorageDirectory(), File.separator + folderName + "/");

        if(!f.isDirectory()) {
            response = f.mkdirs();
        } else {
            response = true;
        }

        return response;
    }

    public static String moveToDirectory(String folderName, String filename,
                                          String originPathFile) {

        String pathFile = null;

        try {

            pathFile = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + folderName + "/" + filename;

            File src = new File(originPathFile);
            File dst = new File(pathFile);

            if (dst.exists()){

                Log.e(TAG, pathFile + " exists.");
            } else {

                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dst);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }


        return pathFile;
    }
}
