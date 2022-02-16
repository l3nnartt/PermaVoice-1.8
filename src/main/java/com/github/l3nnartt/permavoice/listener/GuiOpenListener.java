package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOpenListener {

  private boolean addonGui;
  
  @SubscribeEvent
  public void onGuiOpenEvent(GuiOpenEvent event) {
    if (event.gui instanceof net.labymod.settings.LabyModAddonsGui) {
      this.addonGui = true;
    } else if (PermaVoice.getInstance().isInit() && PermaVoice.getInstance().isInitThread()) {
      this.addonGui = false;
      PermaVoice.getInstance().getPermaVoiceTickListener().setFieldTest(false);
    } 
  }
  
  public boolean isAddonGui() {
    return this.addonGui;
  }
}