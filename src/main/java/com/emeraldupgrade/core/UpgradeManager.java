package com.emeraldupgrade.core;

import com.emeraldupgrade.EmeraldUpgrade;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 强化逻辑核心：类别识别、属性计算与应用、预览文本生成。
 *
 * <p>所有强化数据都写在物品的 {@code minecraft:custom_data} 组件中，
 * 属性则直接写入 {@code minecraft:attribute_modifiers} / {@code minecraft:max_damage} /
 * {@code minecraft:enchantments} 等原版组件，因此强化效果对原版系统完全可见。</p>
 */
public final class UpgradeManager {

    /** 总强化等级上限（铁砧逐级强化与强化台方向强化共用） */
    public static final int MAX_LEVEL = 20;

    /** 本模组追加的攻击力修饰符 ID（用于替换旧值而不是叠加） */
    public static final ResourceLocation ATTACK_BONUS_ID =
            ResourceLocation.fromNamespaceAndPath(EmeraldUpgrade.MODID, "attack_bonus");
    /** 本模组追加的护甲修饰符 ID */
    public static final ResourceLocation ARMOR_BONUS_ID =
            ResourceLocation.fromNamespaceAndPath(EmeraldUpgrade.MODID, "armor_bonus");

    private UpgradeManager() {
    }

    // = ------------------------------------------------------------
    //  类别识别
    // = ------------------------------------------------------------

    /** 根据物品组件判断它属于哪个强化类别。 */
    public static UpgradeCategory getCategory(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.isDamageableItem()) {
            return UpgradeCategory.NONE;
        }
        if (stack.getItem() instanceof BowItem) {
            return UpgradeCategory.BOW;
        }
        if (stack.getItem() instanceof CrossbowItem) {
            return UpgradeCategory.CROSSBOW;
        }
        boolean hasAttack = hasAttribute(stack, Attributes.ATTACK_DAMAGE);
        boolean hasTool = stack.has(DataComponents.TOOL);
        boolean hasArmor = hasAttribute(stack, Attributes.ARMOR);
        if (hasArmor) {
            return UpgradeCategory.ARMOR;
        }
        if (hasAttack && hasTool) {
            return UpgradeCategory.HYBRID; // 斧头：既能攻击又能挖掘
        }
        if (hasAttack) {
            return UpgradeCategory.WEAPON; // 剑、三叉戟、重锤
        }
        if (hasTool) {
            return UpgradeCategory.TOOL; // 镐、锹、锄、剪刀
        }
        return UpgradeCategory.GENERIC; // 盾、鞘翅、钓鱼竿等其他可耐久物品
    }

    private static boolean hasAttribute(ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return false;
        }
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().value() == attribute.value()) {
                return true;
            }
        }
        return false;
    }

    public static int getLevel(ItemStack stack) {
        return UpgradeData.read(stack).level();
    }

    // = ------------------------------------------------------------
    //  铁砧入口
    // = ------------------------------------------------------------

    /** 铁砧中「物品 + 绿宝石」组合是否由本模组处理（否则走原版逻辑）。 */
    public static boolean isAnvilUpgrade(ItemStack tool, ItemStack material) {
        return tool != null && !tool.isEmpty() && tool.isDamageableItem()
                && material != null && !material.isEmpty() && material.is(Items.EMERALD);
    }

    /**
     * 铁砧：应用 {@code levels} 级标准强化，并在物品 lore 尾部附上「即将进行的强化」预览。
     * 会修改传入的 stack（调用方应传入 copy）。
     *
     * @param registries 注册表访问（1.21.1 附魔为动态注册表，用于把 ResourceKey 解析为 Holder）
     */
    public static ItemStack applyAnvilUpgrade(ItemStack stack, int levels, RegistryAccess registries) {
        UpgradeCategory category = getCategory(stack);
        if (levels <= 0 || category == UpgradeCategory.NONE) {
            return stack;
        }
        UpgradeData before = UpgradeData.read(stack);
        UpgradeDeltas deltas = category.perLevel(levels);
        // 先基于「升级前」状态生成预览文本
        List<Component> preview = new ArrayList<>(buildPreviewLines(stack, before, deltas, registries));
        preview.add(Component.translatable("emeraldupgrade.preview.cost", levels));
        preview.add(Component.translatable("emeraldupgrade.preview.level", before.level(), before.level() + levels));
        // 再实际应用强化
        applyDeltas(stack, deltas, registries);
        // 最后附上预览行（玩家从铁砧取出物品时会自动移除，只保留永久等级行）
        appendPreviewLore(stack, preview);
        return stack;
    }

    // = ------------------------------------------------------------
    //  强化台入口
    // = ------------------------------------------------------------

    /** 强化台：应用一个专精方向（点击前界面已展示预览，因此不再附加预览行）。 */
    public static void applyTableUpgrade(ItemStack stack, UpgradeOption option, RegistryAccess registries) {
        if (option == null || getCategory(stack) == UpgradeCategory.NONE) {
            return;
        }
        applyDeltas(stack, option.deltas(), registries);
    }

    // = ------------------------------------------------------------
    //  实际应用强化
    // = ------------------------------------------------------------

    private static void applyDeltas(ItemStack stack, UpgradeDeltas deltas, RegistryAccess registries) {
        if (deltas.isEmpty()) {
            return;
        }
        // 1. 清掉本模组此前添加的 lore（永久等级行 + 旧的预览行）
        stripModLore(stack);

        // 2. 计算强化后的累计数据
        UpgradeData before = UpgradeData.read(stack);
        int baseMax = before.baseMaxDamage() > 0 ? before.baseMaxDamage() : Math.max(1, stack.getMaxDamage());
        UpgradeData after = new UpgradeData(
                before.level() + deltas.levels(),
                before.attack() + deltas.attack(),
                before.durability() + deltas.durability(),
                before.efficiency() + deltas.efficiency(),
                before.power() + deltas.power(),
                before.quickCharge() + deltas.quickCharge(),
                before.armor() + deltas.armor(),
                baseMax,
                1, // loreTail：即将添加 1 行永久等级行
                0);

        // 3. 写入组件
        if (after.attack() != 0 || before.attack() != 0) {
            setAttackBonus(stack, after.attack());
        }
        if (after.armor() != 0 || before.armor() != 0) {
            setArmorBonus(stack, after.armor());
        }
        if (after.durability() != 0 || before.durability() != 0) {
            stack.set(DataComponents.MAX_DAMAGE, Math.max(1, baseMax + after.durability()));
        }
        // 附魔按增量叠加，避免覆盖物品上原有的附魔等级
        addEnchantmentLevel(stack, Enchantments.EFFICIENCY, after.efficiency() - before.efficiency(), registries);
        addEnchantmentLevel(stack, Enchantments.POWER, after.power() - before.power(), registries);
        addEnchantmentLevel(stack, Enchantments.QUICK_CHARGE, after.quickCharge() - before.quickCharge(), registries);

        // 4. 升级后完全修复
        stack.setDamageValue(0);

        // 5. 永久等级标识行（升级后依然保留，让物品看得出来历）
        ItemLore lore = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lore = lore.withLineAdded(makeLevelLine(after.level()));
        stack.set(DataComponents.LORE, lore);

        // 6. 落盘累计数据
        after.write(stack);
    }

    private static void setAttackBonus(ItemStack stack, double amount) {
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();
        ItemAttributeModifiers current = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (ItemAttributeModifiers.Entry entry : current.modifiers()) {
            if (entry.modifier().id().equals(ATTACK_BONUS_ID)) {
                continue; // 去掉旧值，换成新总量
            }
            entries.add(entry);
        }
        if (amount != 0.0) {
            entries.add(new ItemAttributeModifiers.Entry(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(ATTACK_BONUS_ID, amount, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND));
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries, true));
    }

    private static void setArmorBonus(ItemStack stack, double amount) {
        List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();
        ItemAttributeModifiers current = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        EquipmentSlotGroup armorSlot = null;
        for (ItemAttributeModifiers.Entry entry : current.modifiers()) {
            if (entry.modifier().id().equals(ARMOR_BONUS_ID)) {
                continue;
            }
            if (armorSlot == null && entry.attribute().value() == Attributes.ARMOR.value()) {
                armorSlot = entry.slot(); // 复用原版盔甲的槽位组
            }
            entries.add(entry);
        }
        if (amount != 0.0 && armorSlot != null) {
            entries.add(new ItemAttributeModifiers.Entry(
                    Attributes.ARMOR,
                    new AttributeModifier(ARMOR_BONUS_ID, amount, AttributeModifier.Operation.ADD_VALUE),
                    armorSlot));
        }
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries, true));
    }

    private static void addEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> enchantment, int delta, @Nullable RegistryAccess registries) {
        if (delta == 0 || registries == null) {
            return;
        }
        Holder<Enchantment> holder = registries.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantment);
        ItemEnchantments current = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        int newLevel = current.getLevel(holder) + delta;
        if (newLevel <= 0) {
            return;
        }
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        mutable.set(holder, newLevel);
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
    }

    // = ------------------------------------------------------------
    //  预览文本
    // = ------------------------------------------------------------

    /** 生成「即将进行的强化」说明行（基于升级前的物品状态）。 */
    public static List<Component> buildPreviewLines(ItemStack stack, UpgradeData before, UpgradeDeltas deltas, @Nullable RegistryAccess registries) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("emeraldupgrade.preview.header")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        if (deltas.attack() != 0) {
            int from = (int) Math.round(currentAttackDamage(stack));
            lines.add(Component.translatable("emeraldupgrade.preview.attack",
                    "+" + formatNumber(deltas.attack()), String.valueOf(from),
                    String.valueOf(from + (int) Math.round(deltas.attack()))));
        }
        if (deltas.armor() != 0) {
            int from = (int) Math.round(currentArmor(stack));
            lines.add(Component.translatable("emeraldupgrade.preview.armor",
                    "+" + formatNumber(deltas.armor()), String.valueOf(from),
                    String.valueOf(from + (int) Math.round(deltas.armor()))));
        }
        if (deltas.durability() != 0) {
            int from = stack.getMaxDamage();
            lines.add(Component.translatable("emeraldupgrade.preview.durability",
                    String.valueOf(deltas.durability()), String.valueOf(from),
                    String.valueOf(from + deltas.durability())));
        }
        if (deltas.efficiency() != 0) {
            int from = enchantmentLevel(stack, Enchantments.EFFICIENCY, registries);
            lines.add(Component.translatable("emeraldupgrade.preview.efficiency",
                    String.valueOf(deltas.efficiency()), roman(from), roman(from + deltas.efficiency())));
        }
        if (deltas.power() != 0) {
            int from = enchantmentLevel(stack, Enchantments.POWER, registries);
            lines.add(Component.translatable("emeraldupgrade.preview.power",
                    String.valueOf(deltas.power()), roman(from), roman(from + deltas.power())));
        }
        if (deltas.quickCharge() != 0) {
            int from = enchantmentLevel(stack, Enchantments.QUICK_CHARGE, registries);
            lines.add(Component.translatable("emeraldupgrade.preview.quick_charge",
                    String.valueOf(deltas.quickCharge()), roman(from), roman(from + deltas.quickCharge())));
        }
        lines.add(Component.translatable("emeraldupgrade.preview.repair"));
        return lines;
    }

    /** 强化台按钮的悬停说明。 */
    public static List<Component> buildOptionTooltip(ItemStack stack, UpgradeOption option, @Nullable RegistryAccess registries) {
        List<Component> lines = new ArrayList<>();
        lines.add(option.title());
        lines.addAll(buildPreviewLines(stack, UpgradeData.read(stack), option.deltas(), registries));
        lines.add(Component.translatable("emeraldupgrade.table.cost", option.emeraldCost()));
        return lines;
    }

    /** 物品当前的总攻击力（玩家基础 1 点 + 全部攻击伤害修饰符）。 */
    public static double currentAttackDamage(ItemStack stack) {
        double[] total = {1.0};
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers != null) {
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().value() == Attributes.ATTACK_DAMAGE.value()
                        && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE
                        && isMainhand(entry.slot())) {
                    total[0] += entry.modifier().amount();
                }
            }
        }
        return total[0];
    }

    /** 物品当前的总护甲值。 */
    public static double currentArmor(ItemStack stack) {
        double[] total = {0.0};
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers != null) {
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().value() == Attributes.ARMOR.value()
                        && entry.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                    total[0] += entry.modifier().amount();
                }
            }
        }
        return total[0];
    }

    private static boolean isMainhand(EquipmentSlotGroup group) {
        return group == EquipmentSlotGroup.MAINHAND || group == EquipmentSlotGroup.HAND || group == EquipmentSlotGroup.ANY;
    }

    private static int enchantmentLevel(ItemStack stack, ResourceKey<Enchantment> enchantment, @Nullable RegistryAccess registries) {
        if (registries == null) {
            return 0;
        }
        Holder<Enchantment> holder = registries.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantment);
        return stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).getLevel(holder);
    }

    // = ------------------------------------------------------------
    //  Lore 管理（永久等级行 + 临时预览行）
    // = ------------------------------------------------------------

    private static void appendPreviewLore(ItemStack stack, List<Component> lines) {
        if (lines.isEmpty()) {
            return;
        }
        ItemLore lore = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        for (Component line : lines) {
            lore = lore.withLineAdded(line);
        }
        stack.set(DataComponents.LORE, lore);
        UpgradeData data = UpgradeData.read(stack);
        data.withLoreTail(data.loreTail() + lines.size()).withPreviewLines(lines.size()).write(stack);
    }

    /** 取出铁砧结果时调用：只移除预览行，保留「绿宝石强化 +N」永久标识行。 */
    public static void stripPreviewLore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        UpgradeData data = UpgradeData.read(stack);
        int preview = data.previewLines();
        if (preview <= 0) {
            return;
        }
        removeTrailingLore(stack, preview);
        data.withPreviewLines(0).withLoreTail(Math.max(0, data.loreTail() - preview)).write(stack);
    }

    /** 再次强化前调用：移除本模组添加的全部 lore 行。 */
    private static void stripModLore(ItemStack stack) {
        UpgradeData data = UpgradeData.read(stack);
        int tail = data.loreTail();
        if (tail <= 0) {
            return;
        }
        removeTrailingLore(stack, tail);
        data.withLoreTail(0).withPreviewLines(0).write(stack);
    }

    private static void removeTrailingLore(ItemStack stack, int count) {
        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore == null) {
            return;
        }
        List<Component> lines = new ArrayList<>(lore.lines());
        int remove = Math.min(count, lines.size());
        for (int i = 0; i < remove; i++) {
            lines.remove(lines.size() - 1);
        }
        if (lines.isEmpty()) {
            stack.remove(DataComponents.LORE);
        } else {
            stack.set(DataComponents.LORE, new ItemLore(lines));
        }
    }

    private static Component makeLevelLine(int level) {
        return Component.translatable("emeraldupgrade.lore.level", level).withStyle(ChatFormatting.GREEN);
    }

    // = ------------------------------------------------------------
    //  工具方法
    // = ------------------------------------------------------------

    private static String formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(level);
        };
    }
}
