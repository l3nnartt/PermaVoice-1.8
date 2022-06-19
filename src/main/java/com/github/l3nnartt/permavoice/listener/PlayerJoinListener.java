package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.labymod.main.LabyMod;
import net.labymod.utils.Consumer;
import net.labymod.utils.ServerData;

public class PlayerJoinListener implements Consumer<ServerData> {
  public void accept(ServerData serverData) {
    if (PermaVoice.getInstance().isFound() && PermaVoice.getInstance().isEnabled()) {
      if (PermaVoice.getInstance().getVoiceChat().getKeyPushToTalk() == -1)
        LabyMod.getInstance()
            .getLabyModAPI()
            .displayMessageInChat("Please set in LabyMod a hotkey for Push-To-Talk!");
      if (PermaVoice.getInstance().isUpdateAvailable())
        LabyMod.getInstance()
            .getLabyModAPI()
            .displayMessageInChat("§6PermaVoice §7» §fUpdate found, restart your game to update");
    }
  }
}
