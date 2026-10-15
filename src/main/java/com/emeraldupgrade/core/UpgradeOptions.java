package com.emeraldupgrade.core;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 每个强化类别在「绿宝石强化台」中可选的三个专精方向（各有不同，消耗也不同）。
 * 想调整平衡性直接改这里即可。
 */
public final class UpgradeOptions {

    private static final Map<UpgradeCategory, List<UpgradeOption>> OPTIONS = new EnumMap<>(UpgradeCategory.class);

    static {
        // ---- 武器（剑、三叉戟、重锤）----
        OPTIONS.put(UpgradeCategory.WEAPON, List.of(
                new UpgradeOption("emeraldupgrade.option.weapon.attack",
                        new UpgradeDeltas(1, 2, 0, 0, 0, 0, 0), 3),
                new UpgradeOption("emeraldupgrade.option.weapon.durability",
                        new UpgradeDeltas(1, 0, 60, 0, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.weapon.balanced",
                        new UpgradeDeltas(1, 1, 30, 0, 0, 0, 0), 4)));

        // ---- 斧头（攻击 + 挖掘两用）----
        OPTIONS.put(UpgradeCategory.HYBRID, List.of(
                new UpgradeOption("emeraldupgrade.option.hybrid.attack",
                        new UpgradeDeltas(1, 2, 0, 0, 0, 0, 0), 3),
                new UpgradeOption("emeraldupgrade.option.hybrid.efficiency",
                        new UpgradeDeltas(1, 0, 0, 1, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.hybrid.balanced",
                        new UpgradeDeltas(1, 1, 30, 1, 0, 0, 0), 4)));

        // ---- 工具（镐、锹、锄、剪刀）----
        OPTIONS.put(UpgradeCategory.TOOL, List.of(
                new UpgradeOption("emeraldupgrade.option.tool.efficiency",
                        new UpgradeDeltas(1, 0, 0, 1, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.tool.durability",
                        new UpgradeDeltas(1, 0, 60, 0, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.tool.balanced",
                        new UpgradeDeltas(1, 0, 30, 1, 0, 0, 0), 3)));

        // ---- 弓 ----
        OPTIONS.put(UpgradeCategory.BOW, List.of(
                new UpgradeOption("emeraldupgrade.option.bow.power",
                        new UpgradeDeltas(1, 0, 0, 0, 1, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.bow.durability",
                        new UpgradeDeltas(1, 0, 60, 0, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.bow.balanced",
                        new UpgradeDeltas(1, 0, 30, 0, 1, 0, 0), 3)));

        // ---- 弩 ----
        OPTIONS.put(UpgradeCategory.CROSSBOW, List.of(
                new UpgradeOption("emeraldupgrade.option.crossbow.quick_charge",
                        new UpgradeDeltas(1, 0, 0, 0, 0, 1, 0), 2),
                new UpgradeOption("emeraldupgrade.option.crossbow.durability",
                        new UpgradeDeltas(1, 0, 60, 0, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.crossbow.balanced",
                        new UpgradeDeltas(1, 0, 30, 0, 0, 1, 0), 3)));

        // ---- 盔甲 ----
        OPTIONS.put(UpgradeCategory.ARMOR, List.of(
                new UpgradeOption("emeraldupgrade.option.armor.armor",
                        new UpgradeDeltas(1, 0, 0, 0, 0, 0, 1), 2),
                new UpgradeOption("emeraldupgrade.option.armor.durability",
                        new UpgradeDeltas(1, 0, 40, 0, 0, 0, 0), 2),
                new UpgradeOption("emeraldupgrade.option.armor.balanced",
                        new UpgradeDeltas(1, 0, 20, 0, 0, 0, 1), 3)));

        // ---- 其他可耐久物品（盾、鞘翅、钓鱼竿……）----
        OPTIONS.put(UpgradeCategory.GENERIC, List.of(
                new UpgradeOption("emeraldupgrade.option.general.light",
                        new UpgradeDeltas(1, 0, 50, 0, 0, 0, 0), 1),
                new UpgradeOption("emeraldupgrade.option.general.deep",
                        new UpgradeDeltas(1, 0, 120, 0, 0, 0, 0), 3),
                new UpgradeOption("emeraldupgrade.option.general.full",
                        new UpgradeDeltas(1, 0, 200, 0, 0, 0, 0), 5)));
    }

    private UpgradeOptions() {
    }

    public static List<UpgradeOption> getOptions(UpgradeCategory category) {
        return OPTIONS.getOrDefault(category, List.of());
    }
}
