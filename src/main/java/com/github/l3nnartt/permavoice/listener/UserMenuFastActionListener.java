package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import java.util.List;
import java.util.UUID;
import net.labymod.api.events.UserMenuActionEvent;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.util.UserActionEntry;
import net.labymod.utils.ModColor;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class UserMenuFastActionListener implements UserMenuActionEvent {
  public void createActions(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo, List<UserActionEntry> entries) {
    if (!PermaVoice.getInstance().getVoiceChat().isConnected())
      return; 
    if (PermaVoice.getInstance().getVoiceChat().getVolume(entityPlayer.getUniqueID()) == 0) {
      entries.add(new UserActionEntry("[PVC] Fastunmute", UserActionEntry.EnumActionType.NONE, null, new UserActionEntry.ActionExecutor() {
          public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
            PermaVoice.getInstance().getVoiceChat().getPlayerVolumes().remove(user.getUuid());
            LabyMod.getInstance().displayMessageInChat(ModColor.cl('c') + "You unmuted " + ModColor.cl('e') + entityPlayer.getDisplayNameString() + ModColor.cl('c') + " in the voicechat!");
            PermaVoice.getInstance().getVoiceChat().savePlayersVolumes();
          }

          public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
            return true;
          }
        })
      );
    } else {
      entries.add(new UserActionEntry("[PVC] Fastmute", UserActionEntry.EnumActionType.NONE, null, new UserActionEntry.ActionExecutor() {
          public void execute(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
            UserMenuFastActionListener.this.mute(entityPlayer.getUniqueID());
            LabyMod.getInstance().displayMessageInChat(ModColor.cl('c') + "You muted " + ModColor.cl('e') + entityPlayer.getDisplayNameString() + ModColor.cl('c') + " in the voicechat!");
          }

          public boolean canAppear(User user, EntityPlayer entityPlayer, NetworkPlayerInfo networkPlayerInfo) {
            return true;
          }
        })
      );
    } 
  }
  
  private void mute(UUID uuid) {
    PermaVoice.getInstance().getVoiceChat().getPlayerVolumes().put(uuid, Integer.valueOf(0));
    PermaVoice.getInstance().getVoiceChat().savePlayersVolumes();
  }
}