package com.silla;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/** Lado servidor: pide el audio a Fish Audio y se lo manda a los jugadores cercanos. */
public final class TtsService {
    private static final Logger LOG = LoggerFactory.getLogger("silla");
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final Map<UUID, Long> ULTIMO = new ConcurrentHashMap<>();

    private TtsService() {}

    public static void hablar(ServerPlayerEntity player, String texto) {
        // Solo vale si de verdad lleva la tablet en la mano (evita que otro cliente mande paquetes a mano).
        if (!(player.getMainHandStack().getItem() instanceof TabletItem)
                && !(player.getOffHandStack().getItem() instanceof TabletItem)) {
            return;
        }
        SillaConfig cfg = SillaConfig.get();
        String limpio = limpiar(texto, cfg.maxCaracteres);
        if (limpio.isEmpty()) return;

        if (cfg.apiKey == null || cfg.apiKey.isBlank()) {
            aviso(player, "[Tablet] Falta la apiKey de Fish Audio en config/silla_tablet.json", Formatting.RED);
            return;
        }
        long ahora = System.currentTimeMillis();
        Long antes = ULTIMO.get(player.getUuid());
        if (antes != null && ahora - antes < cfg.cooldownSegundos * 1000L) {
            aviso(player, "[Tablet] Espera un momento...", Formatting.YELLOW);
            return;
        }
        ULTIMO.put(player.getUuid(), ahora);

        MinecraftServer server = player.getServer();
        if (server == null) return;
        ServerWorld mundo = player.getServerWorld();
        double x = player.getX(), y = player.getEyeY(), z = player.getZ();

        CompletableFuture.supplyAsync(() -> pedirAudio(cfg, limpio)).whenComplete((pcm, error) ->
                server.execute(() -> {
                    if (error != null || pcm == null) {
                        Throwable t = error != null ? (error.getCause() != null ? error.getCause() : error) : null;
                        LOG.error("Fish Audio fallo: {}", t == null ? "sin audio" : t.toString());
                        aviso(player, "[Tablet] No pude generar la voz (mira el log del servidor).", Formatting.RED);
                        return;
                    }
                    difundir(server, mundo, pcm, cfg, x, y, z);
                }));
    }

    private static byte[] pedirAudio(SillaConfig cfg, String texto) {
        try {
            JsonObject cuerpo = new JsonObject();
            cuerpo.addProperty("text", texto);
            cuerpo.addProperty("reference_id", cfg.referenceId);
            cuerpo.addProperty("format", "pcm");
            cuerpo.addProperty("sample_rate", cfg.sampleRate);
            cuerpo.addProperty("normalize", true);

            HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.fish.audio/v1/tts"))
                    .timeout(Duration.ofSeconds(40))
                    .header("Authorization", "Bearer " + cfg.apiKey.trim())
                    .header("Content-Type", "application/json")
                    .header("model", cfg.model)
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo.toString(), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() != 200) {
                String msg = new String(resp.body(), StandardCharsets.UTF_8);
                throw new IOException("HTTP " + resp.statusCode() + " " + (msg.length() > 200 ? msg.substring(0, 200) : msg));
            }
            byte[] pcm = resp.body();
            if (pcm.length > VoiceS2C.MAX_BYTES) pcm = Arrays.copyOf(pcm, VoiceS2C.MAX_BYTES & ~1);
            return pcm;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void difundir(MinecraftServer server, ServerWorld mundo, byte[] pcm, SillaConfig cfg,
                                 double x, double y, double z) {
        double r2 = (double) cfg.radio * cfg.radio;
        VoiceS2C payload = new VoiceS2C(pcm, cfg.sampleRate, x, y, z, (float) cfg.radio);
        for (ServerPlayerEntity otro : server.getPlayerManager().getPlayerList()) {
            if (otro.getServerWorld() == mundo && otro.squaredDistanceTo(x, y, z) <= r2) {
                ServerPlayNetworking.send(otro, payload);
            }
        }
    }

    private static void aviso(ServerPlayerEntity p, String msg, Formatting color) {
        p.sendMessage(Text.literal(msg).formatted(color), true); // barra de accion, NO chat
    }

    private static String limpiar(String s, int max) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 32 && c != 127) sb.append(c);
        }
        String t = sb.toString().trim();
        return t.length() > max ? t.substring(0, max) : t;
    }
}
