package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    if (!PermaVoice.getService().isInit()) {
      for (LabyModAddon addon : AddonLoader.getAddons()) {
        if (addon == null || addon.about == null || addon.about.name == null)
          continue; 
        if (addon.about.name.equals("CosmeticsMod")) {
          PermaVoice.getService().setCosmeticsMod(true);
          System.out.println("Mod found!");
        } 
        if (addon.about.name.equals("VoiceChat") && addon instanceof VoiceChat) {
          PermaVoice.getService().setVoiceChat((VoiceChat)addon);
          PermaVoice.getService().setFound(true);
          MinecraftForge.EVENT_BUS.unregister(PermaVoice.getService().getVoiceChat());
          try {
            this.fieldPress = PermaVoice.getService().getVoiceChat().getClass().getDeclaredField("pushToTalkPressed");
            this.fieldPress.setAccessible(true);
            this.fieldTest = PermaVoice.getService().getVoiceChat().getClass().getDeclaredField("testingMicrophone");
            this.fieldTest.setAccessible(true);
          } catch (Exception ex) {
            ex.printStackTrace();
          } 
        } 
      } 
      PermaVoice.getService().setInit(true);
    } 
    if (!PermaVoice.getService().isFound())
      return; 
    PermaVoice.getService().getVoiceChat().onTick(event);
    if (PermaVoice.getService().getVoiceChat().getKeyPushToTalk() == -1 || !PermaVoice.getService().isEnabled())
      return; 
    if (PermaVoice.getService().isActive() && !PermaVoice.getService().getVoiceChat().isPushToTalkPressed() && 
      !PermaVoice.getService().getNoiseReduction().isNoiseReduction())
      setPressed(true); 
    if (PermaVoice.getService().getKey() == -1)
      return; 
    if (Keyboard.isKeyDown(PermaVoice.getService().getKey())) {
      if ((Minecraft.getMinecraft()) == null &&
        !this.togglePressed) {
        this.togglePressed = true;
        PermaVoice.getService().setActive(!PermaVoice.getService().getActive());
        setCurrentStatus(!isCurrentStatus());
        if (PermaVoice.getService().isChatMessages())
          LabyMod.getInstance().getLabyModAPI().displayMessageInChat("§ePermaVoice §8» §e"+ (PermaVoice.getService().getActive() ? "§aON" : "§cOFF"));
        if (PermaVoice.getService().getNoiseReduction().isNoiseReduction())
          if (PermaVoice.getService().isActive()) {
            PermaVoice.getService().setCurrentStateOfVoice(true);
          } else {
            PermaVoice.getService().setCurrentStateOfVoice(false);
          }  
      } 
    } else {
      this.togglePressed = false;
    } 
    if (!PermaVoice.getService().isInitThread() && 
      PermaVoice.getService().getNoiseReduction().isNoiseReduction() && 
      PermaVoice.getService().getVoiceChat().isConnected()) {
      PermaVoice.getService().setInitThread(true);
      ScheduledExecutorService exservice = Executors.newSingleThreadScheduledExecutor();
      exservice.execute(() -> {
          
          });
    } 
  }
  
  public void setPressed(boolean mode) {
    try {
      PermaVoice.getService().setActive(mode);
      this.fieldPress.set(PermaVoice.getService().getVoiceChat(), Boolean.valueOf(mode));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void setVoicePressed(boolean mode) {
    try {
      this.fieldPress.set(PermaVoice.getService().getVoiceChat(), Boolean.valueOf(mode));
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
      this.fieldTest.set(PermaVoice.getService().getVoiceChat(), Boolean.valueOf(mode));
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
