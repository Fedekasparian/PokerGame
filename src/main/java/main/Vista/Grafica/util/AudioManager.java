package main.Vista.Grafica.util;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private final Map<String, Clip> clips = new HashMap<>();

    public Clip load(String key, String resourcePath) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) throw new IllegalArgumentException("No existe audio: " + resourcePath);

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clips.put(key, clip);
            return clip;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando audio: " + resourcePath, e);
        }
    }

    public void play(String key) {
        Clip clip = clips.get(key);
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void loop(String key) {
        Clip clip = clips.get(key);
        if (clip == null) return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop(String key) {
        Clip clip = clips.get(key);
        if (clip != null) clip.stop();
    }

    public void volume(String key, float vol0to1) {
        Clip clip = clips.get(key);
        if (clip == null) return;
        FloatControl vc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // conversión simple a dB (evita -inf)
        float v = Math.max(0.0001f, Math.min(1f, vol0to1));
        float db = (float) (20.0 * Math.log10(v));
        db = Math.max(vc.getMinimum(), Math.min(vc.getMaximum(), db));
        vc.setValue(db);
    }
}

