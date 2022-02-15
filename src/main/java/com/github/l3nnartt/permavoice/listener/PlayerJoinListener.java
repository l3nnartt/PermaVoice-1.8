package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;

public class PlayerJoinListener implements Consumer<ServerData> {
  public void accept(ServerData serverData) {
    if (PermaVoice.getInstance().isFound() && PermaVoice.getInstance().isEnabled()) {
      if (PermaVoice.getInstance().getVoiceChat().getKeyPushToTalk() == -1)
        LabyMod.getInstance().getLabyModAPI().displayMessageInChat("Please set in LabyMod a hotkey for Push-To-Talk!");
    } 
  }
}