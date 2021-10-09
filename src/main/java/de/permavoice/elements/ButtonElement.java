package de.permavoice.elements;

import java.awt.Color;
import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.minecraft.client.gui.GuiButton;

public class ButtonElement extends ControlElement {
  private final GuiButton button = new GuiButton(-2, 0, 0, 0, 20, "");
  
  private final Consumer<ControlElement> clickListener;
  
  private boolean enabled = true;
  
  public ButtonElement(String displayName, ControlElement.IconData iconData, String inButtonName, Consumer<ControlElement> clickListener) {
    super(displayName, iconData);
    this.button.displayString = inButtonName;
    this.clickListener = clickListener;
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (this.button.mousePressed(this.mc, mouseX, mouseY)) {
      this.button.playPressSound(this.mc.getSoundHandler());
      this.clickListener.accept(this);
    } 
  }
  
  public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
    super.draw(x, y, maxX, maxY, mouseX, mouseY);
    if (this.displayName != null)
      LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, Color.GRAY.getRGB()); 
    int stringWidth = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(this.button.displayString);
    int buttonWidth = (this.displayName == null) ? (maxX - x) : (stringWidth + 20);
    this.button.setWidth(buttonWidth);
    this.button.enabled = this.enabled;
    LabyModCore.getMinecraft().setButtonXPosition(this.button, maxX - buttonWidth - 2);
    LabyModCore.getMinecraft().setButtonYPosition(this.button, y + 1);
    LabyModCore.getMinecraft().drawButton(this.button, mouseX, mouseY);
  }
  
  public void setSettingEnabled(boolean settingEnabled) {
    this.enabled = settingEnabled;
    this.button.enabled = settingEnabled;
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
}
