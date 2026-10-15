#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成「绿宝石强化」模组所需的 PNG 贴图（仅使用 Python 标准库，无需 PIL）。

生成内容：
  - src/main/resources/assets/emeraldupgrade/textures/block/emerald_upgrade_table.png   (16x16 方块贴图)
  - src/main/resources/assets/emeraldupgrade/textures/gui/container/emerald_upgrade_table.png (256x256 GUI 材质)

如需修改配色，直接改下面的颜色常量后重新运行本脚本即可。
"""
import os
import struct
import zlib

# 目标目录：本脚本位于 <项目>/tools/ 下
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = os.path.join(PROJECT_ROOT, "src", "main", "resources", "assets", "emeraldupgrade")


# ---------------------------------------------------------------------------
# 最小 PNG 写入器
# ---------------------------------------------------------------------------

def write_png(path, width, height, pixels):
    """pixels: 每行一个 bytearray，长度为 width*4（RGBA）。"""
    raw = bytearray()
    for row in pixels:
        raw.append(0)  # filter: none
        raw.extend(row)
    compressed = zlib.compress(bytes(raw), 9)

    def chunk(tag, data):
        return (struct.pack(">I", len(data)) + tag + data
                + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF))

    with open(path, "wb") as f:
        f.write(b"\x89PNG\r\n\x1a\n")
        f.write(chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)))
        f.write(chunk(b"IDAT", compressed))
        f.write(chunk(b"IEND", b""))
    print("已生成:", path)


class Canvas:
    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.pixels = [bytearray(width * 4) for _ in range(height)]

    def set(self, x, y, color):
        if 0 <= x < self.width and 0 <= y < self.height:
            self.pixels[y][x * 4:x * 4 + 4] = bytes(color)

    def fill_rect(self, x, y, w, h, color):
        for yy in range(y, y + h):
            for xx in range(x, x + w):
                self.set(xx, yy, color)

    def save(self, path):
        write_png(path, self.width, self.height, self.pixels)


def noise(x, y, seed=0):
    """确定性伪随机，返回 [0, 1)。"""
    h = (x * 374761393 + y * 668265263 + seed * 1442695041) & 0xFFFFFFFF
    h = (h ^ (h >> 13)) & 0xFFFFFFFF
    h = (h * 1274126177) & 0xFFFFFFFF
    return ((h ^ (h >> 16)) & 0xFFFF) / 65535.0


# ---------------------------------------------------------------------------
# 方块贴图：深绿色石料 + 中央绿宝石镶嵌
# ---------------------------------------------------------------------------

def block_texture():
    c = Canvas(16, 16)
    for y in range(16):
        for x in range(16):
            n = noise(x, y, 1)
            base = int(24 + n * 14)
            r, g, b = max(0, base - 8), min(255, base + 10), max(0, base - 4)

            # 中央绿宝石（菱形渐变）
            d = abs(x - 7.5) + abs(y - 7.5)
            if d < 3.0:
                t = 1.0 - d / 3.0
                r = int(r + (80 - r) * t)
                g = int(g + (230 - g) * t)
                b = int(b + (150 - b) * t)
            elif d < 4.0:
                r, g, b = r + 24, g + 48, b + 30

            # 四角轻微暗化，增加立体感
            edge = max(abs(x - 7.5), abs(y - 7.5)) / 7.5
            shade = 1.0 - 0.18 * edge
            r, g, b = int(r * shade), int(g * shade), int(b * shade)

            # 零星亮微粒
            if noise(x, y, 9) > 0.92:
                r = min(255, r + 26)
                g = min(255, g + 54)
                b = min(255, b + 36)

            c.set(x, y, (r, g, b, 255))
    return c


# ---------------------------------------------------------------------------
# GUI 材质：深紫色面板 + 两个物品槽（坐标与 EmeraldUpgradeMenu 中保持一致）
# ---------------------------------------------------------------------------

def gui_texture():
    c = Canvas(256, 256)

    # 面板 176x166
    c.fill_rect(0, 0, 176, 166, (30, 20, 42, 255))
    # 外边框
    c.fill_rect(0, 0, 176, 2, (92, 62, 120, 255))
    c.fill_rect(0, 164, 176, 2, (92, 62, 120, 255))
    c.fill_rect(0, 0, 2, 166, (92, 62, 120, 255))
    c.fill_rect(174, 0, 2, 166, (92, 62, 120, 255))
    # 内侧高光
    c.fill_rect(2, 2, 172, 1, (66, 44, 88, 255))
    c.fill_rect(2, 2, 1, 162, (66, 44, 88, 255))

    # 物品槽：绿宝石槽 (15,47) 与工具槽 (35,47)，均为 18x18
    for sx in (15, 35):
        c.fill_rect(sx, 47, 18, 18, (14, 12, 18, 255))
        c.fill_rect(sx, 47, 18, 1, (110, 110, 122, 255))       # 上
        c.fill_rect(sx, 47, 1, 18, (110, 110, 122, 255))       # 左
        c.fill_rect(sx, 64, 18, 1, (46, 46, 56, 255))          # 下
        c.fill_rect(sx + 17, 47, 1, 18, (46, 46, 56, 255))     # 右

    return c


def main():
    block_dir = os.path.join(ASSETS, "textures", "block")
    gui_dir = os.path.join(ASSETS, "textures", "gui", "container")
    os.makedirs(block_dir, exist_ok=True)
    os.makedirs(gui_dir, exist_ok=True)

    block_texture().save(os.path.join(block_dir, "emerald_upgrade_table.png"))
    gui_texture().save(os.path.join(gui_dir, "emerald_upgrade_table.png"))
    print("完成。")


if __name__ == "__main__":
    main()
