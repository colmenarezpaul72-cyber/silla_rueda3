package com.silla;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** Servidor -> clientes cercanos: audio PCM 16-bit mono + de donde sale la voz. */
public record VoiceS2C(byte[] pcm, int sampleRate, double x, double y, double z, float radius) implements CustomPayload {
    public static final int MAX_BYTES = 900_000;
    public static final Id<VoiceS2C> ID = new Id<>(SillaMod.id("voz"));
    public static final PacketCodec<RegistryByteBuf, VoiceS2C> CODEC = PacketCodec.tuple(
            PacketCodecs.byteArray(MAX_BYTES), VoiceS2C::pcm,
            PacketCodecs.INTEGER, VoiceS2C::sampleRate,
            PacketCodecs.DOUBLE, VoiceS2C::x,
            PacketCodecs.DOUBLE, VoiceS2C::y,
            PacketCodecs.DOUBLE, VoiceS2C::z,
            PacketCodecs.FLOAT, VoiceS2C::radius,
            VoiceS2C::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
