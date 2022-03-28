package com.github.l3nnartt.permavoice.listener;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class PermaVoiceTickListener {

    private Field fieldPress;
    private Field fieldTest;
    private boolean togglePressed;
    private boolean currentStatus;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!PermaVoice.getInstance().isInit()) {
            for (LabyModAddon addon : AddonLoader.getAddons()) {
                if (addon == null || addon.about == null || addon.about.name == null) {
                    continue;
                }
                if (addon.about.name.equals("VoiceChat") && addon instanceof VoiceChat) {
                    PermaVoice.getInstance().setVoiceChat((VoiceChat) addon);
                    PermaVoice.getInstance().setFound(true);
                    MinecraftForge.EVENT_BUS.unregister(PermaVoice.getInstance().getVoiceChat());
                    try {
                        fieldPress = PermaVoice.getInstance().getVoiceChat().getClass().getDeclaredField("pushToTalkPressed");
                        fieldPress.setAccessible(true);
                        fieldTest = PermaVoice.getInstance().getVoiceChat().getClass().getDeclaredField("testingMicrophone");
                        fieldTest.setAccessible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            PermaVoice.getInstance().setInit(true);
        }
        if (!PermaVoice.getInstance().isFound()) {
            return;
        }
        PermaVoice.getInstance().getVoiceChat().onTick(event);
        if (PermaVoice.getInstance().getVoiceChat().getKeyPushToTalk() == -1 || !PermaVoice.getInstance().isEnabled()) {
            return;
        }
        if (PermaVoice.getInstance().isActive() && !PermaVoice.getInstance().getVoiceChat().isPushToTalkPressed()) {
            setPressed(true);
        }

        if (PermaVoice.getInstance().getKey() == -1) {
            return;
        }
        if (Keyboard.isKeyDown(PermaVoice.getInstance().getKey())) {
            if (Minecraft.getMinecraft().currentScreen == null) {
                if (!this.togglePressed) {
                    this.togglePressed = true;
                    PermaVoice.getInstance().setActive(!PermaVoice.getInstance().getActive());
                    setCurrentStatus(!isCurrentStatus());
                    if (PermaVoice.getInstance().isChatMessages()) {
                        LabyMod.getInstance().getLabyModAPI().displayMessageInChat("§ePermaVoice §8» §e" + (PermaVoice.getInstance().getActive() ? "§aON" : "§cOFF"));
                    }
                }
            }
        } else {
            this.togglePressed = false;
        }
    }

    public void setPressed(boolean mode) {
        try {
            PermaVoice.getInstance().setActive(mode);
            fieldPress.set(PermaVoice.getInstance().getVoiceChat(), mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVoicePressed(boolean mode) {
        try {
            fieldPress.set(PermaVoice.getInstance().getVoiceChat(), mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(boolean currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Field getFieldTest() {
        return fieldTest;
    }

    public void setFieldTest(boolean mode) {
        try {
            fieldTest.set(PermaVoice.getInstance().getVoiceChat(), mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}