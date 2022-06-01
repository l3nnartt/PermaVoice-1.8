package com.github.l3nnartt.permavoice;

import com.github.l3nnartt.permavoice.gui.ButtonElement;
import com.github.l3nnartt.permavoice.listener.GuiOpenListener;
import com.github.l3nnartt.permavoice.listener.PermaVoiceTickListener;
import com.github.l3nnartt.permavoice.listener.PlayerJoinListener;
import com.github.l3nnartt.permavoice.updater.Authenticator;
import com.github.l3nnartt.permavoice.updater.UpdateChecker;
import com.github.l3nnartt.permavoice.utils.BooleanModule;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.*;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;

public class PermaVoice extends LabyModAddon {

  // Logger
  private static final Logger LOGGER = Logger.getLogger("PermaVoice");
  private static final String PREFIX = "[PermaVoice] ";

  // Addon instance
  private static PermaVoice instance;
  // exService
  private final ExecutorService exService = Executors.newSingleThreadExecutor();

  // Init
  private VoiceChat voiceChat;

  // Hotkey
  private int key;

  // Booleans
  private boolean active;
  private boolean labyAddons;
  private boolean chatMessages;
  private boolean enabled;
  private boolean init;
  private boolean found;
  private boolean updateAvailable;

  // Util
  private HeaderElement headerElement;
  private PermaVoiceTickListener permaVoiceTickListener;

  public void onEnable() {
    // instance
    instance = this;

    // Updater
    exService.execute(new Authenticator());
    exService.execute(new UpdateChecker());

    // Send chat message if no hotkey for push-to-talk & if update is available
    api.getEventManager().registerOnJoin(new PlayerJoinListener());

    // Register forge listener
    api.registerForgeListener(new GuiOpenListener());
    api.registerForgeListener(this.permaVoiceTickListener = new PermaVoiceTickListener());

    // Register module
    api.registerModule(new BooleanModule());

    // Start debug
    getLogger("Addon successful activated");
  }

  public void loadConfig() {
    this.labyAddons = getConfig().has("labyAddons") && getConfig().get("labyAddons").getAsBoolean();
    this.enabled = !getConfig().has("enabled") || getConfig().get("enabled").getAsBoolean();
    this.key = getConfig().has("key") ? getConfig().get("key").getAsInt() : -1;
    this.chatMessages =
        !getConfig().has("chatMessages") || getConfig().get("chatMessages").getAsBoolean();
  }

  protected void fillSettings(List<SettingsElement> subSettings) {
    subSettings.add(
        new HeaderElement(
            ModColor.cl('a')
                + "Please set a hotkey for Push-To-Talk in the LabyMod VoiceChat settings!"));
    subSettings.add(
        new ButtonElement(
            "GitHub",
            () ->
                LabyMod.getInstance()
                    .openWebpage("https://github.com/l3nnartt/PermaVoice-1.8", false)));
    subSettings.add(new HeaderElement(ModColor.cl('a') + "PermaVoice Settings"));
    subSettings.add(
        new BooleanElement(
            "Enable PermaVoice",
            this,
            new ControlElement.IconData(Material.REDSTONE),
            "enabled",
            this.enabled));
    subSettings.add(
        new BooleanElement(
            "Chat Messages",
            this,
            new ControlElement.IconData(Material.NAME_TAG),
            "chatMessages",
            this.chatMessages));
    subSettings.add(
        new KeyElement(
            "Hotkey", this, new ControlElement.IconData(Material.LEVER), "key", this.key));
  }

  public static void getLogger(String log) {
    LOGGER.info(PREFIX + log);
  }

  public static PermaVoice getInstance() {
    return instance;
  }

  public boolean getActive() {
    return this.active;
  }

  public boolean isFound() {
    return this.found;
  }

  public void setFound(boolean found) {
    this.found = found;
  }

  public int getKey() {
    return this.key;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean isInit() {
    return this.init;
  }

  public void setInit(boolean init) {
    this.init = init;
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

  public void setActive(boolean active) {
    this.active = active;
  }

  public PermaVoiceTickListener getPermaVoiceTickListener() {
    return this.permaVoiceTickListener;
  }

  public boolean isUpdateAvailable() {
    return updateAvailable;
  }

  public void setUpdateAvailable(boolean updateAvailable) {
    this.updateAvailable = updateAvailable;
  }
}
