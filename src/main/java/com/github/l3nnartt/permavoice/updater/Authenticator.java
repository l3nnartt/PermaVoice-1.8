package com.github.l3nnartt.permavoice.updater;

import com.github.l3nnartt.permavoice.PermaVoice;
import com.mojang.authlib.exceptions.AuthenticationException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Authenticator implements Runnable {
  public boolean authenticate() {
    Minecraft mc = Minecraft.getMinecraft();
    Session session = mc.getSession();
    if (session == null) {
      return false;
    }
    try {
      mc.getSessionService()
          .joinServer(
              session.getProfile(), session.getToken(), "26c142208fc4cb3e6ed4ebc598d989b4848786ed");
      return true;
    } catch (AuthenticationException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void request() {
    try {
      UUID uuid = LabyMod.getInstance().getLabyModAPI().getPlayerUUID();
      String username = LabyMod.getInstance().getLabyModAPI().getPlayerUsername();
      int addonVersion = UpdateChecker.getAddonVersion();

      HttpURLConnection con =
          (HttpURLConnection)
              new URL(
                      "https://dl.lennartloesche.de/permavoice/auth.php?name="
                          + username
                          + "&uuid="
                          + uuid
                          + "&version="
                          + addonVersion)
                  .openConnection();
      con.setRequestProperty(
          "User-Agent",
          "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316"
              + " Firefox/3.6.2");
      con.connect();
      int code = con.getResponseCode();
      if (code == 200) {
        PermaVoice.getLogger("Request successful");
      } else {
        PermaVoice.getLogger("Request failed. Errorcode: " + code);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    if (authenticate()) {
      request();
    }
  }
}
