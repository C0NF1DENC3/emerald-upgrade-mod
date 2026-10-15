package com.emeraldupgrade.block;

import com.emeraldupgrade.block.entity.EmeraldUpgradeTableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/**
 * 绿宝石强化台。右键打开强化界面（借鉴附魔台：放入物品与绿宝石，三选一方向）。
 */
public class EmeraldUpgradeTableBlock extends BaseEntityBlock {

    public static final MapCodec<EmeraldUpgradeTableBlock> CODEC = simpleCodec(EmeraldUpgradeTableBlock::new);

    public EmeraldUpgradeTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * BaseEntityBlock 默认返回 RenderShape.INVISIBLE（有方块实体的方块默认不渲染静态模型），
     * 必须显式覆盖为 MODEL，否则放置后看不到模型（物品图标却正常，因为物品走的是 item 模型）。
     * 参考原版附魔台 EnchantingTableBlock 的写法。
     */
    @Override
    protected net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EmeraldUpgradeTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof EmeraldUpgradeTableBlockEntity blockEntity) {
            ((ServerPlayer) player).openMenu(blockEntity);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            // 破坏方块时掉落内部物品
            if (level.getBlockEntity(pos) instanceof EmeraldUpgradeTableBlockEntity blockEntity) {
                Containers.dropContents(level, pos, blockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
