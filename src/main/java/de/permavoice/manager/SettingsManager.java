package de.permavoice.manager;

import de.permavoice.PermaVoice;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAPI;
import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.*;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SettingsManager {

    private PermaVoice permaVoice;
    private LabyModAPI api;

    private boolean isVoiceChatLoaded;
    private boolean isPVInitialised;
    private VoiceChat voiceChatInstance;
    private Field voiceChatKey;


    private boolean pvEnabled;
    private int pvKey;

    private MODES currentMode;
    private boolean isToggled;
    private boolean state;

    public SettingsManager(PermaVoice permaVoice){
        this.permaVoice = permaVoice;
        this.api = permaVoice.api;

        this.isVoiceChatLoaded = false;
        api.registerForgeListener(this);
        this.isPVInitialised = false;


    }

    public void fillSettings(List<SettingsElement> subSettings){
        subSettings.add(new BooleanElement("Enabled", new ControlElement.IconData(new ResourceLocation("permavoice/icons/enable.png")), aBoolean -> {
            pvEnabled = aBoolean;
            saveSettings();
        }, pvEnabled));
        subSettings.add(new KeyElement("Key", permaVoice ,new ControlElement.IconData(new ResourceLocation("permavoice/icons/wasd.png")), "key", this.pvKey));
        DropDownMenu<String> alignmentDropDownMenu = new DropDownMenu("Mode", 0, 0, 0, 0).fill(MODES.getAllTexts());
        alignmentDropDownMenu.setSelected(this.currentMode.modeText);
        DropDownElement<String> alignmentDropDown = new DropDownElement("Mode", alignmentDropDownMenu);
        alignmentDropDown.setChangeListener(modes -> {
            this.currentMode = MODES.getModeByText(modes);
            saveSettings();
        });
        subSettings.add(alignmentDropDown);

    }

    public void onLoadConfig(){
        this.pvEnabled = !permaVoice.getConfig().has("enabled") || permaVoice.getConfig().get("enabled").getAsBoolean();
        this.pvKey = permaVoice.getConfig().has("key") ? permaVoice.getConfig().get("key").getAsInt() : -1;
        this.currentMode = MODES.getModeById(this.permaVoice.getConfig().has("mode") ? this.permaVoice.getConfig().get("mode").getAsInt() : 1);
    }

    private void saveSettings() {
        this.permaVoice.getConfig().addProperty("enabled", this.pvEnabled);
        this.permaVoice.getConfig().addProperty("currentMode", this.currentMode.modeId);
        this.permaVoice.saveConfig();
    }

    @SubscribeEvent
    public void loadVoiceChat(TickEvent.ClientTickEvent event) {
        if(!isPVInitialised){
            for(LabyModAddon addon : AddonLoader.getAddons()){
                if (addon == null || addon.about == null || addon.about.name == null) {
                    continue;
                }
                if(addon.about.name.equals("VoiceChat") && addon instanceof VoiceChat){
                    voiceChatInstance = (VoiceChat) addon;
                    isVoiceChatLoaded = true;
                    MinecraftForge.EVENT_BUS.unregister(voiceChatInstance);
                    try {
                        voiceChatKey = voiceChatInstance.getClass().getDeclaredField("pushToTalkPressed");
                        voiceChatKey.setAccessible(true);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            isPVInitialised = true;
        }
        if(isVoiceChatLoaded){
            voiceChatInstance.onTick(event);
        }
    }

    @SubscribeEvent
    public void handlePress(TickEvent.ClientTickEvent event){
        if (!this.isPVInitialised || this.voiceChatInstance.getKeyPushToTalk() == -1 || !this.pvEnabled || this.voiceChatInstance.isPushToTalkPressed())
            return;
        if (this.currentMode.equals(MODES.MUTE)) {
            this.state = false;
            if (Keyboard.isKeyDown(this.pvKey) && Minecraft.getMinecraft().currentScreen == null) {
                simulateVoiceChatPress(false);
            } else {
                simulateVoiceChatPress(true);
            }
        } else if (this.currentMode.equals(MODES.TOGGLE)) {
            if (Keyboard.isKeyDown(this.pvKey)) {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    if(!this.isToggled){
                        this.isToggled = true;
                        this.state = !this.state;
                    }
                }
            } else {
                this.isToggled = false;
            }
            simulateVoiceChatPress(this.state);
        }else if (this.currentMode.equals(MODES.SPEAKING)) {
            this.state = false;
            if(voiceChatInstance.getLastRMSLevel() >= 2000){
                simulateVoiceChatPress(true);
            }else {
                simulateVoiceChatPress(false);
            }
        }
    }

    private void simulateVoiceChatPress(boolean status){
        try {
            this.voiceChatKey.set(this.voiceChatInstance, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public VoiceChat getVoiceChatInstance() {
        return voiceChatInstance;
    }
    public boolean isPvEnabled() {
        return pvEnabled;
    }
    public boolean isPVInitialised() {
        return isPVInitialised;
    }
    public boolean isVoiceChatLoaded() {
        return isVoiceChatLoaded;
    }
    public MODES getCurrentMode() {
        return currentMode;
    }
    public enum MODES {

        MUTE(0, "Push To Mute"),
        TOGGLE(1, "Toggle the VoiceChat"),
        SPEAKING(2, "Activate on Speaking (WIP)");

        int modeId;
        String modeText;
        private static MODES[] values;
        MODES(int modeId, String modeText) {
            this.modeId = modeId;
            this.modeText = modeText;
        }

        static {
            values = values();
        }

        public static MODES[] getValues() {
            return values;
        }

        public static MODES getModeById(int modeId) {
            MODES modes = null;
            for (MODES mode : values) {
                if (mode.modeId == modeId)
                    modes = mode;
            }
            return modes;
        }

        public static MODES getModeByText(String modeText) {
            MODES modes = null;
            for (MODES mode : values) {
                if (mode.modeText.equals(modeText))
                    modes = mode;
            }
            return modes;
        }

        public static String[] getAllTexts() {
            ArrayList<String> s = new ArrayList<>();
            for (MODES mode : values)
                s.add(mode.modeText);
            return s.<String>toArray(new String[0]);
        }
    }

}
