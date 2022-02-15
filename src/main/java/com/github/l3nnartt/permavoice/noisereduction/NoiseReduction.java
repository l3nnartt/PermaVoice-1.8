package com.github.l3nnartt.permavoice.noisereduction;

import com.github.l3nnartt.permavoice.PermaVoice;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.labymod.addons.voicechat.audio.AudioModifier;
import net.labymod.utils.ModColor;

public class NoiseReduction {

  private boolean noiseReductionState;
  private int noiseReductionValue;
  private int noiseReductionValueGUI;
  private ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor();
  
  public void startThread() {
    this.exService.scheduleAtFixedRate(() -> {
      if (PermaVoice.getService().getNoiseReduction().isNoiseReduction())
        if (PermaVoice.getService().getPermaVoiceTickListener().isCurrentStatus()) {
          int bufferInSize = PermaVoice.getService().getVoiceChat().getOpusCodecManager().getBufferInSize();
          byte[] data = new byte[bufferInSize];
          PermaVoice.getService().getVoiceChat().getMicrophone().getTargetDataLine().read(data, 0, bufferInSize);
          int rmslevel = AudioModifier.calculateRMSLevel(data);
          if (rmslevel < this.noiseReductionValue) {
            PermaVoice.getService().getPermaVoiceTickListener().setVoicePressed(false);
          } else {
            PermaVoice.getService().getPermaVoiceTickListener().setVoicePressed(true);
          }
        } else if (PermaVoice.getService().isRepeatVoice()) {
          PermaVoice.getService().getPermaVoiceTickListener().setFieldTest(true);
          int bufferInSize = PermaVoice.getService().getVoiceChat().getOpusCodecManager().getBufferInSize();
          byte[] data = new byte[bufferInSize];
          PermaVoice.getService().getVoiceChat().getMicrophone().getTargetDataLine().read(data, 0, bufferInSize);
          int rmslevel = AudioModifier.calculateRMSLevel(data);
          this.noiseReductionValueGUI = rmslevel;
          if (this.noiseReductionValueGUI / Utils.voicedetectionMFactor > 100) {
            PermaVoice.getService().getHeaderElement().setDisplayName(ModColor.cl('a') + "Your VoiceChat Volume " + ModColor.cl('c') + (this.noiseReductionValueGUI / Utils.voicedetectionMFactor));
          } else {
            PermaVoice.getService().getHeaderElement().setDisplayName(ModColor.cl('a') + "Your VoiceChat Volume " + (this.noiseReductionValueGUI / Utils.voicedetectionMFactor));
          }
        } else {
          PermaVoice.getService().getPermaVoiceTickListener().setFieldTest(false);
        }
    }, 0L, 5L, TimeUnit.MILLISECONDS);
  }
  
  public void setNoiseReductionState(boolean noiseReductionState) {
    this.noiseReductionState = noiseReductionState;
  }
  
  public void setNoiseReductionValue(int noiseReductionValue) {
    this.noiseReductionValue = noiseReductionValue;
  }
  
  public int getNoiseReductionValue() {
    return this.noiseReductionValue;
  }
  
  public boolean isNoiseReduction() {
    return this.noiseReductionState;
  }
  
  public int getNoiseReductionValueGUI() {
    return this.noiseReductionValueGUI;
  }
}