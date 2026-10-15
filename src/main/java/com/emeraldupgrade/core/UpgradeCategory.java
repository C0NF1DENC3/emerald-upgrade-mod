package com.emeraldupgrade.core;

/**
 * 强化类别。不同类别在「铁砧标准强化」下每级获得的加成不同，
 * 在「绿宝石强化台」下也会获得不同的三个专精方向。
 */
public enum UpgradeCategory {
    /** 不可强化 */
    NONE(0.0, 0, 0, 0, 0, 0.0),
    /** 纯武器：剑、三叉戟、重锤 —— 攻击力 + 耐久 */
    WEAPON(1.0, 25, 0, 0, 0, 0.0),
    /** 混合型：斧头 —— 攻击力 + 耐久 + 效率 */
    HYBRID(1.0, 20, 1, 0, 0, 0.0),
    /** 工具：镐、锹、锄、剪刀 —— 耐久 + 效率 */
    TOOL(0.0, 25, 1, 0, 0, 0.0),
    /** 弓 —— 耐久 + 力量 */
    BOW(0.0, 25, 0, 1, 0, 0.0),
    /** 弩 —— 耐久 + 快速装填 */
    CROSSBOW(0.0, 25, 0, 0, 1, 0.0),
    /** 盔甲 —— 护甲 + 耐久 */
    ARMOR(0.0, 10, 0, 0, 0, 1.0),
    /** 其他可耐久物品：盾、鞘翅、钓鱼竿等 —— 耐久 */
    GENERIC(0.0, 40, 0, 0, 0, 0.0);

    private final double attackPerLevel;
    private final int durabilityPerLevel;
    private final int efficiencyPerLevel;
    private final int powerPerLevel;
    private final int quickChargePerLevel;
    private final double armorPerLevel;

    UpgradeCategory(double attackPerLevel, int durabilityPerLevel, int efficiencyPerLevel,
                    int powerPerLevel, int quickChargePerLevel, double armorPerLevel) {
        this.attackPerLevel = attackPerLevel;
        this.durabilityPerLevel = durabilityPerLevel;
        this.efficiencyPerLevel = efficiencyPerLevel;
        this.powerPerLevel = powerPerLevel;
        this.quickChargePerLevel = quickChargePerLevel;
        this.armorPerLevel = armorPerLevel;
    }

    public boolean upgradeable() {
        return this != NONE;
    }

    /** 铁砧中「标准强化」每一级带来的属性变化。 */
    public UpgradeDeltas perLevel(int levels) {
        return new UpgradeDeltas(
                levels,
                this.attackPerLevel * levels,
                this.durabilityPerLevel * levels,
                this.efficiencyPerLevel * levels,
                this.powerPerLevel * levels,
                this.quickChargePerLevel * levels,
                this.armorPerLevel * levels);
    }
}
