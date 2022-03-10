package com.github.l3nnartt.permavoice.utils;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.labymod.addons.voicechat.audio.AudioModifier;
import net.labymod.utils.ModColor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NoiseReduction {

    private boolean noiseReductionState;
    private int noiseReductionValue;
    private int noiseReductionValueGUI;
    private final ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor();

    public void startThread() {
        this.exService.scheduleAtFixedRate(() -> {
            if (PermaVoice.getInstance().getNoiseReduction().isNoiseReduction())
                if (PermaVoice.getInstance().getPermaVoiceTickListener().isCurrentStatus()) {
                    int bufferInSize = PermaVoice.getInstance().getVoiceChat().getOpusCodecManager().getBufferInSize();
                    byte[] data = new byte[bufferInSize];
                    PermaVoice.getInstance().getVoiceChat().getMicrophone().getTargetDataLine().read(data, 0, bufferInSize);
                    int rmslevel = AudioModifier.calculateRMSLevel(data);
                  PermaVoice.getInstance().getPermaVoiceTickListener().setVoicePressed(rmslevel >= this.noiseReductionValue);
                } else if (PermaVoice.getInstance().isRepeatVoice()) {
                    PermaVoice.getInstance().getPermaVoiceTickListener().setFieldTest(true);
                    int bufferInSize = PermaVoice.getInstance().getVoiceChat().getOpusCodecManager().getBufferInSize();
                    byte[] data = new byte[bufferInSize];
                    PermaVoice.getInstance().getVoiceChat().getMicrophone().getTargetDataLine().read(data, 0, bufferInSize);
                    int rmslevel = AudioModifier.calculateRMSLevel(data);
                    this.noiseReductionValueGUI = rmslevel;
                    if (this.noiseReductionValueGUI / Utils.voicedetectionMFactor > 100) {
                        PermaVoice.getInstance().getHeaderElement().setDisplayName(ModColor.cl('a') + "Your VoiceChat Volume " + ModColor.cl('c') + (this.noiseReductionValueGUI / Utils.voicedetectionMFactor));
                    } else {
                        PermaVoice.getInstance().getHeaderElement().setDisplayName(ModColor.cl('a') + "Your VoiceChat Volume " + (this.noiseReductionValueGUI / Utils.voicedetectionMFactor));
                    }
                } else {
                    PermaVoice.getInstance().getPermaVoiceTickListener().setFieldTest(false);
                }
        }, 0L, 5L, TimeUnit.MILLISECONDS);
    }

    public boolean isNoiseReduction() {
        return this.noiseReductionState;
    }
}