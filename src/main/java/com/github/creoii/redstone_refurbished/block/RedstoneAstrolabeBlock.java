package com.github.creoii.redstone_refurbished.block;

import com.github.creoii.redstone_refurbished.block.blockentity.RedstoneAstrolabeBlockEntity;
import com.github.creoii.redstone_refurbished.main.registry.BlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class RedstoneAstrolabeBlock extends BlockWithEntity {
    public static final IntProperty POWER = Properties.POWER;

    public RedstoneAstrolabeBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(POWER, 0));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(POWER, ctx.getWorld().getMoonPhase() * 2);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(POWER, world.getMoonPhase() * 2);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneAstrolabeBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return !world.isClient ? checkType(type, BlockEntityRegistry.REDSTONE_ASTROLABE, RedstoneAstrolabeBlock::tick) : null;
    }

    private static void tick(World world, BlockPos pos, BlockState state, RedstoneAstrolabeBlockEntity blockEntity) {
        int phase = world.getMoonPhase() * 2;
        if (phase != state.get(POWER)) {
            world.setBlockState(pos, state.with(POWER, phase), 3);
        }
    }
}
