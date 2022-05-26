package com.github.creoii.redstone_refurbished.main;

import com.github.creoii.redstone_refurbished.main.registry.BlockEntityRegistry;
import com.github.creoii.redstone_refurbished.main.registry.BlockRegistry;
import net.fabricmc.api.ModInitializer;

public class RedstoneRefurbished implements ModInitializer {
    public static final String MOD_ID = "redstone_refurbished";

    @Override
    public void onInitialize() {
        BlockRegistry.register();
        BlockEntityRegistry.register();
    }
}
