package com.github.l3nnartt.permavoice.gui;

import java.lang.reflect.Field;
import net.labymod.core.LabyModCore;
import net.labymod.ingamegui.Module;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class NumberElementCustom extends ControlElement {
  private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
  private Integer currentValue;
  private Consumer<Integer> changeListener;
  private GuiTextField textField;
  private Consumer<Integer> callback;
  private int minValue;
  private int maxValue;
  private boolean hoverUp;
  private boolean hoverDown;
  private int steps;
  private long fastTickerCounterValue;

  public NumberElementCustom(String displayName, final String configEntryName, IconData iconData) {
    super(displayName, configEntryName, iconData);
    this.minValue = 0;
    this.maxValue = 2147483647;
    this.steps = 1;
    this.fastTickerCounterValue = 0L;
    if (!configEntryName.isEmpty()) {
      try {
        this.currentValue = (Integer) ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings());
      } catch (IllegalAccessException var5) {
        var5.printStackTrace();
      } catch (NoSuchFieldException var6) {
        var6.printStackTrace();
      }
    }

    if (this.currentValue == null) {
      this.currentValue = this.minValue;
    }

    this.changeListener = new Consumer<Integer>() {
      public void accept(Integer accepted) {
        try {
          Field f = ModSettings.class.getDeclaredField(configEntryName);
          if (f.getType().equals(Integer.TYPE)) {
            f.set(LabyMod.getSettings(), accepted);
          } else {
            f.set(LabyMod.getSettings(), String.valueOf(accepted));
          }
        } catch (Exception var3) {
          var3.printStackTrace();
        }

        if (NumberElementCustom.this.callback != null) {
          NumberElementCustom.this.callback.accept(accepted);
        }

      }
    };
    this.createTextfield();
  }

  public NumberElementCustom(String displayName, IconData iconData, int currentValue) {
    super(displayName, (String) null, iconData);
    this.minValue = 0;
    this.maxValue = 2147483647;
    this.steps = 1;
    this.fastTickerCounterValue = 0L;
    this.currentValue = currentValue;
    this.changeListener = new Consumer<Integer>() {
      public void accept(Integer accepted) {
        if (NumberElementCustom.this.callback != null) {
          NumberElementCustom.this.callback.accept(accepted);
        }

      }
    };
    this.createTextfield();
  }

  public NumberElementCustom(final Module module, IconData iconData, String displayName, final String attribute) {
    super(module, iconData, displayName);
    this.minValue = 0;
    this.maxValue = 2147483647;
    this.steps = 1;
    this.fastTickerCounterValue = 0L;

    try {
      String attr = (String) module.getAttributes().get(attribute);
      this.currentValue = attr == null ? this.minValue : Integer.valueOf(attr);
    } catch (Exception var6) {
      var6.printStackTrace();
    }

    if (this.currentValue == null) {
      this.currentValue = this.minValue;
    }

    this.changeListener = new Consumer<Integer>() {
      public void accept(Integer accepted) {
        module.getAttributes().put(attribute, String.valueOf(accepted));
        module.loadSettings();
        if (NumberElementCustom.this.callback != null) {
          NumberElementCustom.this.callback.accept(accepted);
        }

      }
    };
    this.createTextfield();
  }

  public NumberElementCustom(String configEntryName, IconData iconData) {
    this(configEntryName, configEntryName, iconData);
  }

  public NumberElementCustom setMinValue(int minValue) {
    this.minValue = minValue;
    if (this.currentValue < this.minValue) {
      this.currentValue = this.minValue;
    }

    return this;
  }

  public NumberElementCustom setMaxValue(int maxValue) {
    this.maxValue = maxValue;
    if (this.currentValue > this.maxValue) {
      this.currentValue = this.maxValue;
    }

    return this;
  }

  public NumberElementCustom setRange(int min, int max) {
    this.setMinValue(min);
    this.setMaxValue(max);
    return this;
  }

  public NumberElementCustom setSteps(int steps) {
    this.steps = steps;
    return this;
  }

  public void createTextfield() {
    this.textField = new GuiTextField(-2, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, this.getObjectWidth(), 20);
    this.updateValue();
    this.textField.setFocused(false);
  }

  private void updateValue() {
    this.textField.setText(String.valueOf(this.currentValue));
  }

  public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
    super.draw(x, y, maxX, maxY, mouseX, mouseY);
    int width = this.getObjectWidth();
    if (this.textField != null) {
      LabyModCore.getMinecraft().setTextFieldXPosition(this.textField, maxX - width - 2);
      LabyModCore.getMinecraft().setTextFieldYPosition(this.textField, y + 1);
      this.textField.drawTextBox();
      LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, ModColor.toRGB(120, 120, 120, 120));
      DrawUtils draw = LabyMod.getInstance().getDrawUtils();
      Minecraft.getMinecraft().getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      if (this.isMouseOver() && this.fastTickerCounterValue != 0L) {
        if (this.fastTickerCounterValue > 0L && this.fastTickerCounterValue + 80L < System.currentTimeMillis()) {
          this.fastTickerCounterValue = System.currentTimeMillis();
          if (this.currentValue < this.maxValue) {
            this.currentValue = this.currentValue + this.steps;
            this.updateValue();
          }
        }

        if (this.fastTickerCounterValue < 0L && this.fastTickerCounterValue - 80L > System.currentTimeMillis() * -1L) {
          this.fastTickerCounterValue = System.currentTimeMillis() * -1L;
          if (this.currentValue > this.minValue) {
            this.currentValue = this.currentValue - this.steps;
            this.updateValue();
          }
        }
      } else {
        this.mouseRelease(mouseX, mouseY, 0);
      }

    }
  }

  public void unfocus(int mouseX, int mouseY, int mouseButton) {
    super.unfocus(mouseX, mouseY, mouseButton);
    this.textField.setFocused(false);
  }

  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
    super.mouseRelease(mouseX, mouseY, mouseButton);
  }

  public void keyTyped(char typedChar, int keyCode) {

  }

  public void updateScreen() {
    super.updateScreen();
    this.textField.updateCursorCounter();
  }

  public GuiTextField getTextField() {
    return this.textField;
  }

  public NumberElementCustom addCallback(Consumer<Integer> callback) {
    this.callback = callback;
    return this;
  }

  public int getObjectWidth() {
    return 50;
  }

  public Integer getCurrentValue() {
    return this.currentValue;
  }

  public void setCurrentValue(Integer currentValue) {
    this.currentValue = currentValue;
  }
}