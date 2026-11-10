package com.silla;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModEntities {
    private ModEntities() {}

    /** Mismos ids que la version anterior, asi los mundos existentes siguen funcionando. */
    public static final EntityType<WheelchairEntity> WHEELCHAIR = Registry.register(
            Registries.ENTITY_TYPE, SillaMod.id("silla_de_ruedas"),
            EntityType.Builder.<WheelchairEntity>create(WheelchairEntity::new, SpawnGroup.MISC)
                    .dimensions(0.8F, 1.1F)
                    .passengerAttachments(WheelchairEntity.SEAT_ATTACH)
                    .maxTrackingRange(10)
                    .build("silla_de_ruedas"));

    public static final Item WHEELCHAIR_EGG = Registry.register(
            Registries.ITEM, SillaMod.id("silla_de_ruedas_spawn_egg"),
            new SpawnEggItem(WHEELCHAIR, 12107460, 9116186, new Item.Settings()));

    public static final Item TABLET = Registry.register(
            Registries.ITEM, SillaMod.id("tablet"),
            new TabletItem(new Item.Settings().maxCount(1)));

    public static void init() {
        FabricDefaultAttributeRegistry.register(WHEELCHAIR, WheelchairEntity.createAttributes());
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(e -> e.add(WHEELCHAIR_EGG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(e -> e.add(TABLET));
    }
}
