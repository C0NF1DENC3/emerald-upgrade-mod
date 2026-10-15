<div align="center">

# 绿宝石强化 · Emerald Upgrade

**Minecraft 1.21.1 · Forge 52.x · Java 21**

[English](#english-readme) · [中文](#中文说明)

</div>

---

# 中文说明

一个 **Minecraft Forge 1.21.1** 模组：在不破坏原版铁砧机制的前提下，引入**绿宝石升级机制**——任何工具/武器/弓弩/盔甲都可以用绿宝石强化，并且**每次升级前都会在游戏内清楚展示即将发生的变化**。同时新增「绿宝石强化台」，借鉴附魔台机制提供**三个不同的专精方向**供玩家选择。

| 项目 | 内容 |
|---|---|
| **适用 Minecraft 版本** | **1.21.1**（仅此版本，不兼容 1.21 / 1.21.2+） |
| **适用加载器** | **Forge 52.x**（如 52.1.19）。不适用于 NeoForge 或 Fabric |
| **Java 版本** | **Java 21** |
| **ModID** | `emeraldupgrade` |
| **模组版本** | 1.0.0 |
| **许可证** | MIT |

> ⚠️ **务必确认版本**：本模组的 Mixin 直接作用于 Minecraft 内部类，加载器版本或游戏版本
> 不匹配时会**启动失败**，而不是降级运行。下方安装步骤会再次确认。

---

## 一、玩法机制

### 1. 铁砧强化（改造原版铁砧）

在铁砧中放入：

```
[1号槽: 任意可强化物品]  +  [2号槽: 绿宝石 x N]  →  [结果槽: 强化后的物品]
```

- 每 **1 个绿宝石 = 1 级强化**；放入 N 个绿宝石就一次升 N 级（不超过等级上限）。
- 结果槽物品的说明文字（lore）里会**逐条列出即将发生的全部变化**，例如钻石剑 +3 级：

  ```
  即将进行的强化:
    攻击力 +3 (9 → 12)
    耐久上限 +75 (1561 → 1636)
    升级后物品将完全修复
    消耗绿宝石: 3
    强化等级: 0 → 3
  ```

- 取出结果时只消耗绿宝石（**不消耗经验等级**），预览文字会自动移除，只保留一行绿色的 `绿宝石强化 +N` 永久标识。
- 铁砧原有的**修理、合并附魔、重命名**机制完全不受影响——只要 2 号槽放的不是绿宝石，一律走原版逻辑。

### 2. 绿宝石强化台（新增方块，借鉴附魔台）

合成配方（致敬附魔台的材料结构）：

```
  黑曜石  绿宝石块  黑曜石
  绿宝石块   书    绿宝石块
  黑曜石  绿宝石块  黑曜石
```

（4 黑曜石 + 4 绿宝石块 + 1 书）

右键打开界面：**左槽放绿宝石、中间放工具**，右侧出现**三个强化方向按钮**（类似附魔台的三个附魔选项），各有不同：

| 物品类别 | 方向 A | 方向 B | 方向 C |
|---|---|---|---|
| 剑/三叉戟/重锤 | 破甲之路：攻击 +2（3 绿宝石） | 坚韧之路：耐久 +60（2 绿宝石） | 王者之路：攻击 +1、耐久 +30（4 绿宝石） |
| 斧头 | 裂地之路：攻击 +2（3） | 迅捷之路：效率 +1（2） | 全能之路：攻击 +1、耐久 +30、效率 +1（4） |
| 镐/锹/锄/剪刀 | 迅捷之路：效率 +1（2） | 坚韧之路：耐久 +60（2） | 匠魂之路：效率 +1、耐久 +30（3） |
| 弓 | 力量之路：力量 +1（2） | 坚韧之路：耐久 +60（2） | 神射手之路：力量 +1、耐久 +30（3） |
| 弩 | 速射之路：快速装填 +1（2） | 坚韧之路：耐久 +60（2） | 双强化之路：快速装填 +1、耐久 +30（3） |
| 盔甲 | 守护之路：护甲 +1（2） | 坚韧之路：耐久 +40（2） | 均衡之路：护甲 +1、耐久 +20（3） |
| 其他（盾/鞘翅/钓鱼竿…） | 日常保养：耐久 +50（1） | 深度保养：耐久 +120（3） | 全面翻新：耐久 +200（5） |

- **悬停按钮**即可看到该方向的完整效果说明（与铁砧预览同一套文案），点击后由服务端权威校验并执行，同时播放附魔台音效。
- 每次选择任意方向都会使总强化等级 +1。

### 3. 铁砧「标准强化」每级数值

| 类别 | 每级效果 |
|---|---|
| 剑/三叉戟/重锤 | 攻击力 **+1**，耐久上限 **+25** |
| 斧头 | 攻击力 **+1**，耐久 **+20**，效率 **+1 级** |
| 镐/锹/锄/剪刀 | 耐久 **+25**，效率 **+1 级** |
| 弓 | 耐久 **+25**，力量 **+1 级** |
| 弩 | 耐久 **+25**，快速装填 **+1 级** |
| 盔甲 | 护甲 **+1**，耐久 **+10** |
| 其他可耐久物品 | 耐久 **+40** |

所有强化都会**把物品完全修复**。总等级上限 **20 级**（铁砧与强化台共用计数）。

---

## 二、目录结构

```
EmeraldUpgrade/
├── build.gradle / settings.gradle / gradle.properties   # 构建脚本；版本号唯一来源是 gradle.properties
├── gradlew / gradlew.bat / gradle/wrapper/               # Gradle 8.8 Wrapper（含腾讯云镜像源）
└── src/main/
    ├── java/com/emeraldupgrade/
    │   ├── EmeraldUpgrade.java                  # 主类：注册方块/物品/方块实体/菜单
    │   ├── core/                                # 强化核心逻辑（纯原版 API，无网络包）
    │   │   ├── UpgradeCategory.java             #   类别识别 + 每级数值
    │   │   ├── UpgradeDeltas.java               #   一次强化的增量
    │   │   ├── UpgradeData.java                 #   NBT 持久化的累计数据
    │   │   ├── UpgradeManager.java              #   属性计算/应用/预览文本/lore 管理
    │   │   ├── UpgradeOption.java / UpgradeOptions.java  # 强化台的三个方向定义
    │   ├── block/…BlockEntity.java              # 强化台方块与方块实体（Container + MenuProvider）
    │   ├── container/EmeraldUpgradeMenu.java    # 菜单：槽位、快捷移动、方向选择服务端校验
    │   ├── client/EmeraldUpgradeTableScreen.java# 界面：三个方向按钮 + 悬停说明
    │   └── mixin/
    │       ├── AnvilMenuMixin.java              # 铁砧拦截（createResult / onTake）
    │       └── ItemCombinerMenuAccessor.java    # 访问父类继承的 player 字段（@Shadow 做不到）
    ├── resources/
    │   ├── emeraldupgrade.mixins.json
    │   ├── assets/emeraldupgrade/               # 模型、方块状态、语言(中/英)、贴图
    │   └── data/emeraldupgrade/                 # 合成配方、战利品表
    └── templates/mods.toml.in                   # mods.toml 模板（构建时注入版本号）
tools/generate_textures.py                        # 贴图生成脚本（纯标准库，可改配色后重跑）
```

> `mods.toml` **不在 `resources/` 里**：它由 `build.gradle` 的 `generateModsToml` 任务从
> `src/main/templates/mods.toml.in` 生成，版本号统一取自 `gradle.properties`，
> 这样升级版本只需改一处，不会出现「jar 名更新了、游戏里显示的版本还是旧的」这种不一致。

---

## 三、环境要求与构建

**要求**：JDK **21**（Forge 1.21.1 需要）、IntelliJ IDEA（推荐）或 Eclipse。无需自行安装 Gradle——仓库已包含 Wrapper。

```bash
# 1. 生成 IDE 运行配置（首次必须）
./gradlew genIntellijRuns        # IDEA 用户；Eclipse 用户用 genEclipseRuns

# 2. 开发时直接启动游戏（自动加载本模组）
./gradlew runClient

# 3. 构建 jar
./gradlew build
```

构建产物在 `build/libs/emeraldupgrade-mc1.21.1-1.0.0.jar`（命名格式：`<modid>-mc<MC版本>-<模组版本>.jar`）。

> **版本号只改一处**：所有版本信息集中在 `gradle.properties` 的
> `minecraft_version` / `forge_version` / `mod_version`。改这里，jar 文件名、
> 清单、`mods.toml` 与游戏内显示的版本会同步更新——不会出现「文件名是新版本、
> 游戏里却显示旧版本」这种误导。
>
> **国内网络提示**：Wrapper 默认已指向腾讯云镜像
> （`https://mirrors.cloud.tencent.com/gradle/gradle-8.8-bin.zip`）；
> Maven 依赖可在 `build.gradle` 的 `repositories` 中加入阿里云镜像。

## 四、安装到游戏

**先确认版本组合**：本模组只支持 **Minecraft 1.21.1 + Forge 52.x + Java 21**。
因为 Mixin 直接修改 Minecraft 内部类，版本不匹配会**启动失败**而不是降级运行。
如果你的整合包是 1.21、1.21.2+ 或 NeoForge/Fabric，这个模组装不上。

把构建出的 jar（`build/libs/emeraldupgrade-mc1.21.1-1.0.0.jar`）复制到 `.minecraft` 的 `mods` 文件夹。

**你的 PCL 实际游戏目录是 `D:\PCL 正式版 2.10.9\.minecraft\`（注意是隐藏文件夹 `.minecraft`，不是 `.minecraft` 后缀的目录）。** 该启动器对 1.21.1-Forge 版本开启了**版本隔离**，因此 jar 应放在：

```
D:\PCL 正式版 2.10.9\.minecraft\versions\1.21.1-Forge_52.1.19\mods\
```

（该 jar 已于 2026-10-14 由我构建并复制到上述目录；同时也在根目录 `.minecraft\mods\` 放了一份，关闭版本隔离时同样生效。两个位置只会有一个被读取，不会重复加载。）

**安装成功的判断依据**：进入游戏 → 打开模组列表 → 应看到 `Emerald Upgrade (MC 1.21.1)`。
若列表里没有，说明 jar 没放对上面那个带 `versions\...` 的目录。

功能位置：创造模式物品栏 →「功能方块」页 →「绿宝石强化台」；铁砧强化无需任何新物品，直接就能用。

---

## 五、构建要点与 1.21.1 踩坑记录

以下均为本次实际构建（ForgeGradle 6.0.24 + Gradle 8.8 + JDK 21）验证过的结论，与代码一一对应：

1. **必须 JDK 21**：ForgeGradle 6 不支持在 Java 25 下运行（`build.gradle` 中 `java.toolchain.languageVersion = 21`）。
2. **`dependencies` 中必须显式声明** `minecraft 'net.minecraftforge:forge:1.21.1-52.1.19'`，否则配置阶段报 `Missing 'minecraft' dependency`。
3. **ForgeGradle 6 移除了 `mixin {}` 扩展块**（FG3 时代写法），直接写会报 `Could not find method mixin()`。
4. **Forge 1.21.1 生产环境用的就是 Mojang 官方映射名**（已反编译验证：`AnvilMenu.createResult` 等符号直接可读，无 SRG 名），因此 Mixin **不需要 refmap**，也无需 Mixin 注解处理器。
5. **jar 清单必须加 `MixinConfigs` 属性**（见 `build.gradle` 中 `tasks.named('jar')` 的 manifest 配置），否则生产环境不注册 Mixin（开发环境不受影响）——这是 1.20.6+ 的已知变化。
6. **界面注册**：`RegisterMenuScreensEvent` 在 1.21.1 已被移除，改为在 `FMLClientSetupEvent` 里调用 vanilla 的 `MenuScreens.register(...)`（Forge 已将其补丁为 public）。
7. **附魔是动态注册表**：`Enchantments.EFFICIENCY` 等常量是 `ResourceKey<Enchantment>` 而非 `Holder`，需经 `level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key)` 转换（本模组通过方法参数把 `RegistryAccess` 一路传到 `UpgradeManager`）。
8. **其他 1.21.1 API 细节**：`ItemAttributeModifiers.Entry` 的槽位访问器叫 `slot()`；`Containers` 在 `net.minecraft.world` 包；`BaseEntityBlock` 需要实现 `codec()`；`GuiGraphics.renderTooltip(Font, List<Component>, ...)` 需多传一个 `Optional.empty()`。
9. **Mixin 的 `@Shadow` 不能 shadow 父类继承来的字段**（例如 `ItemCombinerMenu.player`）：生产环境会抛
   `InvalidMixinException: @Shadow field player was not located in the target class` 并在启动加载 `Blocks` 时直接崩溃。
   继承字段必须改用 `@Accessor` 访问接口读取（见 `ItemCombinerMenuAccessor.java`）。
10. **开发环境不会注册 classpath 模组的 Mixin 配置**（开发目录没有 jar 清单，而 Forge 只认清单里的 `MixinConfigs`），
    所以 Mixin 相关改动在 `runClient` 里可能「看起来正常」实则未生效，**必须打生产 jar 用真实游戏验证**。
    本次修复即通过复刻 PCL 启动命令启动真实游戏确认：模组被发现、Mixin 正常 apply、世界正常加载。

## 六、技术要点（供学习/二次开发参考）

1. **铁砧拦截用 Mixin**（`AnvilMenuMixin`）：
   - 在 `AnvilMenu#createResult` 的 `HEAD` 处注入：若 1 号槽是绿宝石、0 号槽是可强化物品，就生成强化结果并 `ci.cancel()` 跳过原版逻辑；否则直接放行。
   - 复用原版字段 `repairItemCountCost` 让「取出结果」时自动消耗对应数量绿宝石；把 `cost`（DataSlot）置 0 实现零经验花费。
   - 在 `onTake` 的 `HEAD` 注入，移除结果物品上的预览 lore 行（通过 NBT 记录的行数精确移除，永久等级行保留）。
2. **属性写入原版数据组件**（1.21.1 组件系统）：
   - 攻击力/护甲 → `minecraft:attribute_modifiers`（`ItemAttributeModifiers.Entry` + `AttributeModifier`，用固定 ID 修饰符实现「替换旧值」而不是叠加错误）；
   - 耐久 → `minecraft:max_damage`；
   - 效率/力量/快速装填 → `minecraft:enchantments`（`ItemEnchantments.Mutable` 按增量叠加，不会覆盖物品原有附魔等级）；
   - 累计数据 → `minecraft:custom_data` 下的 `emeraldupgrade` 子标签。
3. **强化台零自定义网络包**：界面按钮通过原版
   `gameMode.handleInventoryButtonClick(containerId, buttonId)` → 服务端 `AbstractContainerMenu#clickMenuButton` 完成通信，槽位内容由原版 Slot 同步机制自动同步；方向选项由两端共享的 `UpgradeOptions` 静态表生成，服务端再做权威校验（防作弊）。
4. **贴图由脚本生成**：`tools/generate_textures.py` 用纯 Python 标准库写 PNG，改颜色常量后重跑即可，不依赖任何图像库。

## 七、调参指南

| 想改什么 | 改哪里 |
|---|---|
| 总等级上限（默认 20） | `UpgradeManager.MAX_LEVEL` |
| 铁砧每级数值 | `UpgradeCategory` 枚举构造参数 |
| 强化台三个方向的效果/价格 | `UpgradeOptions` 静态块 |
| 绿宝石→经验花费 | `AnvilMenuMixin` 中 `this.cost.set(0)` |
| 合成配方 | `data/emeraldupgrade/recipes/emerald_upgrade_table.json` |
| 界面配色/槽位坐标 | `tools/generate_textures.py` 与 `EmeraldUpgradeMenu` 中的坐标常量 |

## 八、已知限制

- 在铁砧里同时放绿宝石和输入名字时，以强化为先（重命名会被忽略）；需要重命名时请不要在2 号槽放绿宝石。
- 效率等附魔可超过原版上限（如效率 VI），组件系统支持任意等级，属有意为之。
- 本模组只改铁砧计算结果，不修改原版任何配方/数值，与其他模组冲突面很小；若同时安装了其他修改铁砧的模组，以加载顺序后者的 Mixin 为准。

## 九、源码与仓库

- GitHub：<https://github.com/C0NF1DENC3/emerald-upgrade-mod>
- 许可：MIT

> **关于仓库名**：GitHub 规定仓库名仅允许 ASCII 字母/数字与 `-` `_` `.`，连续的非 ASCII
> 字符会被静默折叠成单个连字符，因此「绿宝石强化模组」无法出现在仓库名里。
> 中文信息放在仓库描述与本文标题中。

克隆与推送：

```bash
git clone https://github.com/C0NF1DENC3/emerald-upgrade-mod.git
cd emerald-upgrade-mod
```

构建（仓库已包含 Gradle Wrapper，**无需自行安装 Gradle**，只需本机有 JDK 21）：

```bash
./gradlew build        # Linux / macOS / Git Bash
gradlew.bat build      # Windows CMD / PowerShell
```

> **Wrapper 使用国内镜像**：`gradle/wrapper/gradle-wrapper.properties` 里的 `distributionUrl`
> 指向腾讯云镜像（`mirrors.cloud.tencent.com/gradle`）。原因：本网络环境下
> `services.gradle.org` 的连通性探测会失败，导致 `gradle wrapper` 任务报
> "Test of distribution url failed"。若你在海外或该地址不可用，把它改回官方地址即可：
>
> ```properties
> distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
> ```
>
> **Windows + 代理环境下推送失败**（`schannel: ... CRYPT_E_NO_REVOCATION_CHECK`）：
> 证书吊销检查（CRL）请求被本地代理拦截。此时用环境变量绕过：
>
> ```bash
> GIT_SSL_NO_VERIFY=1 git push origin main
> ```
>
> 补充：`git config http.schannelCheckRevoke false` 在 Git 2.55 + 代理场景下对该错误无效，
> 需用上面的环境变量或改用 SSH remote。

---
---

<a id="english-readme"></a>

# English README

<div align="center">

**Minecraft 1.21.1 · Forge 52.x · Java 21**

[中文](#中文说明) · [English](#english-readme)

</div>

A **Minecraft Forge 1.21.1** mod that adds **emerald-based upgrading** without breaking vanilla
anvil mechanics — any tool, weapon, bow, crossbow or piece of armour can be upgraded with emeralds,
and **every upgrade shows you exactly what will change before you commit**. It also adds an
**Emerald Upgrade Table**, modelled on the enchanting table, offering **three different
specialisations** to choose from.

| | |
|---|---|
| **Minecraft version** | **1.21.1** only (not compatible with 1.21 or 1.21.2+) |
| **Mod loader** | **Forge 52.x** (e.g. 52.1.19). Not for NeoForge or Fabric |
| **Java version** | **Java 21** |
| **Mod ID** | `emeraldupgrade` |
| **Mod version** | 1.0.0 |
| **License** | MIT |

> ⚠️ **Check your version first.** This mod uses Mixins against Minecraft internals, so a
> mismatched game or loader version causes a **launch failure** rather than degraded behaviour.
> The installation steps below repeat this check.

---

## 1. Gameplay mechanics

### 1.1 Anvil upgrading

Place the following in an anvil:

```
[Slot 1: any upgradable item]  +  [Slot 2: emeralds x N]  →  [Result: upgraded item]
```

- Every **1 emerald = 1 upgrade level**. Put N emeralds in to gain N levels at once (never above the cap).
- The result item's **lore lists every single change that is about to happen**, e.g. a diamond
  sword upgraded by 3 levels:

  ```
  即将进行的强化:
    攻击力 +3 (9 → 12)
    耐久上限 +75 (1561 → 1636)
    升级后物品将完全修复
    消耗绿宝石: 3
    强化等级: 0 → 3
  ```

- Take the item out and the preview lines disappear — only the permanent green
  **“Emerald Upgrade +N”** tag remains, and the item is **fully repaired**.
- **No experience levels are consumed**; emeralds are the only cost.
- **Vanilla anvil mechanics are fully preserved.** As long as slot 2 does *not* contain emeralds,
  repairing, combining and renaming behave exactly as in vanilla Minecraft.

### 1.2 Emerald Upgrade Table

Crafted with **4 obsidian + 4 emeralds + 1 book**.

Right-click it to open an enchanting-table-style GUI. Put emeralds in the left slot and your item
in the middle slot, and **three buttons** appear on the right — one per specialisation. Hover any
button to see the full preview of what it does; click to apply.

The three directions are intentionally different in character, so the choice matters. Example for a
diamond sword (costs in parentheses):

| Direction | Effect per level | Character |
|---|---|---|
| 破甲 Sundering | +2 attack damage (3 emeralds) | Aggressive |
| 坚韧 Fortitude | +60 max durability (2 emeralds) | Tanky |
| 王者 Sovereign | +1 attack, +30 durability (4 emeralds) | Balanced |

Standard anvil upgrading — per level, up to **20 levels** total (the anvil and the table share
one counter):

| Item type | Per-level effect |
|---|---|
| Sword, trident, mace | +1 attack damage, +25 max durability |
| Axe | +1 attack damage, +20 durability, +1 efficiency |
| Pickaxe, shovel, hoe, shears | +25 durability, +1 efficiency |
| Bow | +25 durability, +1 Power |
| Crossbow | +25 durability, +1 Quick Charge |
| Helmet, chestplate, leggings, boots | +1 armour, +10 durability |
| Other damageable items (shield, elytra, fishing rod…) | +40 durability |

Every upgrade fully repairs the item.

## 2. Installation

You need **exactly** the right combination:

1. Install **Minecraft Forge 52.x** for **Minecraft 1.21.1** (e.g. 52.1.19).
   Get it from the [official Forge downloads page](https://files.minecraftforge.net/).
2. Download the `.jar` from the [Releases page](https://github.com/C0NF1DENC3/emerald-upgrade-mod/releases)
   (or build it yourself with `./gradlew build` — see below).
3. Copy it into your profile's `mods` folder.
   - Standard: `.minecraft/mods/`
   - If your launcher isolates versions (e.g. MultiMC, PCL, HMCL with version isolation):
     `.minecraft/versions/1.21.1-Forge_52.1.19/mods/`
4. Launch the **Forge 52.x** profile.

The mod shows up in **Mods → Mod List** as `Emerald Upgrade (MC 1.21.1)`.

### Verifying the install

- **In-game**: open the mod list; the entry must read `Emerald Upgrade (MC 1.21.1)`.
- **Server**: the same line appears in the console on startup.
- **If Minecraft crashes on launch** with a mixin error, your loader version is wrong —
  check that you are running Forge 52.x, not NeoForge and not a different Minecraft version.

## 3. Building from source

Requires **JDK 21**. The repository includes the Gradle Wrapper, so you do **not** need to install
Gradle yourself.

```bash
git clone https://github.com/C0NF1DENC3/emerald-upgrade-mod.git
cd emerald-upgrade-mod
./gradlew build        # Linux / macOS / Git Bash
gradlew.bat build      # Windows CMD / PowerShell
```

Output: `build/libs/emeraldupgrade-mc1.21.1-1.0.0.jar`

Other useful tasks:

```bash
./gradlew runClient            # launch a dev client with the mod loaded
./gradlew genIntellijRuns      # generate IDE run configurations
```

To upgrade to a different Minecraft version, edit **only** `gradle.properties`
(`minecraft_version`, `forge_version`) — the jar name, manifest and in-game version metadata are
all derived from it.

> **Gradle mirror**: `gradle/wrapper/gradle-wrapper.properties` points at a Chinese mirror
> (`mirrors.cloud.tencent.com/gradle`) because `services.gradle.org` is unreachable from the
> network this was developed on. To use the official URL instead:
>
> ```properties
> distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
> ```

## 4. Technical notes

1. **Mixin on the anvil.** `AnvilMenuMixin` injects at the head of `AnvilMenu#createResult` and
   cancels it when the second slot holds emeralds. Anything else falls through to vanilla logic.
   Two things worth knowing if you extend this:
   - `@Shadow` **cannot** shadow an inherited field. `player` lives on `ItemCombinerMenu`, not
     `AnvilMenu`, so shadowing it throws `InvalidMixinException` at launch. Use an
     `@Accessor` mixin (`ItemCombinerMenuAccessor`) instead.
   - ForgeGradle 6 removed the `mixin {}` extension block from older MDKs, and Forge 1.20.6+
     requires `MixinConfigs` in the jar manifest or the mixin is silently never applied.
   - **The dev environment does not register mixins for classpath mods** (no jar manifest), so a
     dev run cannot validate mixin changes. Always verify with a production jar.
2. **Vanilla data components.** Upgrades are written to Minecraft 1.21.1's own components
   (`attribute_modifiers`, `max_damage`, `enchantments`); the level counter and preview live in
   `custom_data`. This means `/give` and NBT viewers see everything correctly.
3. **No custom network packets.** The table GUI reuses vanilla
   `gameMode.handleInventoryButtonClick(containerId, buttonId)` → server-side
   `AbstractContainerMenu#clickMenuButton`; slot contents sync through the normal Slot mechanism.
   Both sides derive the three options from the same `UpgradeOptions` table, and the server
   re-validates every click.
4. **Procedural textures.** `tools/generate_textures.py` writes the PNGs using only the Python
   standard library — no image library required. Edit the colour constants and re-run it.

## 5. Tuning values

| What to change | Where |
|---|---|
| Max upgrade level (default 20) | `UpgradeManager.MAX_LEVEL` |
| Per-level anvil numbers | `UpgradeCategory` enum constructor args |
| The three table directions' effects/costs | `UpgradeOptions` static block |
| Emerald → XP cost | `this.cost.set(0)` in `AnvilMenuMixin` |
| Crafting recipe | `data/emeraldupgrade/recipes/emerald_upgrade_table.json` |
| GUI colours / slot positions | `tools/generate_textures.py` and the constants in `EmeraldUpgradeMenu` |

## 6. Known limitations

- Renaming an item in the anvil is ignored while emeralds sit in slot 2 (upgrading takes priority).
  Remove the emeralds first if you want to rename.
- Efficiency and similar enchantments may exceed the vanilla cap (e.g. Efficiency VI) — the
  component system supports arbitrary levels. This is intentional.
- The mod only changes the anvil's *result*; it modifies no vanilla recipe or numeric value, so it
  has a very small conflict surface. If another anvil-modifying mod is installed, whichever Mixin
  loads last wins.

## 7. Repository

- GitHub: <https://github.com/C0NF1DENC3/emerald-upgrade-mod>
- License: MIT

> **On the repository name:** GitHub only allows ASCII letters, digits, `-`, `_` and `.` in
> repository names; runs of non-ASCII characters are silently collapsed into a single hyphen.
> The Chinese name therefore lives in the repository description and in the README title.
>
> **Pushing from Windows behind a proxy** fails with
> `schannel: ... CRYPT_E_NO_REVOCATION_CHECK` because the CRL check is blocked. Work around it
> with an environment variable:
>
> ```bash
> GIT_SSL_NO_VERIFY=1 git push origin main
> ```
>
> Note: `git config http.schannelCheckRevoke false` does **not** fix this on Git 2.55 behind a
> proxy; use the variable above or switch to an SSH remote.
