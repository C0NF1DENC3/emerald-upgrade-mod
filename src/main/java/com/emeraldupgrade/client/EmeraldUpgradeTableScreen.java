package com.emeraldupgrade.client;

import com.emeraldupgrade.EmeraldUpgrade;
import com.emeraldupgrade.container.EmeraldUpgradeMenu;
import com.emeraldupgrade.core.UpgradeCategory;
import com.emeraldupgrade.core.UpgradeManager;
import com.emeraldupgrade.core.UpgradeOption;
import com.emeraldupgrade.core.UpgradeOptions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 绿宝石强化台界面：左侧放入绿宝石与工具，右侧三个按钮即三个强化方向。
 * 悬停按钮可查看该方向的完整效果说明（与铁砧预览同一套文案）。
 */
public class EmeraldUpgradeTableScreen extends AbstractContainerScreen<EmeraldUpgradeMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(EmeraldUpgrade.MODID, "textures/gui/container/emerald_upgrade_table.png");

    private static final int BUTTON_X = 62;
    private static final int BUTTON_WIDTH = 102;
    private static final int BUTTON_HEIGHT = 19;
    private static final int BUTTON_TOP = 12;
    private static final int BUTTON_SPACING = 20;
    private static final int BUTTON_COUNT = 3;

    public EmeraldUpgradeTableScreen(EmeraldUpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.renderButtons(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // 界面底色为深色，文字统一使用浅色
        graphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF, false);

        ItemStack tool = this.menu.getSlot(EmeraldUpgradeMenu.TOOL_SLOT).getItem();
        Component levelLine;
        if (tool.isEmpty()) {
            levelLine = Component.translatable("emeraldupgrade.table.hint.empty");
        } else if (UpgradeManager.getCategory(tool) == UpgradeCategory.NONE) {
            levelLine = Component.translatable("emeraldupgrade.table.not_upgradeable");
        } else {
            levelLine = Component.translatable("emeraldupgrade.table.level",
                    UpgradeManager.getLevel(tool), UpgradeManager.MAX_LEVEL);
        }
        graphics.drawString(this.font, levelLine, 8, 16, 0x9CE89C, false);

        Component hint = this.resolveHint();
        if (!hint.getString().isEmpty()) {
            graphics.drawString(this.font, hint, BUTTON_X, 72, 0xC0B8D8, false);
        }
    }

    // ---------------- 三个方向按钮 ----------------

    private void renderButtons(GuiGraphics graphics, int mouseX, int mouseY) {
        ItemStack tool = this.menu.getSlot(EmeraldUpgradeMenu.TOOL_SLOT).getItem();
        ItemStack emeralds = this.menu.getSlot(EmeraldUpgradeMenu.EMERALD_SLOT).getItem();
        UpgradeCategory category = UpgradeManager.getCategory(tool);
        List<UpgradeOption> options = UpgradeOptions.getOptions(category);
        int emeraldCount = emeralds.isEmpty() ? 0 : emeralds.getCount();
        boolean maxed = !tool.isEmpty() && UpgradeManager.getLevel(tool) >= UpgradeManager.MAX_LEVEL;

        for (int i = 0; i < BUTTON_COUNT; i++) {
            int x = this.leftPos + BUTTON_X;
            int y = this.topPos + BUTTON_TOP + i * BUTTON_SPACING;
            boolean hasOption = category.upgradeable() && i < options.size() && !maxed;
            UpgradeOption option = hasOption ? options.get(i) : null;
            boolean affordable = option != null && emeraldCount >= option.emeraldCost();

            // 背景与边框
            int background = option == null ? 0xFF2A1E38 : (affordable ? 0xFF5D3E80 : 0xFF3B2C4E);
            int border = option == null ? 0xFF3A2C4A : (affordable ? 0xFFB08CE8 : 0xFF6A5A7A);
            graphics.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, background);
            graphics.fill(x, y, x + BUTTON_WIDTH, y + 1, border);
            graphics.fill(x, y + BUTTON_HEIGHT - 1, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, border);
            graphics.fill(x, y, x + 1, y + BUTTON_HEIGHT, border);
            graphics.fill(x + BUTTON_WIDTH - 1, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, border);

            // 悬停高亮
            if (option != null && this.isInsideButton(i, mouseX, mouseY)) {
                graphics.fill(x + 1, y + 1, x + BUTTON_WIDTH - 1, y + BUTTON_HEIGHT - 1, 0x30FFFFFF);
            }

            // 文本
            if (option != null) {
                graphics.drawString(this.font, option.title(), x + 6, y + 6, affordable ? 0xFFFFFF : 0x9A9A9A, false);
                Component cost = Component.translatable("emeraldupgrade.table.cost", option.emeraldCost());
                int costWidth = this.font.width(cost);
                graphics.drawString(this.font, cost, x + BUTTON_WIDTH - 6 - costWidth, y + 6,
                        affordable ? 0x7CFC7C : 0xC87A7A, false);
            } else if (i == 0) {
                Component placeholder;
                if (tool.isEmpty()) {
                    placeholder = Component.translatable("emeraldupgrade.table.hint.empty");
                } else if (!category.upgradeable()) {
                    placeholder = Component.translatable("emeraldupgrade.table.not_upgradeable");
                } else if (maxed) {
                    placeholder = Component.translatable("emeraldupgrade.table.hint.max");
                } else {
                    placeholder = Component.translatable("emeraldupgrade.table.hint.select");
                }
                graphics.drawString(this.font, placeholder, x + 6, y + 6, 0x8A8A8A, false);
            }
        }
    }

    private boolean isInsideButton(int index, double mouseX, double mouseY) {
        int x = this.leftPos + BUTTON_X;
        int y = this.topPos + BUTTON_TOP + index * BUTTON_SPACING;
        return mouseX >= x && mouseX < x + BUTTON_WIDTH && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
    }

    private Component resolveHint() {
        ItemStack tool = this.menu.getSlot(EmeraldUpgradeMenu.TOOL_SLOT).getItem();
        ItemStack emeralds = this.menu.getSlot(EmeraldUpgradeMenu.EMERALD_SLOT).getItem();
        if (tool.isEmpty() || emeralds.isEmpty()) {
            return Component.translatable("emeraldupgrade.table.hint.empty");
        }
        UpgradeCategory category = UpgradeManager.getCategory(tool);
        if (category == UpgradeCategory.NONE) {
            return Component.translatable("emeraldupgrade.table.not_upgradeable");
        }
        if (UpgradeManager.getLevel(tool) >= UpgradeManager.MAX_LEVEL) {
            return Component.translatable("emeraldupgrade.table.hint.max");
        }
        int emeraldCount = emeralds.getCount();
        boolean anyAffordable = UpgradeOptions.getOptions(category).stream()
                .anyMatch(option -> option.emeraldCost() <= emeraldCount);
        return Component.translatable(anyAffordable
                ? "emeraldupgrade.table.hint.select"
                : "emeraldupgrade.table.hint.poor");
    }

    // ---------------- 交互与提示框 ----------------

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int hovered = this.getHoveredButton(mouseX, mouseY);
        if (hovered >= 0) {
            List<Component> lines = this.buildOptionTooltip(hovered);
            if (!lines.isEmpty()) {
                graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    private int getHoveredButton(double mouseX, double mouseY) {
        ItemStack tool = this.menu.getSlot(EmeraldUpgradeMenu.TOOL_SLOT).getItem();
        UpgradeCategory category = UpgradeManager.getCategory(tool);
        List<UpgradeOption> options = UpgradeOptions.getOptions(category);
        boolean maxed = UpgradeManager.getLevel(tool) >= UpgradeManager.MAX_LEVEL;
        for (int i = 0; i < BUTTON_COUNT; i++) {
            if (!this.isInsideButton(i, mouseX, mouseY)) {
                continue;
            }
            if (category.upgradeable() && i < options.size() && !maxed) {
                return i;
            }
            return -1;
        }
        return -1;
    }

    private List<Component> buildOptionTooltip(int index) {
        List<Component> lines = new ArrayList<>();
        ItemStack tool = this.menu.getSlot(EmeraldUpgradeMenu.TOOL_SLOT).getItem();
        UpgradeOption option = UpgradeOptions.getOptions(UpgradeManager.getCategory(tool)).get(index);
        net.minecraft.core.RegistryAccess registries =
                this.minecraft.level != null ? this.minecraft.level.registryAccess() : null;
        lines.addAll(UpgradeManager.buildOptionTooltip(tool, option, registries));
        return lines;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int hovered = this.getHoveredButton(mouseX, mouseY);
            if (hovered >= 0) {
                // 通过原版容器按钮包通知服务端（服务端会做权威校验）
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, hovered);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
