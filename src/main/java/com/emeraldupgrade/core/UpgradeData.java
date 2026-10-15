package com.emeraldupgrade.core;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 物品上累计的强化数据，存放在物品的 {@code minecraft:custom_data} 组件的
 * {@code emeraldupgrade} 子标签下（不会影响其他模组写入的自定义数据）。
 *
 * @param level          累计强化等级（铁砧与强化台共用，上限见 UpgradeManager#MAX_LEVEL）
 * @param attack         本模组追加的攻击力总量
 * @param durability     本模组追加的耐久上限总量
 * @param efficiency     本模组追加的效率附魔等级总量
 * @param power          本模组追加的力量附魔等级总量
 * @param quickCharge    本模组追加的快速装填等级总量
 * @param armor          本模组追加的护甲总量
 * @param baseMaxDamage  第一次强化时的原始耐久上限（用于计算加成）
 * @param loreTail       物品 lore 尾部由本模组添加的行数（永久等级行 + 预览行）
 * @param previewLines   其中属于「即将升级预览」的行数（取出物品时移除，永久等级行保留）
 */
public record UpgradeData(int level, double attack, int durability, int efficiency, int power,
                          int quickCharge, double armor, int baseMaxDamage, int loreTail, int previewLines) {

    private static final String TAG_KEY = "emeraldupgrade";

    public static final UpgradeData EMPTY = new UpgradeData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    public static UpgradeData read(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return EMPTY;
        }
        CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
        if (custom == null || custom.isEmpty()) {
            return EMPTY;
        }
        CompoundTag sub = custom.copyTag().getCompound(TAG_KEY);
        if (sub.isEmpty()) {
            return EMPTY;
        }
        return new UpgradeData(
                sub.getInt("level"),
                sub.getDouble("attack"),
                sub.getInt("durability"),
                sub.getInt("efficiency"),
                sub.getInt("power"),
                sub.getInt("quickCharge"),
                sub.getDouble("armor"),
                sub.getInt("baseMaxDamage"),
                sub.getInt("loreTail"),
                sub.getInt("previewLines"));
    }

    public void write(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag sub = new CompoundTag();
        sub.putInt("level", this.level);
        sub.putDouble("attack", this.attack);
        sub.putInt("durability", this.durability);
        sub.putInt("efficiency", this.efficiency);
        sub.putInt("power", this.power);
        sub.putInt("quickCharge", this.quickCharge);
        sub.putDouble("armor", this.armor);
        sub.putInt("baseMaxDamage", this.baseMaxDamage);
        sub.putInt("loreTail", this.loreTail);
        sub.putInt("previewLines", this.previewLines);
        tag.put(TAG_KEY, sub);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public UpgradeData withLoreTail(int value) {
        return new UpgradeData(level, attack, durability, efficiency, power, quickCharge, armor, baseMaxDamage, value, previewLines);
    }

    public UpgradeData withPreviewLines(int value) {
        return new UpgradeData(level, attack, durability, efficiency, power, quickCharge, armor, baseMaxDamage, loreTail, value);
    }
}
