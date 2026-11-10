package com.silla;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

/** Reproduce el audio recibido, mas bajo cuanto mas lejos estes. Usa el volumen "Voz" del juego. */
public final class VoicePlayer {
    private static final Logger LOG = LoggerFactory.getLogger("silla");

    private VoicePlayer() {}

    public static void play(VoiceS2C p) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        double dist = Math.sqrt(mc.player.squaredDistanceTo(p.x(), p.y(), p.z()));
        float cerca = (float) Math.max(0.0, 1.0 - dist / Math.max(1.0F, p.radius()));
        float vol = cerca
                * mc.options.getSoundVolume(SoundCategory.MASTER)
                * mc.options.getSoundVolume(SoundCategory.VOICE);
        if (vol <= 0.001F) return;

        final byte[] pcm = p.pcm();
        final int rate = p.sampleRate();
        final float volumen = vol;
        Thread t = new Thread(() -> {
            try {
                AudioFormat fmt = new AudioFormat(rate, 16, 1, true, false); // PCM 16-bit mono little-endian
                Clip clip = AudioSystem.getClip();
                clip.open(fmt, pcm, 0, pcm.length - (pcm.length % 2));
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl g = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float db = (float) (20.0 * Math.log10(Math.max(volumen, 0.0001F)));
                    g.setValue(Math.max(g.getMinimum(), Math.min(g.getMaximum(), db)));
                }
                clip.addLineListener(ev -> {
                    if (ev.getType() == LineEvent.Type.STOP) clip.close();
                });
                clip.start();
            } catch (Exception e) {
                LOG.error("No pude reproducir la voz de la tablet: {}", e.toString());
            }
        }, "silla-voz");
        t.setDaemon(true);
        t.start();
    }
}
