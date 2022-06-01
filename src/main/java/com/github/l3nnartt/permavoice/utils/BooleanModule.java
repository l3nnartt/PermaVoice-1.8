package com.github.l3nnartt.permavoice.utils;

import com.github.l3nnartt.permavoice.PermaVoice;
import net.labymod.ingamegui.ModuleCategory;
import net.labymod.ingamegui.ModuleCategoryRegistry;
import net.labymod.ingamegui.moduletypes.SimpleModule;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;

public class BooleanModule extends SimpleModule {

  public String getDisplayName() {
    return "PermaVoice";
  }

  public String getDisplayValue() {
    if (PermaVoice.getInstance().getActive()) return "ENABLED";
    return "DISABLED";
  }

  public String getDefaultValue() {
    return "ERROR";
  }

  public ControlElement.IconData getIconData() {
    return new ControlElement.IconData(Material.LEVER);
  }

  public void loadSettings() {}

  public String getSettingName() {
    return "PermaVoice Status";
  }

  public String getDescription() {
    return "PLACEHOLDER";
  }

  public int getSortingId() {
    return 0;
  }

  public ModuleCategory getCategory() {
    return ModuleCategoryRegistry.CATEGORY_EXTERNAL_SERVICES;
  }

  public String getControlName() {
    return "PermaVoice Status";
  }
}
