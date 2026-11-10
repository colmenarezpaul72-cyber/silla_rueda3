package com.silla;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/** Silla de ruedas montable y conducible (click derecho para subir, WASD para mover). */
public class WheelchairEntity extends MobEntity {
    /**
     * Altura (en bloques) a la que se ancla el pasajero. El asiento del modelo esta a 0.75 bloques
     * (12 px). Si ves que el jugador se hunde o flota, cambia SOLO este numero.
     */
    public static final float SEAT_ATTACH = 0.6F;
    /** Radio de la rueda grande en bloques (7 px). */
    private static final float WHEEL_RADIUS = 0.4375F;

    private float wheel, prevWheel;
    private double lastX, lastZ;
    private boolean posInit;

    public WheelchairEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.hasPassengers()) {
            return super.interactMob(player, hand);
        }
        if (!this.getWorld().isClient) {
            player.startRiding(this);
        }
        return ActionResult.success(this.getWorld().isClient);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof PlayerEntity player ? player : null;
    }

    @Override
    protected void tickControlled(PlayerEntity player, Vec3d movementInput) {
        super.tickControlled(player, movementInput);
        this.setRotation(player.getYaw(), player.getPitch() * 0.5F);
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity player, Vec3d movementInput) {
        float side = player.sidewaysSpeed * 0.5F;
        float forward = player.forwardSpeed;
        if (forward <= 0.0F) {
            forward *= 0.25F; // marcha atras mas lenta
        }
        return new Vec3d(side, 0.0, forward);
    }

    @Override
    protected float getSaddledSpeed(PlayerEntity player) {
        return (float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevWheel = this.wheel;
        double x = this.getX(), z = this.getZ();
        if (posInit) {
            double dx = x - lastX, dz = z - lastZ;
            float dist = (float) Math.sqrt(dx * dx + dz * dz);
            if (dist > 1.0E-4F) {
                float rad = this.getYaw() * MathHelper.RADIANS_PER_DEGREE;
                double forward = dx * -MathHelper.sin(rad) + dz * MathHelper.cos(rad);
                this.wheel += (forward >= 0 ? 1 : -1) * dist / WHEEL_RADIUS;
            }
        }
        lastX = x;
        lastZ = z;
        posInit = true;
    }

    /** Giro acumulado de las ruedas (radianes), interpolado para que se vea suave. */
    public float wheelRoll(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevWheel, wheel);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_CHAIN_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_CHAIN_BREAK;
    }
}
