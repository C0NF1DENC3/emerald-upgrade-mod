package com.emeraldupgrade.core;

/**
 * 一次强化带来的属性变化量（增量，不是总量）。
 *
 * @param levels       强化等级提升量
 * @param attack       攻击力增量
 * @param durability   耐久上限增量
 * @param efficiency   效率附魔等级增量
 * @param power        力量附魔等级增量（弓）
 * @param quickCharge  快速装填附魔等级增量（弩）
 * @param armor        护甲增量
 */
public record UpgradeDeltas(int levels, double attack, int durability, int efficiency,
                            int power, int quickCharge, double armor) {

    public static final UpgradeDeltas NONE = new UpgradeDeltas(0, 0, 0, 0, 0, 0, 0);

    public boolean isEmpty() {
        return levels == 0 && attack == 0 && durability == 0 && efficiency == 0
                && power == 0 && quickCharge == 0 && armor == 0;
    }
}
