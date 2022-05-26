package com.github.creoii.redstone_refurbished.main.registry;

import com.github.creoii.redstone_refurbished.block.blockentity.ReceptorBlockEntity;
import com.github.creoii.redstone_refurbished.block.blockentity.RedstoneAstrolabeBlockEntity;
import com.github.creoii.redstone_refurbished.main.RedstoneRefurbished;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntityRegistry {
    public static final BlockEntityType<ReceptorBlockEntity> RECEPTOR = FabricBlockEntityTypeBuilder.create(ReceptorBlockEntity::new, BlockRegistry.RECEPTOR).build();
    public static final BlockEntityType<RedstoneAstrolabeBlockEntity> REDSTONE_ASTROLABE = FabricBlockEntityTypeBuilder.create(RedstoneAstrolabeBlockEntity::new, BlockRegistry.REDSTONE_ASTROLABE).build();

    public static void register() {
        registerBlockEntity(new Identifier(RedstoneRefurbished.MOD_ID, "receptor"), RECEPTOR);
        registerBlockEntity(new Identifier(RedstoneRefurbished.MOD_ID, "redstone_astrolabe"), REDSTONE_ASTROLABE);
    }

    public static <BE extends BlockEntityType<?>> BE registerBlockEntity(Identifier id, BE blockEntity) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, blockEntity);
    }
}
