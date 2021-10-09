//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package de.permavoice.elements;

import java.io.IOException;
import net.labymod.api.LabyModAddon;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.ingamegui.Module;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.PreviewRenderer;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class StringElementCustom extends ControlElement {
    private String currentValue;
    private Consumer<String> changeListener;
    private ModTextField textField;
    private Consumer<String> callback;
    private boolean hoverExpandButton;
    private boolean isWrong;

    public StringElementCustom(String displayName, final String configEntryName, IconData iconData) {
        super(displayName, configEntryName, iconData);
        this.hoverExpandButton = false;
        if (!configEntryName.isEmpty()) {
            try {
                this.currentValue = (String)ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings());
            } catch (IllegalAccessException var5) {
                var5.printStackTrace();
            } catch (NoSuchFieldException var6) {
                var6.printStackTrace();
            }
        }

        if (this.currentValue == null) {
            this.currentValue = "";
        }

        this.changeListener = new Consumer<String>() {
            public void accept(String accepted) {
                try {
                    ModSettings.class.getDeclaredField(configEntryName).set(LabyMod.getSettings(), accepted);
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

                if (StringElementCustom.this.callback != null) {
                    StringElementCustom.this.callback.accept(accepted);
                }

            }
        };
        this.createTextfield();
    }

    public StringElementCustom(final Module module, IconData iconData, String displayName, final String attribute) {
        super(module, iconData, displayName);
        this.hoverExpandButton = false;
        this.currentValue = (String)module.getAttributes().get(attribute);
        if (this.currentValue == null) {
            this.currentValue = "";
        }

        this.changeListener = new Consumer<String>() {
            public void accept(String accepted) {
                module.getAttributes().put(attribute, accepted);
                module.loadSettings();
                if (StringElementCustom.this.callback != null) {
                    StringElementCustom.this.callback.accept(accepted);
                }

            }
        };
        this.createTextfield();
    }

    public StringElementCustom(String displayName, final LabyModAddon addon, IconData iconData, final String attribute, String currentValue) {
        super(displayName, iconData);
        this.hoverExpandButton = false;
        if (currentValue == null) {
            currentValue = "";
        }

        this.currentValue = currentValue;
        this.changeListener = new Consumer<String>() {
            public void accept(String accepted) {
                addon.getConfig().addProperty(attribute, accepted);
                addon.loadConfig();
                if (StringElementCustom.this.callback != null) {
                    StringElementCustom.this.callback.accept(accepted);
                }

            }
        };
        this.createTextfield();
    }

    public StringElementCustom(String displayName, IconData iconData, String currentValue, Consumer<String> changeListener) {
        super(displayName, iconData);
        this.hoverExpandButton = false;
        this.currentValue = currentValue;
        this.changeListener = changeListener;
        this.createTextfield();
    }

    public StringElementCustom(String configEntryName, IconData iconData) {
        this(configEntryName, configEntryName, iconData);
    }

    public void createTextfield() {
        this.textField = new ModTextField(-2, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, this.getObjectWidth() - 5, 20);
        this.textField.setMaxStringLength(500);
        this.updateValue();
        this.textField.setCursorPositionEnd();
        this.textField.setFocused(false);
    }

    private void updateValue() {
        this.textField.setText(this.currentValue == null ? "" : this.currentValue);
    }

    public void draw(int x, int y, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x, y, maxX, maxY, mouseX, mouseY);
        int width = this.getObjectWidth() - 5;
        if (this.textField != null) {
            this.textField.xPosition = maxX - width - 2;
            this.textField.yPosition = y + 1;
            this.textField.drawTextBox();
            LabyMod.getInstance().getDrawUtils().drawRectangle(x - 1, y, x, maxY, ModColor.toRGB(120, 120, 120, 120));
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.BUTTON_EXPAND);
            this.hoverExpandButton = mouseX > maxX - this.getObjectWidth() - 12 && mouseX < maxX - this.getObjectWidth() - 7 + 8 && mouseY > y + 1 && mouseY < y + 1 + 8;
            LabyMod.getInstance().getDrawUtils().drawTexture((double)(maxX - this.getObjectWidth() - 7), (double)(y + 1), 0.0D, this.hoverExpandButton ? 130.0D : 0.0D, 256.0D, 128.0D, 8.0D, 8.0D);
        }
    }

    public void unfocus(int mouseX, int mouseY, int mouseButton) {
        super.unfocus(mouseX, mouseY, mouseButton);
        if (this.hoverExpandButton) {
            this.hoverExpandButton = false;
            Minecraft.getMinecraft().displayGuiScreen(new ExpandedStringElementGuiCustom(this.textField, Minecraft.getMinecraft().currentScreen, new Consumer<ModTextField>() {
                public void accept(ModTextField accepted) {
                    StringElementCustom.this.textField.setText(accepted.getText());
                    StringElementCustom.this.textField.setFocused(true);
                    StringElementCustom.this.textField.setCursorPosition(accepted.getCursorPosition());
                    StringElementCustom.this.textField.setSelectionPos(accepted.getSelectionEnd());
                    StringElementCustom.this.changeListener.accept(StringElementCustom.this.textField.getText());
                }
            }));
        }

        this.textField.setFocused(false);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, 0);
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (this.textField.textboxKeyTyped(typedChar, keyCode)) {
            this.changeListener.accept(this.textField.getText());
        }

    }

    public void updateScreen() {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    public StringElementCustom maxLength(int maxLength) {
        this.textField.setMaxStringLength(maxLength);
        return this;
    }

    public StringElementCustom addCallback(Consumer<String> callback) {
        this.callback = callback;
        return this;
    }

    public int getObjectWidth() {
        return 85;
    }

    public ModTextField getTextField() {
        return textField;
    }

    public void setWrong(boolean wrong) {
        isWrong = wrong;
        String dN = ModColor.removeColor(getDisplayName());
        setDisplayName(isWrong ? ModColor.cl("c") + dN : ModColor.cl("a") + dN);
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public class ExpandedStringElementGuiCustom extends GuiScreen {
        private GuiScreen backgroundScreen;
        private Consumer<ModTextField> callback;
        private ModTextField preField;
        private ModTextField expandedField;

        public ExpandedStringElementGuiCustom(ModTextField preField, GuiScreen backgroundScreen, Consumer<ModTextField> callback) {
            this.backgroundScreen = backgroundScreen;
            this.callback = callback;
            this.preField = preField;
        }

        public void initGui() {
            super.initGui();
            this.backgroundScreen.width = this.width;
            this.backgroundScreen.height = this.height;
            if (this.backgroundScreen instanceof LabyModModuleEditorGui) {
                PreviewRenderer.getInstance().init(ExpandedStringElementGuiCustom.class);
            }

            this.expandedField = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), this.width / 2 - 150, this.height / 4 + 45, 300, 20);
            this.expandedField.setMaxStringLength(this.preField.getMaxStringLength());
            this.expandedField.setFocused(true);
            this.expandedField.setText(this.preField.getText());
            this.expandedField.setCursorPosition(this.preField.getCursorPosition());
            this.expandedField.setSelectionPos(this.preField.getSelectionEnd());
            this.buttonList.add(new GuiButton(1, this.width / 2 - 50, this.height / 4 + 85, 100, 20, LanguageManager.translate("button_done")));
        }

        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.backgroundScreen.drawScreen(mouseX, mouseY, partialTicks);
            drawRect(0, 0, this.width, this.height, -2147483648);
            drawRect(this.width / 2 - 165, this.height / 4 + 35, this.width / 2 + 165, this.height / 4 + 120, -2147483648);
            this.expandedField.drawTextBox();
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.expandedField.mouseClicked(mouseX, mouseY, mouseButton);
            this.callback.accept(this.expandedField);
        }

        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
            }

            if (this.expandedField.textboxKeyTyped(typedChar, keyCode)) {
                this.callback.accept(this.expandedField);
            }

        }

        public void updateScreen() {
            this.backgroundScreen.updateScreen();
            this.expandedField.updateCursorCounter();
        }

        protected void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);
            if (button.id == 1) {
                Minecraft.getMinecraft().displayGuiScreen(this.backgroundScreen);
            }

        }

        public GuiScreen getBackgroundScreen() {
            return this.backgroundScreen;
        }
    }
}
