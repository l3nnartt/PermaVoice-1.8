package de.permavoice;

import de.permavoice.manager.SettingsManager;
import de.permavoice.updater.UpdateChecker;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermaVoice extends LabyModAddon {

    private SettingsManager settingsManager;
    private final ExecutorService exService = Executors.newSingleThreadExecutor();

    @Override
    public void onEnable() {
        this.settingsManager = new SettingsManager(this);
       // exService.execute(new UpdateChecker());
    }

    @Override
    public void loadConfig() {
        this.settingsManager.onLoadConfig();
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        settingsManager.fillSettings(list);
    }
}
