package de.permavoice.manager;

import de.permavoice.PermaVoice;
import net.labymod.addons.voicechat.VoiceChat;
import net.labymod.addons.voicechat.audio.Microphone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoiceActivationManager {
    private static final int BYTESIZE = 8000;


    private ByteArrayOutputStream byteArrayOutputStream;
    private TargetDataLine targetDataLine;
    private int cnt;
    private boolean captureing;
    private byte[] tempBuffer;
    private int countzero;
    private short[] convert;
    private int voiceLvl;

    private ArrayList<VoiceEvent> events;

    private ExecutorService service = Executors.newSingleThreadExecutor();

    public  VoiceActivationManager(){
        tempBuffer = new byte[BYTESIZE];
        convert = new short[tempBuffer.length];
        events = new ArrayList<>();
    }

    public void startThread(){
        service.execute(this::run);
    }

    public void registerEvent(VoiceEvent event){
        events.add(event);
    }

    private void run(){
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            while (true){
                System.out.println("DIFF");
                if(PermaVoice.getSettingsManager().getVoiceChatInstance().getMicrophone() != null){
                    targetDataLine = PermaVoice.getSettingsManager().getVoiceChatInstance().getMicrophone().getTargetDataLine();
                    cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    byteArrayOutputStream.write(tempBuffer,0,cnt);
                    try {
                        countzero = 0;
                        for (int i = 0; i < tempBuffer.length; i++) {
                            convert[i] = tempBuffer[i];
                            if(convert[i] == 0){
                                countzero++;
                            }
                        }
                        System.out.println(countzero);
                        if(captureing){

                            for(VoiceEvent event : events){
                                event.onVoice(countzero);
                            }
                        }
                }catch (StringIndexOutOfBoundsException e) {
                        System.out.println(e.getMessage());
                    }
                    Thread.sleep(5);
                    targetDataLine.close();

                }else {
                    System.out.println("NULL");
                }

            }


        }catch (Exception e) {
            System.out.println(e);
        }

    }

    public void setVoiceLvl(int voiceLvl) {
        this.voiceLvl = voiceLvl;
    }

    public int getVoiceLvl() {
        return voiceLvl;
    }

    public void setCaptureing(boolean captureing) {
        this.captureing = captureing;
    }

    public boolean isCaptureing() {
        return captureing;
    }
}
