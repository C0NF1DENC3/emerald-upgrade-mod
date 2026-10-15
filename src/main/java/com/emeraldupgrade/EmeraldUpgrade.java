package com.emeraldupgrade;

import com.emeraldupgrade.block.EmeraldUpgradeTableBlock;
import com.emeraldupgrade.block.entity.EmeraldUpgradeTableBlockEntity;
import com.emeraldupgrade.container.EmeraldUpgradeMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 绿宝石强化（Emerald Upgrade）模组主类。
 *
 * <p>机制总览：</p>
 * <ul>
 *   <li><b>铁砧强化</b>：铁砧中放入「可强化物品 + 绿宝石」，结果槽会出现强化后的物品，
 *       并附带清楚的预览说明（攻击力/耐久/附魔变化、消耗数量、等级变化）。</li>
 *   <li><b>绿宝石强化台</b>：全新方块，借鉴附魔台机制——放入物品与绿宝石后提供三个
 *       不同的专精强化方向，玩家三选一。</li>
 * </ul>
 */
@Mod(EmeraldUpgrade.MODID)
public class EmeraldUpgrade {
    public static final String MODID = "emeraldupgrade";

    // ---------- 注册器 ----------
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    // ---------- 注册内容 ----------
    public static final RegistryObject<Block> UPGRADE_TABLE_BLOCK = BLOCKS.register("emerald_upgrade_table",
            () -> new EmeraldUpgradeTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.EMERALD)
                    .sound(SoundType.STONE)
                    .strength(5.0F, 1200.0F)
                    .noOcclusion()));

    public static final RegistryObject<Item> UPGRADE_TABLE_ITEM = ITEMS.register("emerald_upgrade_table",
            () -> new BlockItem(UPGRADE_TABLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<EmeraldUpgradeTableBlockEntity>> UPGRADE_TABLE_BE =
            BLOCK_ENTITY_TYPES.register("emerald_upgrade_table",
                    () -> BlockEntityType.Builder.of(EmeraldUpgradeTableBlockEntity::new, UPGRADE_TABLE_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<EmeraldUpgradeMenu>> UPGRADE_MENU = MENU_TYPES.register("emerald_upgrade_table",
            () -> new MenuType<>(EmeraldUpgradeMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public EmeraldUpgrade() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        // 创造模式物品栏
        modEventBus.addListener(this::addCreativeTabContents);
    }

    private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(UPGRADE_TABLE_BLOCK.get());
        }
    }
}
