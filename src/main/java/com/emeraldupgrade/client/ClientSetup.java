package com.emeraldupgrade.client;

import com.emeraldupgrade.EmeraldUpgrade;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端专用注册：把强化台菜单绑定到界面。
 *
 * <p>Forge 1.21.1 已移除 RegisterMenuScreensEvent，改为在 FMLClientSetupEvent 中
 * 直接调用 vanilla 的 MenuScreens.register（Forge 已将其补丁为 public）。</p>
 */
@Mod.EventBusSubscriber(modid = EmeraldUpgrade.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(EmeraldUpgrade.UPGRADE_MENU.get(), EmeraldUpgradeTableScreen::new));
    }
}
