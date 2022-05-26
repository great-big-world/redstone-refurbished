package com.github.creoii.redstone_refurbished.main.registry;

import com.github.creoii.redstone_refurbished.block.CopperPipeBlock;
import com.github.creoii.redstone_refurbished.block.RedstoneAstrolabeBlock;
import com.github.creoii.redstone_refurbished.block.ReceptorBlock;
import com.github.creoii.redstone_refurbished.main.RedstoneRefurbished;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {
    public static final Block RECEPTOR = new ReceptorBlock(FabricBlockSettings.copy(Blocks.REPEATER).nonOpaque());
    public static final Block REDSTONE_ASTROLABE = new RedstoneAstrolabeBlock(FabricBlockSettings.copy(Blocks.OBSERVER));
    public static final Block COPPER_PIPE = new CopperPipeBlock(FabricBlockSettings.of(Material.METAL));

    public static void register() {
        registerBlock(new Identifier(RedstoneRefurbished.MOD_ID, "receptor"), RECEPTOR, ItemGroup.REDSTONE);
        registerBlock(new Identifier(RedstoneRefurbished.MOD_ID, "redstone_astrolabe"), REDSTONE_ASTROLABE, ItemGroup.REDSTONE);
        //registerBlock(new Identifier(RedstoneRefurbished.MOD_ID, "copper_pipe"), COPPER_PIPE, ItemGroup.REDSTONE);
    }

    public static <B extends Block> Block registerBlock(Identifier id, B block, ItemGroup group) {
        Registry.register(Registry.BLOCK, id, block);
        if (group != null) Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(group)));
        return block;
    }
}
