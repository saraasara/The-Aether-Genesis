package com.aetherteam.aether_genesis.client;

import com.aetherteam.aether_genesis.Genesis;
import com.aetherteam.aether_genesis.item.GenesisItems;
import com.aetherteam.aether_genesis.item.accessories.DyeableClothItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Genesis.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GenesisColorResolvers {
    @SubscribeEvent
    static void registerItemColor(RegisterColorHandlersEvent.Item event) {
        event.register((color, itemProvider) -> itemProvider > 0 ? -1 : ((DyeableClothItem) color.getItem()).getColor(color), GenesisItems.CAPE.get());
    }
}
