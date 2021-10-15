package de.permavoice;

import de.permavoice.manager.SettingsManager;
import de.permavoice.manager.VoiceActivationManager;
import de.permavoice.updater.UpdateChecker;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermaVoice extends LabyModAddon {

    private static SettingsManager settingsManager;
    private static VoiceActivationManager voiceActivationManager;

    private final ExecutorService exService = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        settingsManager = new SettingsManager(this);
        voiceActivationManager = new VoiceActivationManager();
        voiceActivationManager.registerEvent(settingsManager);
       // exService.execute(new UpdateChecker());
    }

    @Override
    public void loadConfig() {
        settingsManager.onLoadConfig();
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        settingsManager.fillSettings(list);
    }

    public static VoiceActivationManager getVoiceActivationManager() {
        return voiceActivationManager;
    }

    public static SettingsManager getSettingsManager() {
        return settingsManager;
    }
}
