package com.github.l3nnartt.permavoice;

import com.github.l3nnartt.permavoice.gui.ButtonElement;
import com.github.l3nnartt.permavoice.listener.GuiOpenListener;
import com.github.l3nnartt.permavoice.listener.PermaVoiceTickListener;
import com.github.l3nnartt.permavoice.listener.PlayerJoinListener;
import com.github.l3nnartt.permavoice.listener.UserMenuFastActionListener;
import com.github.l3nnartt.permavoice.modules.BooleanModule;
import com.github.l3nnartt.permavoice.noisereduction.NoiseReduction;
import com.github.l3nnartt.permavoice.noisereduction.Utils;
import com.github.l3nnartt.permavoice.updater.Authenticator;
import com.github.l3nnartt.permavoice.updater.UpdateChecker;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.UserMenuActionEvent;
import net.labymod.ingamegui.Module;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.NumberElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;

public class PermaVoice extends LabyModAddon {

    private VoiceChat voiceChat;
    private boolean active;
    private int key;
    private boolean chatMessages;
    private boolean enabled;
    private boolean cosmeticsMod;
    private boolean init;
    public boolean found;
    private boolean noiseactivevoice;
    private boolean repeatVoice;
    private BooleanElement booleanElement;
    public static boolean downloadedLouderVoice;
    public static boolean hdCapes;
    private PermaVoiceTickListener permaVoiceTickListener;
    private GuiOpenListener guiOpenListener;
    private HeaderElement headerElement;
    private NoiseReduction noiseReduction;
    private boolean currentStateOfVoice;
    private boolean initThread;
    private UpdateChecker updateChecker;
    public static int serverVersion;
    private static PermaVoice service;
    private Authenticator auth = new Authenticator();
    public static final ArrayList<String> PLAYERS = new ArrayList<>();
    public static final JsonParser PARSER = new JsonParser();
    public static final int CLIENT_VERSION = 40;
    public static final String CLIENT_VERSIONPRETTY = "3.0";

    public void onEnable() {
        service = this;
        this.active = false;
        this.updateChecker = new UpdateChecker();
        this.updateChecker.check();
        this.noiseReduction = new NoiseReduction();
        this.api.registerForgeListener(this.guiOpenListener = new GuiOpenListener());
        this.cosmeticsMod = false;
        this.api.registerForgeListener(this.permaVoiceTickListener = new PermaVoiceTickListener());
        this.api.registerModule((Module)new BooleanModule());
        this.api.getEventManager().registerOnJoin((Consumer)new PlayerJoinListener());
        this.api.getEventManager().register((UserMenuActionEvent)new UserMenuFastActionListener());
    }

    public void loadConfig() {
        this.enabled = getConfig().has("enabled") ? getConfig().get("enabled").getAsBoolean() : true;
        this.key = getConfig().has("key") ? getConfig().get("key").getAsInt() : -1;
        this.chatMessages = getConfig().has("chatmessages") ? getConfig().get("chatmessages").getAsBoolean() : true;
        downloadedLouderVoice = getConfig().has("downloadedloudervoice") ? getConfig().get("downloadedloudervoice").getAsBoolean() : false;
        hdCapes = getConfig().has("hdCapes") ? getConfig().get("hdCapes").getAsBoolean() : false;
        getService().getNoiseReduction().setNoiseReductionState(getConfig().has("noiseReductionState") ? getConfig().get("noiseReductionState").getAsBoolean() : false);
        getService().getNoiseReduction().setNoiseReductionValue(getConfig().has("noiseReductionValue") ? getConfig().get("noiseReductionValue").getAsInt() : 100);
    }

    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new HeaderElement(ModColor.cl('a') + "PermaVoice v" + "3.0" + " by Timo#0187"));
        subSettings.add(new HeaderElement(ModColor.cl('a') + "Please set a hotkey for push to talk in the LabyMod VoiceChat settings!"));
        subSettings.add(new ButtonElement("Discord", () -> LabyMod.getInstance().openWebpage("https://discord.gg/VhmBcuCGcm", true)));
        subSettings.add(new HeaderElement(ModColor.cl('a') + "PermaVoice Settings"));
        subSettings.add(new BooleanElement("Enable PermaVoice", this, new ControlElement.IconData(Material.REDSTONE), "enabled", this.enabled));
        subSettings.add(new BooleanElement("Chat Messages", this, new ControlElement.IconData(Material.NAME_TAG), "chatmessages", this.chatMessages));
        subSettings.add(new KeyElement("Key", this, new ControlElement.IconData(Material.LEVER), "key", this.key));
        subSettings.add(new HeaderElement(ModColor.cl('a') + "Voice activation Settings (Beta) | DEACTIVATED!!!"));
        subSettings.add(new HeaderElement(ModColor.cl('a') + "Any bugs found? Contact us on Discord!"));
        subSettings.add(new BooleanElement("Voice activation", this, new ControlElement.IconData(Material.NAME_TAG), "noiseReductionState", getService().getNoiseReduction().isNoiseReduction()));
        subSettings.add((new NumberElement("Voice activation level", new ControlElement.IconData(Material.WATER_BUCKET), this.noiseReduction.getNoiseReductionValue() / Utils.voicedetectionMFactor)).addCallback(integer -> {
            if (this.voiceChat.isConnected()) {
                getService().getNoiseReduction().setNoiseReductionValue(integer.intValue() * Utils.voicedetectionMFactor);
                getConfig().addProperty("noiseReductionValue", Integer.valueOf(integer.intValue() * Utils.voicedetectionMFactor));
                saveConfig();
            }
        }));
        this.booleanElement = (new BooleanElement("Check Voice Volume", this, new ControlElement.IconData(Material.LAVA_BUCKET), "repeatVoice", this.repeatVoice)).addCallback(aBoolean -> this.repeatVoice = aBoolean.booleanValue());
        subSettings.add(this.booleanElement);
        this.headerElement = new HeaderElement(ModColor.cl('a') + "Your VoiceChat Volume " + this.noiseReduction.getNoiseReductionValueGUI());
        subSettings.add(this.headerElement);
    }

    public static PermaVoice getService() {
        return service;
    }

    public boolean getActive() {
        return this.active;
    }

    public UpdateChecker getUpdateChecker() {
        return this.updateChecker;
    }

    public boolean getCosmeticsMod() {
        return this.cosmeticsMod;
    }

    public void setCosmeticsMod(boolean cosmeticsMod) {
        this.cosmeticsMod = cosmeticsMod;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public Authenticator getAuth() {
        return this.auth;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getKey() {
        return this.key;
    }

    public boolean isFound() {
        return this.found;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isInit() {
        return this.init;
    }

    public boolean isChatMessages() {
        return this.chatMessages;
    }

    public VoiceChat getVoiceChat() {
        return this.voiceChat;
    }

    public void setVoiceChat(VoiceChat voiceChat) {
        this.voiceChat = voiceChat;
    }

    public boolean isActive() {
        return this.active;
    }

    public NoiseReduction getNoiseReduction() {
        return this.noiseReduction;
    }

    public PermaVoiceTickListener getPermaVoiceTickListener() {
        return this.permaVoiceTickListener;
    }

    public boolean isInitThread() {
        return this.initThread;
    }

    public void setInitThread(boolean initThread) {
        this.initThread = initThread;
    }

    public void setCurrentStateOfVoice(boolean currentStateOfVoice) {
        this.currentStateOfVoice = currentStateOfVoice;
    }

    public boolean isNoiseactivevoice() {
        return this.noiseactivevoice;
    }

    public boolean isCosmeticsMod() {
        return this.cosmeticsMod;
    }

    public boolean isRepeatVoice() {
        return this.repeatVoice;
    }

    public HeaderElement getHeaderElement() {
        return this.headerElement;
    }

    public static boolean isHdCapes() {
        return hdCapes;
    }

    public static void setHdCapes(boolean hdCapes) {
        PermaVoice.hdCapes = hdCapes;
    }
}