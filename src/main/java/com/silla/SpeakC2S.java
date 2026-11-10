package com.silla;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** Cliente -> servidor: el texto que el jugador escribio en la tablet. */
public record SpeakC2S(String text) implements CustomPayload {
    public static final Id<SpeakC2S> ID = new Id<>(SillaMod.id("hablar"));
    public static final PacketCodec<RegistryByteBuf, SpeakC2S> CODEC =
            PacketCodec.tuple(PacketCodecs.string(300), SpeakC2S::text, SpeakC2S::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
