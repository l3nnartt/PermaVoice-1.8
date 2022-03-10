package com.github.l3nnartt.permavoice.updater;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileDownloader {

    private final String url;
    private final File file;

    public FileDownloader(String url, File file) {
        this.url = url;
        this.file = file;
    }

    public boolean download() {
        if (this.file != null && this.url != null && this.url.startsWith("http"))
            try {
                FileUtils.copyURLToFile(new URL(this.url), this.file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return false;
    }
}