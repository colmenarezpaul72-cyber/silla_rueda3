package com.silla;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Tablet: click derecho abre la barra de texto; lo escrito se dice en voz alta (sin salir en el chat). */
public class TabletItem extends Item {
    /** Lo asigna el cliente (SillaClient). En servidor queda en null y no se usa. */
    public static Runnable OPEN_SCREEN = null;

    public TabletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient && OPEN_SCREEN != null) {
            OPEN_SCREEN.run();
        }
        return TypedActionResult.success(stack, world.isClient);
    }
}
