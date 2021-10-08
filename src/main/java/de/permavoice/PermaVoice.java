package de.permavoice;

import de.permavoice.manager.SettingsManager;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class PermaVoice extends LabyModAddon {

    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        this.settingsManager = new SettingsManager(this);
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
