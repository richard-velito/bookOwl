package org.devel.bookowl.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

public class BookUtilities {

    private static String TAG = BookUtilities.class.getSimpleName();

    public static Book readBookFromFile(String path){

        Book book=null;
        File pathFile = new File(path);

        try {

            InputStream epubInputStream = new
                    FileInputStream(pathFile);

            book = ((new EpubReader()).readEpub(epubInputStream)) ;
        } catch (Exception e) {

            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return book;
    }

    public static Resource getCover(Book book) {

        try {

            if ( book.getCoverImage() != null ) {

                return book.getCoverImage();
            } else {

                MediaType[] bitmapTypes = { MediatypeService.PNG,
                        MediatypeService.GIF, MediatypeService.JPG };

                List<Resource> bitmapResources =
                        book.getResources().getResourcesByMediaTypes( bitmapTypes );
                Resource coverResource = null;

                for ( Resource res: bitmapResources ) {

                    if ( coverResource == null || res.getSize() > coverResource.getSize() ) {
                        coverResource = res;
                    }
                }

                return coverResource;
            }

        } catch (Exception e) {
            return null;
        }
    }
}
