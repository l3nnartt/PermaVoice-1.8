package com.github.l3nnartt.permavoice.updater;

import com.github.l3nnartt.permavoice.PermaVoice;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import net.labymod.addon.AddonLoader;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.commons.io.IOUtils;

public class UpdateChecker implements Runnable {
  public void check() {
    try {
      String content = getURLContent("http://dl.lennartloesche.de/permavoice/8/info.json");
      JsonObject object = (new JsonParser()).parse(content).getAsJsonObject();
      int serverVersion = object.get("version").getAsInt();
      if (3 < serverVersion) {
        System.out.println("[PermaVoice] Outdatet version of Bugfixes detected, restart your Game");
        File file = initFile();
        Runtime.getRuntime().addShutdownHook(new Thread(new FileDownloader("http://dl.lennartloesche.de/permavoice/8/Bugfixes.jar", file)));
      } else {
        System.out.println("[PermaVoice] You run on the latest version of Bugfixes");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getURLContent(String url) throws IOException {
    HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
    con.setConnectTimeout(5000);
    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
    con.connect();
    return IOUtils.toString(con.getInputStream(), "UTF-8");
  }
  
  private static File initFile() {

    File dir = null;
    File file = null;

    try {
      dir = AddonLoader.getAddonsDirectory();
    } catch (NoClassDefFoundError e) {
      e.printStackTrace();
      String[] ver = RealmsSharedConstants.VERSION_STRING.split("\\.");
      dir = new File("LabyMod/", "addons-" + ver[0] + "." + ver[1]);
    }

    if (dir != null && dir.exists()) {
      file = new File(dir, "PermaVoice.jar");
      if (!file.exists()) {
        File[] listFiles;
        for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; i++) {
          File f = listFiles[i];
          if (f.getName().toLowerCase().contains("permavoice")) {
            file = f;
            break;
          }
        } 
      } 
    }

    if (dir != null && file != null && file.exists()) return file;
    try {
      URLConnection con = PermaVoice.class.getProtectionDomain().getCodeSource().getLocation().openConnection();
      file = new File(((JarURLConnection)con).getJarFileURL().getPath());
    } catch (Exception e2) {
      e2.printStackTrace();
    } return file;
  }

  @Override
  public void run() {
    check();
  }
}