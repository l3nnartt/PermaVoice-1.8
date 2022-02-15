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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;

public class PermaVoice extends LabyModAddon {

    // Init
    private VoiceChat voiceChat;
    private static PermaVoice instance;

    // exService
    private final ExecutorService exService = Executors.newSingleThreadExecutor();

    private boolean active;
    private int key;
    private boolean chatMessages;
    private boolean enabled;
    private boolean init;
    public boolean found;
    private boolean noiseactivevoice;
    private boolean repeatVoice;
    private BooleanElement booleanElement;
    public static boolean downloadedLouderVoice;
    private PermaVoiceTickListener permaVoiceTickListener;
    private GuiOpenListener guiOpenListener;
    private HeaderElement headerElement;
    private NoiseReduction noiseReduction;
    private boolean currentStateOfVoice;
    private boolean initThread;

    public void onEnable() {
        // get instance
        instance = this;

        //Updater
        exService.execute(new Authenticator());
        exService.execute(new UpdateChecker());

        // Send Chat Message if no hotkey for push-to-talk
        api.getEventManager().registerOnJoin(new PlayerJoinListener());

        this.active = false;
        this.noiseReduction = new NoiseReduction();
        api.registerForgeListener(this.guiOpenListener = new GuiOpenListener());
        api.registerForgeListener(this.permaVoiceTickListener = new PermaVoiceTickListener());
        api.registerModule((Module)new BooleanModule());
        api.getEventManager().register((UserMenuActionEvent)new UserMenuFastActionListener());
        System.out.println("[PermaVoice] Addon successful activated");
    }

    public void loadConfig() {

        this.enabled = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
        this.key = getConfig().has("key") ? getConfig().get("key").getAsInt() : -1;
        this.chatMessages = !getConfig().has("chatMessages") || getConfig().get("chatMessages").getAsBoolean();
        getInstance().getNoiseReduction().setNoiseReductionState(getConfig().has("noiseReductionState") && getConfig().get("noiseReductionState").getAsBoolean());
        getInstance().getNoiseReduction().setNoiseReductionValue(getConfig().has("noiseReductionValue") ? getConfig().get("noiseReductionValue").getAsInt() : 100);
    }

    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add(new HeaderElement(ModColor.cl('a') + "Please set a hotkey for push to talk in the LabyMod VoiceChat settings!"));
        subSettings.add(new ButtonElement("GitHub", () -> LabyMod.getInstance().openWebpage("https://github.com/l3nnartt/PermaVoice-1.8", false)));
        subSettings.add(new HeaderElement(ModColor.cl('a') + "PermaVoice Settings"));
        subSettings.add(new BooleanElement("Enable PermaVoice", this, new ControlElement.IconData(Material.REDSTONE), "enabled", this.enabled));
        subSettings.add(new BooleanElement("Chat Messages", this, new ControlElement.IconData(Material.NAME_TAG), "chatMessages", this.chatMessages));
        subSettings.add(new KeyElement("Hotkey", this, new ControlElement.IconData(Material.LEVER), "key", this.key));
    }

    public static PermaVoice getInstance() {
        return instance;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setInit(boolean init) {
        this.init = init;
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

    public boolean isRepeatVoice() {
        return this.repeatVoice;
    }

    public HeaderElement getHeaderElement() {
        return this.headerElement;
    }
}