package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;

public class PlayerJoinListener implements Consumer<ServerData> {
  public void accept(ServerData serverData) {
    if (PermaVoice.getService().isFound() && PermaVoice.getService().isEnabled()) {
      if (PermaVoice.getService().getVoiceChat().getKeyPushToTalk() == -1)
        LabyMod.getInstance().getLabyModAPI().displayMessageInChat("set in LabyMod a Hotkey for Push To Talk!"); 
      PermaVoice.getService();
      if (!PermaVoice.isHdCapes()) {
        LabyMod.getInstance().getLabyModAPI().displayMessageInChat("just released our new Addon! Look here https://HDCapes.de/");
        PermaVoice.getService().getConfig().addProperty("hdCapes", Boolean.valueOf(true));
        PermaVoice.getService().saveConfig();
        PermaVoice.getService();
        PermaVoice.setHdCapes(true);
      } 
    } 
  }
}