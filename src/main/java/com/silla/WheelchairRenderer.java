package com.silla;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class WheelchairRenderer extends MobEntityRenderer<WheelchairEntity, WheelchairModel> {
    private static final Identifier TEXTURE = SillaMod.id("textures/entity/silla_de_ruedas.png");

    public WheelchairRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new WheelchairModel(ctx.getPart(WheelchairModel.LAYER)), 0.5F);
    }

    @Override
    public Identifier getTexture(WheelchairEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(WheelchairEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        this.model.roll = entity.wheelRoll(tickDelta);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
