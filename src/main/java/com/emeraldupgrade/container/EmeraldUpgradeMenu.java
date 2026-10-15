package com.emeraldupgrade.container;

import com.emeraldupgrade.EmeraldUpgrade;
import com.emeraldupgrade.block.entity.EmeraldUpgradeTableBlockEntity;
import com.emeraldupgrade.core.UpgradeCategory;
import com.emeraldupgrade.core.UpgradeManager;
import com.emeraldupgrade.core.UpgradeOption;
import com.emeraldupgrade.core.UpgradeOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * 绿宝石强化台菜单。
 *
 * <p>通信方式完全复用原版机制：界面上的三个方向按钮通过
 * {@code gameMode.handleInventoryButtonClick} 发送到服务端的
 * {@link #clickMenuButton(Player, int)}，无需自定义网络包；
 * 格子内容由原版 Slot 同步机制自动同步到客户端。</p>
 */
public class EmeraldUpgradeMenu extends AbstractContainerMenu {

    public static final int CONTAINER_SIZE = 2;
    /** 绿宝石槽（下标 0） */
    public static final int EMERALD_SLOT = 0;
    /** 工具槽（下标 1） */
    public static final int TOOL_SLOT = 1;

    private final Container container;
    @Nullable
    private final EmeraldUpgradeTableBlockEntity blockEntity;
    private final boolean serverSide;

    /** 客户端构造（MenuType 工厂调用，使用虚拟容器，内容由原版机制同步） */
    public EmeraldUpgradeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), null, false);
    }

    /** 服务端构造 */
    public EmeraldUpgradeMenu(int containerId, Inventory playerInventory, EmeraldUpgradeTableBlockEntity blockEntity) {
        this(containerId, playerInventory, blockEntity, blockEntity, true);
    }

    private EmeraldUpgradeMenu(int containerId, Inventory playerInventory, Container container,
                               @Nullable EmeraldUpgradeTableBlockEntity blockEntity, boolean serverSide) {
        super(EmeraldUpgrade.UPGRADE_MENU.get(), containerId);
        this.container = container;
        this.blockEntity = blockEntity;
        this.serverSide = serverSide;
        this.container.startOpen(playerInventory.player);

        // 绿宝石槽 + 工具槽
        this.addSlot(new FilteredSlot(this.container, EMERALD_SLOT, 15, 47, stack -> stack.is(Items.EMERALD)));
        this.addSlot(new FilteredSlot(this.container, TOOL_SLOT, 35, 47,
                stack -> UpgradeManager.getCategory(stack) != UpgradeCategory.NONE));

        // 玩家背包（3 行 + 快捷栏）
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // ---------------- 方向选择（按钮点击，服务端权威） ----------------

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!this.serverSide || id < 0 || id > 2) {
            return true;
        }
        ItemStack tool = this.container.getItem(TOOL_SLOT);
        ItemStack emeralds = this.container.getItem(EMERALD_SLOT);
        if (tool.isEmpty() || emeralds.isEmpty()) {
            return true;
        }
        if (UpgradeManager.getLevel(tool) >= UpgradeManager.MAX_LEVEL) {
            return true;
        }
        List<UpgradeOption> options = UpgradeOptions.getOptions(UpgradeManager.getCategory(tool));
        if (id >= options.size()) {
            return true;
        }
        UpgradeOption option = options.get(id);
        if (emeralds.getCount() < option.emeraldCost()) {
            return true; // 绿宝石不足
        }

        // ---- 校验通过，执行强化 ----
        UpgradeManager.applyTableUpgrade(tool, option, player.level().registryAccess());
        emeralds.shrink(option.emeraldCost());
        if (emeralds.isEmpty()) {
            this.container.setItem(EMERALD_SLOT, ItemStack.EMPTY);
        }
        this.container.setChanged();
        this.playUpgradeSound(player);
        return true;
    }

    private void playUpgradeSound(Player player) {
        if (this.blockEntity == null) {
            return;
        }
        BlockPos pos = this.blockEntity.getBlockPos();
        player.level().playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.7F, 1.3F);
    }

    // ---------------- 原版容器行为 ----------------

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return result;
        }
        ItemStack stack = slot.getItem();
        result = stack.copy();
        if (index < CONTAINER_SIZE) {
            // 强化槽 → 玩家背包
            if (!this.moveItemStackTo(stack, CONTAINER_SIZE, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.is(Items.EMERALD)) {
            // 背包 → 绿宝石槽
            if (!this.moveItemStackTo(stack, EMERALD_SLOT, EMERALD_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (UpgradeManager.getCategory(stack) != UpgradeCategory.NONE) {
            // 背包 → 工具槽
            if (!this.moveItemStackTo(stack, TOOL_SLOT, TOOL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        if (!this.serverSide || this.blockEntity == null) {
            return true; // 客户端虚拟菜单
        }
        if (this.blockEntity.getLevel() == null || this.blockEntity.isRemoved()) {
            return false;
        }
        BlockPos pos = this.blockEntity.getBlockPos();
        return player.level().getBlockState(pos).is(EmeraldUpgrade.UPGRADE_TABLE_BLOCK.get())
                && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    /** 只允许放入指定物品的槽位。 */
    private static class FilteredSlot extends Slot {

        private final Predicate<ItemStack> filter;

        public FilteredSlot(Container container, int index, int x, int y, Predicate<ItemStack> filter) {
            super(container, index, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.filter.test(stack);
        }
    }
}
