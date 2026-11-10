package com.silla;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Modelo de la silla de ruedas (generado: ver tools/gen_model.py). Textura 128x128. */
public class WheelchairModel extends EntityModel<WheelchairEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Identifier.of("silla", "silla_de_ruedas"), "main");

    private final ModelPart root;
    private final ModelPart ruedaIzq, ruedaDer, ruedita1, ruedita2;
    /** Giro de las ruedas (radianes). Lo fija el renderer antes de dibujar. */
    public float roll;

    public WheelchairModel(ModelPart root) {
        this.root = root;
        this.ruedaIzq = root.getChild("rueda_izq");
        this.ruedaDer = root.getChild("rueda_der");
        this.ruedita1 = root.getChild("ruedita_izq");
        this.ruedita2 = root.getChild("ruedita_der");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild("bb_main", ModelPartBuilder.create()
                .uv(30, 21).cuboid(-6F, -11F, -5F, 12F, 1F, 11F)
                .uv(44, 34).cuboid(-5.5F, -12F, -4.5F, 11F, 1F, 9F)
                .uv(32, 0).cuboid(-6F, -26F, 4.5F, 12F, 14F, 1.5F)
                .uv(76, 21).cuboid(-5F, -24F, 3.5F, 10F, 11F, 1F)
                .uv(0, 0).cuboid(-7.5F, -30F, 5F, 1.5F, 19F, 1.5F)
                .uv(8, 46).cuboid(-7.5F, -31F, 6.5F, 1.5F, 1.5F, 5F)
                .uv(8, 0).cuboid(6F, -30F, 5F, 1.5F, 19F, 1.5F)
                .uv(22, 46).cuboid(6F, -31F, 6.5F, 1.5F, 1.5F, 5F)
                .uv(98, 21).cuboid(-8F, -19F, -4F, 2F, 1.5F, 10F)
                .uv(84, 34).cuboid(-7.75F, -17.5F, -4F, 1.5F, 8F, 1.5F)
                .uv(0, 34).cuboid(6F, -19F, -4F, 2F, 1.5F, 10F)
                .uv(92, 34).cuboid(6.25F, -17.5F, -4F, 1.5F, 8F, 1.5F)
                .uv(60, 0).cuboid(-7.5F, -10F, -5.5F, 1.5F, 1.5F, 12F)
                .uv(24, 34).cuboid(-7.5F, -12F, 0.5F, 1.5F, 8F, 3F)
                .uv(88, 0).cuboid(6F, -10F, -5.5F, 1.5F, 1.5F, 12F)
                .uv(34, 34).cuboid(6F, -12F, 0.5F, 1.5F, 8F, 3F)
                .uv(88, 46).cuboid(-6F, -8.5F, -3F, 12F, 1F, 1.5F)
                .uv(0, 55).cuboid(-8F, -7.5F, 1.4F, 16F, 1F, 1.2F)
                .uv(114, 34).cuboid(-7.25F, -9F, -8.75F, 1.5F, 6.5F, 1.5F)
                .uv(0, 46).cuboid(5.75F, -9F, -8.75F, 1.5F, 6.5F, 1.5F)
                .uv(36, 46).cuboid(-7F, -3.5F, -12F, 5F, 1F, 5F)
                .uv(56, 46).cuboid(2F, -3.5F, -12F, 5F, 1F, 5F),
                ModelTransform.pivot(0F, 24F, 0F));

        ModelPartData rueda_izq = root.addChild("rueda_izq", ModelPartBuilder.create(), ModelTransform.pivot(-9F, 17F, 2F));
        rueda_izq.addChild("llanta0", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(0F, 0F, 0F));
        rueda_izq.addChild("llanta1", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(0.7854F, 0F, 0F));
        rueda_izq.addChild("llanta2", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(1.5708F, 0F, 0F));
        rueda_izq.addChild("llanta3", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(2.3562F, 0F, 0F));
        rueda_izq.addChild("rayo_a", ModelPartBuilder.create().uv(116, 0).cuboid(-1.1F, -6F, -0.4F, 2.2F, 12F, 0.8F), ModelTransform.NONE);
        rueda_izq.addChild("rayo_b", ModelPartBuilder.create().uv(0, 21).cuboid(-1.1F, -0.4F, -6F, 2.2F, 0.8F, 12F), ModelTransform.NONE);
        rueda_izq.addChild("cubo", ModelPartBuilder.create().uv(76, 46).cuboid(-1.4F, -1.5F, -1.5F, 2.8F, 3F, 3F), ModelTransform.NONE);
        ModelPartData rueda_der = root.addChild("rueda_der", ModelPartBuilder.create(), ModelTransform.pivot(9F, 17F, 2F));
        rueda_der.addChild("llanta0", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(0F, 0F, 0F));
        rueda_der.addChild("llanta1", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(0.7854F, 0F, 0F));
        rueda_der.addChild("llanta2", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(1.5708F, 0F, 0F));
        rueda_der.addChild("llanta3", ModelPartBuilder.create().uv(16, 0).cuboid(-1F, -7F, -2.9F, 2F, 14F, 5.8F), ModelTransform.rotation(2.3562F, 0F, 0F));
        rueda_der.addChild("rayo_a", ModelPartBuilder.create().uv(116, 0).cuboid(-1.1F, -6F, -0.4F, 2.2F, 12F, 0.8F), ModelTransform.NONE);
        rueda_der.addChild("rayo_b", ModelPartBuilder.create().uv(0, 21).cuboid(-1.1F, -0.4F, -6F, 2.2F, 0.8F, 12F), ModelTransform.NONE);
        rueda_der.addChild("cubo", ModelPartBuilder.create().uv(76, 46).cuboid(-1.4F, -1.5F, -1.5F, 2.8F, 3F, 3F), ModelTransform.NONE);
        root.addChild("ruedita_izq", ModelPartBuilder.create().uv(100, 34).cuboid(-1F, -2.5F, -2.5F, 2F, 5F, 5F), ModelTransform.pivot(-6.5F, 21.5F, -8F));
        root.addChild("ruedita_der", ModelPartBuilder.create().uv(100, 34).cuboid(-1F, -2.5F, -2.5F, 2F, 5F, 5F), ModelTransform.pivot(6.5F, 21.5F, -8F));

        return TexturedModelData.of(data, 128, 128);
    }

    @Override
    public void setAngles(WheelchairEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ruedaIzq.pitch = roll;
        ruedaDer.pitch = roll;
        ruedita1.pitch = roll * 1.75F; // ruedas chicas giran mas rapido (radio menor)
        ruedita2.pitch = roll * 1.75F;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }
}
