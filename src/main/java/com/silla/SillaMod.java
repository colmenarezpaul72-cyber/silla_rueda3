package com.silla;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class SillaMod implements ModInitializer {
    public static final String ID = "silla";

    public static Identifier id(String path) {
        return Identifier.of(ID, path);
    }

    @Override
    public void onInitialize() {
        SillaConfig.load();
        ModEntities.init();

        PayloadTypeRegistry.playC2S().register(SpeakC2S.ID, SpeakC2S.CODEC);
        PayloadTypeRegistry.playS2C().register(VoiceS2C.ID, VoiceS2C.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SpeakC2S.ID,
                (payload, context) -> TtsService.hablar(context.player(), payload.text()));
    }
}
