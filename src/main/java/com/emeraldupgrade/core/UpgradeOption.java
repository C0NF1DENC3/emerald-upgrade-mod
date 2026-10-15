package com.emeraldupgrade.core;

import net.minecraft.network.chat.Component;

/**
 * 绿宝石强化台提供的单个「强化方向」。
 *
 * @param titleKey    方向标题的翻译键
 * @param deltas      该方向带来的属性变化
 * @param emeraldCost 需要消耗的绿宝石数量
 */
public record UpgradeOption(String titleKey, UpgradeDeltas deltas, int emeraldCost) {

    public Component title() {
        return Component.translatable(this.titleKey);
    }
}
