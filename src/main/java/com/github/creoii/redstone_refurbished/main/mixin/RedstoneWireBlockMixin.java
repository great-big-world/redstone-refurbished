package com.github.creoii.redstone_refurbished.main.mixin;

import com.github.creoii.redstone_refurbished.main.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Iterator;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void redstone_refurbished$connectToCopperPipe(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        //if (state.isOf(BlockRegistry.COPPER_PIPE)) {
        //    cir.setReturnValue(state.get(CopperPipeBlock.FULL));
        //}
        if (state.isOf(BlockRegistry.RECEPTOR)) {
            cir.setReturnValue(state.get(Properties.HORIZONTAL_FACING) == dir.getOpposite());
        }
    }

    @Redirect(method = "getReceivedRedstonePower", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction$Type;iterator()Ljava/util/Iterator;"))
    private Iterator<Direction> redstone_refurbish$transmitSignalEverywhere(Direction.Type instance) {
        return Arrays.stream(Direction.values()).iterator();
    }

    //@Inject(method = "getWeakRedstonePower", at = @At("HEAD"), cancellable = true)
    //private void redstone_refurbished$weakCopperPipePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
    //    if (state.isOf(BlockRegistry.COPPER_PIPE) && !state.get(CopperPipeBlock.FULL)) cir.setReturnValue(0);
    //}

    //@Inject(method = "getStrongRedstonePower", at = @At("HEAD"), cancellable = true)
    //private void redstone_refurbished$strongCopperPipePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
    //    if (state.isOf(BlockRegistry.COPPER_PIPE) && !state.get(CopperPipeBlock.FULL)) cir.setReturnValue(0);
    //}
}
