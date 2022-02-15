package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import java.lang.reflect.Field;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class PermaVoiceTickListener {

  private Field fieldPress;
  private Field fieldTest;
  private boolean togglePressed;
  private boolean currentStatus;
  
  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!PermaVoice.getInstance().isInit()) {
      for (LabyModAddon addon : AddonLoader.getAddons()) {
        if (addon == null || addon.about == null || addon.about.name == null)
          continue;
        if (addon.about.name.equals("VoiceChat") && addon instanceof VoiceChat) {
          PermaVoice.getInstance().setVoiceChat((VoiceChat)addon);
          PermaVoice.getInstance().setFound(true);
          MinecraftForge.EVENT_BUS.unregister(PermaVoice.getInstance().getVoiceChat());
          try {
            this.fieldPress = PermaVoice.getInstance().getVoiceChat().getClass().getDeclaredField("pushToTalkPressed");
            this.fieldPress.setAccessible(true);
            this.fieldTest = PermaVoice.getInstance().getVoiceChat().getClass().getDeclaredField("testingMicrophone");
            this.fieldTest.setAccessible(true);
          } catch (Exception ex) {
            ex.printStackTrace();
          } 
        } 
      } 
      PermaVoice.getInstance().setInit(true);
    } 
    if (!PermaVoice.getInstance().isFound())
      return; 
    PermaVoice.getInstance().getVoiceChat().onTick(event);
    if (PermaVoice.getInstance().getVoiceChat().getKeyPushToTalk() == -1 || !PermaVoice.getInstance().isEnabled())
      return; 
    if (PermaVoice.getInstance().isActive() && !PermaVoice.getInstance().getVoiceChat().isPushToTalkPressed() &&
      !PermaVoice.getInstance().getNoiseReduction().isNoiseReduction())
      setPressed(true); 
    if (PermaVoice.getInstance().getKey() == -1)
      return; 
    if (Keyboard.isKeyDown(PermaVoice.getInstance().getKey())) {
      if ((Minecraft.getMinecraft()) == null &&
        !this.togglePressed) {
        this.togglePressed = true;
        PermaVoice.getInstance().setActive(!PermaVoice.getInstance().getActive());
        setCurrentStatus(!isCurrentStatus());
        if (PermaVoice.getInstance().isChatMessages())
          LabyMod.getInstance().getLabyModAPI().displayMessageInChat("§ePermaVoice §8» §e"+ (PermaVoice.getInstance().getActive() ? "§aON" : "§cOFF"));
        if (PermaVoice.getInstance().getNoiseReduction().isNoiseReduction())
          if (PermaVoice.getInstance().isActive()) {
            PermaVoice.getInstance().setCurrentStateOfVoice(true);
          } else {
            PermaVoice.getInstance().setCurrentStateOfVoice(false);
          }
      } 
    } else {
      this.togglePressed = false;
    } 
    if (!PermaVoice.getInstance().isInitThread() &&
      PermaVoice.getInstance().getNoiseReduction().isNoiseReduction() &&
      PermaVoice.getInstance().getVoiceChat().isConnected()) {
      PermaVoice.getInstance().setInitThread(true);
    } 
  }
  
  public void setPressed(boolean mode) {
    try {
      PermaVoice.getInstance().setActive(mode);
      this.fieldPress.set(PermaVoice.getInstance().getVoiceChat(), mode);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void setVoicePressed(boolean mode) {
    try {
      this.fieldPress.set(PermaVoice.getInstance().getVoiceChat(), mode);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void setCurrentStatus(boolean currentStatus) {
    this.currentStatus = currentStatus;
  }
  
  public boolean isCurrentStatus() {
    return this.currentStatus;
  }
  
  public Field getFieldTest() {
    return this.fieldTest;
  }
  
  public void setFieldTest(boolean mode) {
    try {
      this.fieldTest.set(PermaVoice.getInstance().getVoiceChat(), mode);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
