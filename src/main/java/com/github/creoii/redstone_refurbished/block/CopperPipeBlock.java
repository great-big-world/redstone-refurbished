package com.github.creoii.redstone_refurbished.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class CopperPipeBlock extends RedstoneWireBlock implements Waterloggable {
    private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(4d, 0d, 4d, 12d, 4d, 12d);
    private static final Map<Direction, VoxelShape> SIDE_SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(4d, 0d, 0d, 4d, 4d, 12d), Direction.SOUTH, Block.createCuboidShape(4d, 0d, 4d, 12d, 4d, 16d), Direction.EAST, Block.createCuboidShape(4d, 0d, 4d, 16d, 4d, 12d), Direction.WEST, Block.createCuboidShape(0d, 0d, 4d, 12d, 4d, 12d), Direction.UP, Block.createCuboidShape(0d, 0d, 4d, 12d, 4d, 12d), Direction.DOWN, Block.createCuboidShape(0d, 0d, 4d, 12d, 4d, 12d)));
    private static final Map<Direction, VoxelShape> UP_SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.union(SIDE_SHAPES.get(Direction.NORTH), Block.createCuboidShape(4d, 0d, 0d, 12d, 16d, 4d)), Direction.SOUTH, VoxelShapes.union(SIDE_SHAPES.get(Direction.SOUTH), Block.createCuboidShape(4d, 0d, 16d, 12d, 16d, 16d)), Direction.EAST, VoxelShapes.union(SIDE_SHAPES.get(Direction.EAST), Block.createCuboidShape(16d, 0d, 4d, 16d, 16d, 12d)), Direction.WEST, VoxelShapes.union(SIDE_SHAPES.get(Direction.WEST), Block.createCuboidShape(0d, 0d, 4d, 4d, 16d, 12d)), Direction.UP, VoxelShapes.union(SIDE_SHAPES.get(Direction.UP), Block.createCuboidShape(0d, 0d, 4d, 4d, 16d, 12d)), Direction.DOWN, VoxelShapes.union(SIDE_SHAPES.get(Direction.DOWN), Block.createCuboidShape(0d, 0d, 4d, 4d, 16d, 12d))));
    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public static final EnumProperty<WireConnection> WIRE_CONNECTION_UP = EnumProperty.of("up", WireConnection.class);
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_DOWN = EnumProperty.of("down", WireConnection.class);
    public static final BooleanProperty FULL = BooleanProperty.of("full");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CopperPipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.NONE).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE).with(WIRE_CONNECTION_EAST, WireConnection.NONE).with(WIRE_CONNECTION_WEST, WireConnection.NONE).with(WIRE_CONNECTION_UP, WireConnection.NONE).with(WIRE_CONNECTION_DOWN, WireConnection.NONE).with(FULL, false).with(POWER, 0).with(WATERLOGGED, false));

        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST, Direction.UP, WIRE_CONNECTION_UP, Direction.DOWN, WIRE_CONNECTION_DOWN));
        for (BlockState blockState : getStateManager().getStates()) {
            if (blockState.get(POWER) == 0) {
                SHAPES.put(blockState, getShapeForState(blockState));
            }
        }
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;

        for (Direction direction : Direction.values()) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, SIDE_SHAPES.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, UP_SHAPES.get(direction));
            }
        }

        return voxelShape;
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);
        for (Direction direction : Direction.values()) {
            if (!state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected()) {
                WireConnection wireConnection = getRenderConnectionType(world, pos, direction, bl);
                state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
            }
        }

        return state;
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            boolean bl2 = canRunOnSide(world, blockPos, blockState, direction);
            if (bl2 && connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }

                return WireConnection.SIDE;
            }
        }

        return !connectsTo(blockState, direction) && (blockState.isSolidBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down()))) ? WireConnection.NONE : WireConnection.SIDE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockState state = world.getBlockState(ctx.getBlockPos());
        boolean bl = isNotConnected(state);
        state = getDefaultWireState(world, getDefaultState().with(POWER, state.get(POWER)), ctx.getBlockPos());
        if (!bl || !isNotConnected(state)) {
            boolean north = state.get(WIRE_CONNECTION_NORTH).isConnected();
            boolean south = state.get(WIRE_CONNECTION_SOUTH).isConnected();
            boolean east = state.get(WIRE_CONNECTION_EAST).isConnected();
            boolean west = state.get(WIRE_CONNECTION_WEST).isConnected();
            boolean up = state.get(WIRE_CONNECTION_UP).isConnected();
            boolean down = state.get(WIRE_CONNECTION_DOWN).isConnected();
            boolean bl6 = !north && !south;
            boolean bl7 = !east && !west;
            boolean bl8 = !up && !down;
            if (!west && bl6) {
                state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
            }

            if (!east && bl6) {
                state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
            }

            if (!north && bl7) {
                state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
            }

            if (!south && bl7) {
                state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
            }

            if (!up && bl8) {
                state = getDefaultState().with(WIRE_CONNECTION_UP, WireConnection.SIDE).with(POWER, state.get(POWER)).with(FULL, state.get(FULL)).with(WATERLOGGED, state.get(WATERLOGGED));
            }

            if (!down && bl8) {
                state = getDefaultState().with(WIRE_CONNECTION_DOWN, WireConnection.SIDE).with(POWER, state.get(POWER)).with(FULL, state.get(FULL)).with(WATERLOGGED, state.get(WATERLOGGED));
            }

        }
        return state;
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected() && !state.get(WIRE_CONNECTION_UP).isConnected() && !state.get(WIRE_CONNECTION_DOWN).isConnected();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.with(POWER, 0));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos).with(FULL, state.get(FULL)).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return super.rotate(state, rotation).with(FULL, state.get(FULL)).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return super.mirror(state, mirror).with(FULL, state.get(FULL)).with(WATERLOGGED, state.get(WATERLOGGED));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).isEmpty() && state.get(FULL)) {
            BlockState newState = state.with(FULL, false);
            player.setStackInHand(hand, Items.REDSTONE.getDefaultStack());
            world.setBlockState(pos, newState, 3);
            updateForNewState(world, pos, state, newState);
            return ActionResult.SUCCESS;
        } else if (player.getStackInHand(hand).isOf(Items.REDSTONE) && !state.get(FULL)) {
            BlockState newState = state.with(FULL, true);
            if (!player.isCreative()) player.getStackInHand(hand).decrement(1);
            world.setBlockState(pos, newState, 3);
            updateForNewState(world, pos, state, newState);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (canRunOnSide(world, blockPos, world.getBlockState(blockPos), direction.getOpposite())) return true;
        }
        return false;
    }

    private boolean canRunOnSide(BlockView world, BlockPos pos, BlockState floor, Direction face) {
        return floor.isSideSolidFullSquare(world, pos, face) || floor.isOf(Blocks.HOPPER);
    }

    public boolean emitsRedstonePower(BlockState state) {
        return state.get(FULL);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(FULL) & state.get(POWER) > 0)
            super.randomDisplayTick(state, world, pos, random);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_WEST, WIRE_CONNECTION_UP, WIRE_CONNECTION_DOWN, FULL, POWER, WATERLOGGED);
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        for (Direction direction : Direction.values()) {
            BlockPos blockPos = pos.offset(direction);
            if (oldState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() != newState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() && world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
            }
        }
    }
}
