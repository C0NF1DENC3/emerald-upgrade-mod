package com.emeraldupgrade.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露 {@link ItemCombinerMenu} 的 player 字段。
 *
 * <p>Mixin 的 @Shadow 只能作用于目标类「自己声明」的成员，继承自父类的字段
 * （如 ItemCombinerMenu.player）必须通过 @Accessor 访问接口来读取，
 * 否则生产环境会抛 InvalidMixinException 导致游戏崩溃。</p>
 */
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {

    @Accessor("player")
    Player emeraldupgrade$getPlayer();
}
