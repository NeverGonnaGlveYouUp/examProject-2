package ru.tusur.ShaurmaWebSiteProject.ui.utils;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.FilenameUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageResourceUtils {
    public static StreamResource getImageResource(String url) {
        if (url != null) return new StreamResource(FilenameUtils.getName(url), (InputStreamFactory) () -> {
            try {
                return new DataInputStream(new FileInputStream(url));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        else return null;
    }
}
