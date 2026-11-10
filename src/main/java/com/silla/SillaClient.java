package com.silla;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

public class SillaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(WheelchairModel.LAYER, WheelchairModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.WHEELCHAIR, WheelchairRenderer::new);

        TabletItem.OPEN_SCREEN = () -> MinecraftClient.getInstance().setScreen(new TabletScreen());

        ClientPlayNetworking.registerGlobalReceiver(VoiceS2C.ID, (payload, context) -> VoicePlayer.play(payload));
    }
}
