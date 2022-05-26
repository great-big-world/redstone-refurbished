package com.github.creoii.redstone_refurbished.block.blockentity;

import com.github.creoii.redstone_refurbished.main.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ReceptorBlockEntity extends BlockEntity {
    public ReceptorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.RECEPTOR, pos, state);
    }
}
