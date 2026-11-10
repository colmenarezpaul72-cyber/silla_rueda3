package com.silla;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** config/silla_tablet.json (se crea sola la primera vez). Solo la usa el SERVIDOR. */
public final class SillaConfig {
    private static final Logger LOG = LoggerFactory.getLogger("silla");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static SillaConfig INSTANCE = new SillaConfig();

    /** Tu API key de Fish Audio (https://fish.audio/app/api-keys). NO la subas a GitHub. */
    public String apiKey = "";
    /** Voz "Jorge Loquendo". */
    public String referenceId = "75e8b1278959490f9cf3341e578a9203";
    /** Modelo TTS de Fish Audio (cabecera "model"). */
    public String model = "s2-pro";
    /** Hz del audio (8000, 16000, 24000, 32000 o 44100). */
    public int sampleRate = 24000;
    /** Distancia (bloques) a la que se escucha la voz. */
    public int radio = 32;
    public int maxCaracteres = 140;
    public int cooldownSegundos = 3;

    public static SillaConfig get() {
        return INSTANCE;
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("silla_tablet.json");
        try {
            if (Files.exists(path)) {
                SillaConfig c = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), SillaConfig.class);
                if (c != null) INSTANCE = c;
            } else {
                Files.writeString(path, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
                LOG.info("Config creada en {} -> pon tu apiKey de Fish Audio ahi.", path);
            }
        } catch (Exception e) {
            LOG.error("No pude leer/escribir {}: {}", path, e.toString());
        }
        if (INSTANCE.apiKey == null || INSTANCE.apiKey.isBlank()) {
            String env = System.getenv("FISH_API_KEY");
            if (env != null && !env.isBlank()) INSTANCE.apiKey = env.trim();
        }
        if (INSTANCE.apiKey == null || INSTANCE.apiKey.isBlank()) {
            LOG.warn("Tablet sin voz: falta 'apiKey' en config/silla_tablet.json (o variable FISH_API_KEY).");
        }
    }
}
