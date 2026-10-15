package com.emeraldupgrade.mixin;

import com.emeraldupgrade.core.UpgradeCategory;
import com.emeraldupgrade.core.UpgradeManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 铁砧强化 Mixin。
 *
 * <p>在铁砧计算结果时拦截：如果 1 号槽是绿宝石、0 号槽是可强化物品，
 * 就生成强化后的结果物品（附带预览说明）并取消原版逻辑；
 * 其他情况一律放行，完全保留原版铁砧的修理/合并/重命名机制。</p>
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    /** 原版字段：取出结果时要从 1 号槽消耗的“材料”数量，我们用它来消耗绿宝石。 */
    @Shadow
    public int repairItemCountCost;

    /** 原版字段：经验等级花费，我们置 0（绿宝石就是代价）。 */
    @Shadow
    private DataSlot cost;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void emeraldupgrade$onCreateResult(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack tool = menu.getSlot(0).getItem();
        ItemStack material = menu.getSlot(1).getItem();

        if (!UpgradeManager.isAnvilUpgrade(tool, material)) {
            return; // 不是「物品 + 绿宝石」组合，走原版逻辑
        }
        if (UpgradeManager.getCategory(tool) == UpgradeCategory.NONE) {
            return; // 该物品不可强化，走原版逻辑
        }

        // player 字段继承自 ItemCombinerMenu，@Shadow 拿不到，需通过 Accessor 接口读取
        Player player = ((ItemCombinerMenuAccessor) (Object) menu).emeraldupgrade$getPlayer();

        int remaining = UpgradeManager.MAX_LEVEL - UpgradeManager.getLevel(tool);
        if (remaining > 0) {
            int levels = Math.min(material.getCount(), remaining);
            ItemStack result = UpgradeManager.applyAnvilUpgrade(tool.copy(), levels, player.level().registryAccess());
            menu.getSlot(2).set(result);
            this.repairItemCountCost = levels; // 取出时消耗对应数量的绿宝石
        } else {
            menu.getSlot(2).set(ItemStack.EMPTY); // 已达等级上限，不给结果
            this.repairItemCountCost = 0;
        }
        this.cost.set(0); // 不额外消耗经验等级
        ci.cancel();
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void emeraldupgrade$onTake(Player player, ItemStack carried, CallbackInfo ci) {
        // 取出强化后的物品时移除“即将进行的强化”预览行，保留永久等级标识行
        UpgradeManager.stripPreviewLore(carried);
    }
}
