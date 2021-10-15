package de.permavoice.manager;

import de.permavoice.PermaVoice;
import de.permavoice.elements.ButtonElement;
import net.labymod.addon.AddonLoader;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.api.LabyModAPI;
import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import scala.actors.threadpool.Executor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsManager implements VoiceEvent {

    private PermaVoice permaVoice;
    private LabyModAPI api;

    private boolean isVoiceChatLoaded;
    private boolean isPVInitialised;
    private VoiceChat voiceChatInstance;
    private Field voiceChatKey;


    private boolean pvEnabled;
    private int pvKey;

    private MODES currentMode;
    private boolean isToggled;
    private boolean state;

    private boolean shouldPlaySound;

    private String soundToPlay;

    private int voiceLvl;



    public SettingsManager(PermaVoice permaVoice){
        this.permaVoice = permaVoice;
        this.api = permaVoice.api;

        this.isVoiceChatLoaded = false;
        api.registerForgeListener(this);
        this.isPVInitialised = false;


    }

    public void fillSettings(List<SettingsElement> subSettings){
        subSettings.add(new HeaderElement("§7General Section"));
        subSettings.add(new BooleanElement("Enabled", new ControlElement.IconData(new ResourceLocation("permavoice/icons/enable.png")), aBoolean -> {
            pvEnabled = aBoolean;
        }, pvEnabled));
        subSettings.add(new KeyElement("Hotkey" ,new ControlElement.IconData(new ResourceLocation("permavoice/icons/wasd.png")), this.pvKey, integer -> {
            this.pvKey = integer;
        }));
        DropDownMenu<String> alignmentDropDownMenu = new DropDownMenu("Mode", 0, 0, 0, 0).fill(MODES.getAllTexts());
        alignmentDropDownMenu.setSelected(this.currentMode.modeText);
        DropDownElement<String> alignmentDropDown = new DropDownElement("Mode", alignmentDropDownMenu);
        alignmentDropDown.setChangeListener(modes -> {
            this.currentMode = MODES.getModeByText(modes);
        });
        subSettings.add(alignmentDropDown);

        subSettings.add(new HeaderElement("§7Sound Section"));
        subSettings.add(new BooleanElement("Play Sound", new ControlElement.IconData(new ResourceLocation("permavoice/icons/sound.png")), aBoolean -> {
            shouldPlaySound = aBoolean;
        }, shouldPlaySound));
        DropDownMenu<String> soundsMenu = new DropDownMenu("Sound", 0, 0, 0, 0).fill(getAllSounds().toArray(new String[0]));
        soundsMenu.setSelected(this.soundToPlay);
        DropDownElement<String> soundsDropDown = new DropDownElement("Mode", soundsMenu);
        soundsDropDown.setChangeListener(sound -> this.soundToPlay = sound);
        subSettings.add(soundsDropDown);
        subSettings.add(new HeaderElement("§7"));
        subSettings.add(new ButtonElement("Save Settings", null, "CLICK", controlElement -> {
            saveSettings();
        }));
    }

    public void onLoadConfig(){
        this.pvEnabled = permaVoice.getConfig().has("enabled") ? permaVoice.getConfig().get("enabled").getAsBoolean() : true;
        this.shouldPlaySound = permaVoice.getConfig().has("playsound") ? permaVoice.getConfig().get("playsound").getAsBoolean() : false;
        this.pvKey = permaVoice.getConfig().has("key") ? permaVoice.getConfig().get("key").getAsInt() : -1;
        this.currentMode = MODES.getModeById(this.permaVoice.getConfig().has("mode") ? this.permaVoice.getConfig().get("mode").getAsInt() : 1);
        this.soundToPlay = permaVoice.getConfig().has("soundToPlay") ? permaVoice.getConfig().get("soundToPlay").getAsString() : "random.bow";
    }

    private void saveSettings() {
        this.permaVoice.getConfig().addProperty("enabled", this.pvEnabled);
        this.permaVoice.getConfig().addProperty("currentMode", this.currentMode.modeId);
        this.permaVoice.getConfig().addProperty("soundToPlay", soundToPlay);
        this.permaVoice.getConfig().addProperty("playsound", shouldPlaySound);
        this.permaVoice.saveConfig();
    }

    @SubscribeEvent
    public void loadVoiceChat(TickEvent.ClientTickEvent event) {
        if(!isPVInitialised){
            for(LabyModAddon addon : AddonLoader.getAddons()){
                if (addon == null || addon.about == null || addon.about.name == null) {
                    continue;
                }
                if(addon.about.name.equals("VoiceChat") && addon instanceof VoiceChat){
                    voiceChatInstance = (VoiceChat) addon;
                    isVoiceChatLoaded = true;
                    System.out.println("START");
                    permaVoice.getVoiceActivationManager().startThread();
                    MinecraftForge.EVENT_BUS.unregister(voiceChatInstance);
                    try {
                        voiceChatKey = voiceChatInstance.getClass().getDeclaredField("pushToTalkPressed");
                        voiceChatKey.setAccessible(true);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            isPVInitialised = true;
        }
        if(isVoiceChatLoaded){
            voiceChatInstance.onTick(event);
        }
    }

    @SubscribeEvent
    public void handlePress(TickEvent.ClientTickEvent event){
        if (!this.isVoiceChatLoaded || this.voiceChatInstance.getKeyPushToTalk() == -1 || !this.pvEnabled || this.voiceChatInstance.isPushToTalkPressed())
            return;
        if (this.currentMode.equals(MODES.MUTE)) {
            this.state = false;
            if (Keyboard.isKeyDown(this.pvKey) && Minecraft.getMinecraft().currentScreen == null) {
                simulateVoiceChatPress(false);
            } else {
                simulateVoiceChatPress(true);
            }
        } else if (this.currentMode.equals(MODES.TOGGLE)) {
            if (Keyboard.isKeyDown(this.pvKey)) {
                if (Minecraft.getMinecraft().currentScreen == null) {
                    if(!this.isToggled){
                        this.isToggled = true;
                        this.state = !this.state;
                        if(shouldPlaySound){
                            Minecraft.getMinecraft().thePlayer.playSound(soundToPlay, 1.0f, 1.0f);
                        }
                    }
                }
            } else {
                this.isToggled = false;
            }
            simulateVoiceChatPress(this.state);
        }
        permaVoice.getVoiceActivationManager().setCaptureing(this.currentMode.equals(MODES.SPEAKING));
    }

    private void simulateVoiceChatPress(boolean status){
        try {
            this.voiceChatKey.set(this.voiceChatInstance, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public VoiceChat getVoiceChatInstance() {
        return voiceChatInstance;
    }
    public boolean isPvEnabled() {
        return pvEnabled;
    }
    public boolean isPVInitialised() {
        return isPVInitialised;
    }
    public boolean isVoiceChatLoaded() {
        return isVoiceChatLoaded;
    }
    public MODES getCurrentMode() {
        return currentMode;
    }

    @Override
    public void onVoice(int lvl) {

        api.displayMessageInChat(String.valueOf(permaVoice.getVoiceActivationManager().isCaptureing()));
        if (!this.isVoiceChatLoaded || this.voiceChatInstance.getKeyPushToTalk() == -1 || !this.pvEnabled || this.voiceChatInstance.isPushToTalkPressed())
            return;
        if(this.currentMode.equals(MODES.SPEAKING)){
            api.displayMessageInChat(lvl + "");
            if(lvl <= 1000){
                api.displayMessageInChat("SPEAKING");
                simulateVoiceChatPress(true);
            }else {
                simulateVoiceChatPress(false);
            }
        }

    }


    public enum MODES {

        MUTE(0, "Push To Mute"),
        TOGGLE(1, "Toggle the VoiceChat"),
        SPEAKING(2, "Activate on Speaking (WIP)");

        int modeId;
        String modeText;
        private static MODES[] values;
        MODES(int modeId, String modeText) {
            this.modeId = modeId;
            this.modeText = modeText;
        }

        static {
            values = values();
        }

        public static MODES[] getValues() {
            return values;
        }

        public static MODES getModeById(int modeId) {
            MODES modes = null;
            for (MODES mode : values) {
                if (mode.modeId == modeId)
                    modes = mode;
            }
            return modes;
        }

        public static MODES getModeByText(String modeText) {
            MODES modes = null;
            for (MODES mode : values) {
                if (mode.modeText.equals(modeText))
                    modes = mode;
            }
            return modes;
        }

        public static String[] getAllTexts() {
            ArrayList<String> s = new ArrayList<>();
            for (MODES mode : values)
                s.add(mode.modeText);
            return s.toArray(new String[0]);
        }
    }

    private ArrayList<String> getAllSounds(){
        ArrayList<String> sounds = new ArrayList<>();
        //General
        sounds.add("ambient.cave.cave");
        sounds.add("ambient.weather.rain");
        sounds.add("ambient.weather.thunder");
        sounds.add("game.player.hurt.fall.big");
        sounds.add("game.neutral.hurt.fall.big");
        sounds.add("game.hostile.hurt.fall.big");
        sounds.add("game.player.hurt.fall.small");
        sounds.add("game.neutral.hurt.fall.small");
        sounds.add("game.hostile.hurt.fall.small");
        sounds.add("game.player.hurt");
        sounds.add("game.neutral.hurt");
        sounds.add("game.hostile.hurt");
        sounds.add("game.player.die");
        sounds.add("game.neutral.die");
        sounds.add("game.hostile.die");
        sounds.add("game.potion.smash");
        sounds.add("dig.cloth");
        sounds.add("dig.glass");
        sounds.add("dig.grass");
        sounds.add("dig.gravel");
        sounds.add("dig.sand");
        sounds.add("dig.snow");
        sounds.add("dig.stone");
        sounds.add("dig.wood");
        sounds.add("fire.fire");
        sounds.add("fire.ignite");
        sounds.add("item.fireCharge.use");
        sounds.add("fireworks.blast");
        sounds.add("fireworks.blast_far");
        sounds.add("fireworks.largeBlast");
        sounds.add("fireworks.largeBlast_far");
        sounds.add("fireworks.launch");
        sounds.add("fireworks.twinkle");
        sounds.add("fireworks.twinkle_far");
        sounds.add("game.player.swim.splash");
        sounds.add("game.neutral.swim.splash");
        sounds.add("game.hostile.swim.splash");
        sounds.add("game.player.swim");
        sounds.add("game.neutral.swim");
        sounds.add("game.hostile.swim");
        sounds.add("liquid.lava");
        sounds.add("liquid.lavapop");
        sounds.add("liquid.water");
        sounds.add("minecart.base");
        sounds.add("minecart.inside");
        sounds.add("note.bass");
        sounds.add("note.bassattack");
        sounds.add("note.bd");
        sounds.add("note.harp");
        sounds.add("note.hat");
        sounds.add("note.pling");
        sounds.add("note.snare");
        sounds.add("portal.portal");
        sounds.add("portal.travel");
        sounds.add("portal.trigger");
        sounds.add("random.anvil_break");
        sounds.add("random.anvil_land");
        sounds.add("random.anvil_use");
        sounds.add("random.bow");
        sounds.add("random.bowhit");
        sounds.add("random.break");
        sounds.add("random.burp");
        sounds.add("random.chestclosed");
        sounds.add("random.chestopen");
        sounds.add("gui.button.press");
        sounds.add("random.click");
        sounds.add("random.door_open");
        sounds.add("random.door_close");
        sounds.add("random.drink");
        sounds.add("random.eat");
        sounds.add("random.explode");
        sounds.add("random.fizz");
        sounds.add("game.tnt.primed");
        sounds.add("creeper.primed");
        sounds.add("random.levelup");
        sounds.add("random.orb");
        sounds.add("random.pop");
        sounds.add("random.splash");
        sounds.add("random.successful_hit");
        sounds.add("random.wood_click");
        sounds.add("step.cloth");
        sounds.add("step.grass");
        sounds.add("step.gravel");
        sounds.add("step.ladder");
        sounds.add("step.sand");
        sounds.add("step.snow");
        sounds.add("step.stone");
        sounds.add("step.wood");
        sounds.add("tile.piston.in");
        sounds.add("tile.piston.out");
        //Mobs
        sounds.add("mob.bat.death");
        sounds.add("mob.bat.hurt");
        sounds.add("mob.bat.idle");
        sounds.add("mob.bat.loop");
        sounds.add("mob.bat.takeoff");
        sounds.add("mob.blaze.breathe");
        sounds.add("mob.blaze.death");
        sounds.add("mob.blaze.hit");
        sounds.add("mob.cat.hiss");
        sounds.add("mob.cat.hitt");
        sounds.add("mob.cat.meow");
        sounds.add("mob.cat.purr");
        sounds.add("mob.cat.purreow");
        sounds.add("mob.chicken.hurt");
        sounds.add("mob.chicken.plop");
        sounds.add("mob.chicken.say");
        sounds.add("mob.chicken.step");
        sounds.add("mob.cow.hurt");
        sounds.add("mob.cow.say");
        sounds.add("mob.cow.step");
        sounds.add("mob.creeper.death");
        sounds.add("mob.creeper.say");
        sounds.add("mob.enderdragon.end");
        sounds.add("mob.enderdragon.growl");
        sounds.add("mob.enderdragon.hit");
        sounds.add("mob.enderdragon.wings");
        sounds.add("mob.endermen.death");
        sounds.add("mob.endermen.hit");
        sounds.add("mob.endermen.idle");
        sounds.add("mob.endermen.portal");
        sounds.add("mob.endermen.scream");
        sounds.add("mob.endermen.stare");
        sounds.add("mob.ghast.affectionate_scream");
        sounds.add("mob.ghast.charge");
        sounds.add("mob.ghast.death");
        sounds.add("mob.ghast.fireball");
        sounds.add("mob.ghast.moan");
        sounds.add("mob.ghast.scream");
        sounds.add("mob.guardian.hit");
        sounds.add("mob.guardian.idle");
        sounds.add("mob.guardian.death");
        sounds.add("mob.guardian.elder.hit");
        sounds.add("mob.guardian.elder.idle");
        sounds.add("mob.guardian.elder.death");
        sounds.add("mob.guardian.land.hit");
        sounds.add("mob.guardian.land.idle");
        sounds.add("mob.guardian.land.death");
        sounds.add("mob.guardian.curse");
        sounds.add("mob.guardian.attack");
        sounds.add("mob.guardian.flop");
        sounds.add("mob.horse.angry");
        sounds.add("mob.horse.armor");
        sounds.add("mob.horse.breathe");
        sounds.add("mob.horse.death");
        sounds.add("mob.horse.donkey.angry");
        sounds.add("mob.horse.donkey.death");
        sounds.add("mob.horse.donkey.hit");
        sounds.add("mob.horse.donkey.idle");
        sounds.add("mob.horse.gallop");
        sounds.add("mob.horse.hit");
        sounds.add("mob.horse.idle");
        sounds.add("mob.horse.jump");
        sounds.add("mob.horse.land");
        sounds.add("mob.horse.leather");
        sounds.add("mob.horse.skeleton.death");
        sounds.add("mob.horse.skeleton.hit");
        sounds.add("mob.horse.skeleton.idle");
        sounds.add("mob.horse.soft");
        sounds.add("mob.horse.wood");
        sounds.add("mob.horse.zombie.death");
        sounds.add("mob.horse.zombie.hit");
        sounds.add("mob.horse.zombie.idle");
        sounds.add("mob.irongolem.death");
        sounds.add("mob.irongolem.hit");
        sounds.add("mob.irongolem.throw");
        sounds.add("mob.irongolem.walk");
        sounds.add("mob.magmacube.big");
        sounds.add("mob.magmacube.jump");
        sounds.add("mob.magmacube.small");
        sounds.add("mob.pig.death");
        sounds.add("mob.pig.say");
        sounds.add("mob.pig.step");
        sounds.add("mob.rabbit.hurt");
        sounds.add("mob.rabbit.idle");
        sounds.add("mob.rabbit.hop");
        sounds.add("mob.rabbit.death");
        sounds.add("mob.sheep.say");
        sounds.add("mob.sheep.shear");
        sounds.add("mob.sheep.step");
        sounds.add("mob.silverfish.hit");
        sounds.add("mob.silverfish.kill");
        sounds.add("mob.silverfish.say");
        sounds.add("mob.silverfish.step");
        sounds.add("mob.skeleton.death");
        sounds.add("mob.skeleton.hurt");
        sounds.add("mob.skeleton.say");
        sounds.add("mob.skeleton.step");
        sounds.add("mob.slime.attack");
        sounds.add("mob.slime.big");
        sounds.add("mob.slime.small");
        sounds.add("mob.spider.death");
        sounds.add("mob.spider.say");
        sounds.add("mob.spider.step");
        sounds.add("mob.villager.death");
        sounds.add("mob.villager.haggle");
        sounds.add("mob.villager.hit");
        sounds.add("mob.villager.idle");
        sounds.add("mob.villager.no");
        sounds.add("mob.villager.yes");
        sounds.add("mob.wither.death");
        sounds.add("mob.wither.hurt");
        sounds.add("mob.wither.idle");
        sounds.add("mob.wither.shoot");
        sounds.add("mob.wither.spawn");
        sounds.add("mob.wolf.bark");
        sounds.add("mob.wolf.death");
        sounds.add("mob.wolf.growl");
        sounds.add("mob.wolf.howl");
        sounds.add("mob.wolf.hurt");
        sounds.add("mob.wolf.panting");
        sounds.add("mob.wolf.shake");
        sounds.add("mob.wolf.step");
        sounds.add("mob.wolf.whine");
        sounds.add("mob.zombie.death");
        sounds.add("mob.zombie.hurt");
        sounds.add("mob.zombie.infect");
        sounds.add("mob.zombie.metal");
        sounds.add("mob.zombie.remedy");
        sounds.add("mob.zombie.say");
        sounds.add("mob.zombie.step");
        sounds.add("mob.zombie.unfect");
        sounds.add("mob.zombie.wood");
        sounds.add("mob.zombie.woodbreak");
        sounds.add("mob.zombiepig.zpig");
        sounds.add("mob.zombiepig.zpigangry");
        sounds.add("mob.zombiepig.zpigdeath");
        sounds.add("mob.zombiepig.zpighurt");
        return sounds;
    }

}
