package de.permavoice.manager;

import de.permavoice.PermaVoice;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAPI;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.lang.reflect.Field;
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

    public SettingsManager(PermaVoice permaVoice){
        this.permaVoice = permaVoice;
        this.api = permaVoice.api;

        this.isVoiceChatLoaded = false;

        api.registerForgeListener(this);
        this.isPVInitialised = false;
    }

    public void fillSettings(List<SettingsElement> subSettings){
        subSettings.add(new BooleanElement("Enabled", new ControlElement.IconData(Material.REDSTONE), aBoolean -> pvEnabled = aBoolean, pvEnabled));
        subSettings.add(new KeyElement("Key", permaVoice ,new ControlElement.IconData(Material.ACACIA_STAIRS), "key", this.pvKey));
    }

    public void onLoadConfig(){
        this.pvEnabled = !permaVoice.getConfig().has("enabled") || permaVoice.getConfig().get("enabled").getAsBoolean();
        this.pvKey = permaVoice.getConfig().has("key") ? permaVoice.getConfig().get("key").getAsInt() : -1;
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

    }


    private void simulateVoiceChatPress(boolean status){
        try {
            voiceChatKey.set(voiceChatInstance, status);
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
}
