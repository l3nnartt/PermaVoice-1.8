package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiOpenListener {

  @SubscribeEvent
  public void onGuiOpenEvent(GuiOpenEvent event) {
    if (PermaVoice.getInstance().isInit()) {
      PermaVoice.getInstance().getPermaVoiceTickListener().setFieldTest(false);
    }
  }
}
