package com.github.creoii.redstone_refurbished.block.blockentity;

import com.github.creoii.redstone_refurbished.main.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class RedstoneAstrolabeBlockEntity extends BlockEntity {
    public RedstoneAstrolabeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.REDSTONE_ASTROLABE, pos, state);
    }
}
