# maplegrove-misidechat

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21-brightgreen)](https://www.minecraft.net/)
[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

## 📖 项目简介

**maplegrove-misidechat** 是一个仿照独立游戏《米塔》(Miside) 中文本掉落效果的 Minecraft 聊天插件。当玩家发送聊天消息时，消息会以单个字符的形式从空中掉落，创造出独特的视觉效果。

> 🎮 效果预览：[Bilibili 视频演示](https://www.bilibili.com/video/BV1YrxTzZEj7)

## ✨ 特性

- 🎬 **文本掉落动画**：聊天消息以字符为单位逐个显示并掉落
- 🌟 **平滑过渡效果**：字符生成时有旋转和缩放动画
- 💫 **渐隐消失**：字符掉落后会逐渐透明并消失
- 🎯 **面向玩家**：文本会根据玩家视角自动调整朝向
- 🔧 **轻量级设计**：无需额外依赖，开箱即用

## 📋 系统要求

| 组件 | 版本要求 |
|------|----------|
| Minecraft | 1.21+ |
| Java | 21+ |
| Spigot/Paper | 1.21-R0.1+ |

## 🚀 安装方法

### 方式一：手动安装

1. 下载最新版本的 `maplegrove-misidechat.jar` 文件
2. 将 jar 文件放入服务器的 `plugins/` 目录
3. 重启服务器

### 方式二：源码编译

```bash
# 克隆仓库
git clone https://github.com/Yamds/maplegrove-misidechat.git
cd maplegrove-misidechat

# 构建项目
./gradlew build

# 编译后的文件位于 build/libs/ 目录
```

## ⚙️ 配置说明

当前版本配置已预设好，配置文件位于 `plugins/maplegrove-misidechat/config.yml`。

> ⚠️ **注意**：由于是自用插件，部分参数已写死在代码中，尚未开放完整的配置选项。

## 🎮 使用方法

插件安装后自动启用，无需任何命令。玩家在聊天栏发送消息时，即可看到文本掉落效果。

## 🏗️ 技术架构

### 核心组件

```
src/main/java/fun/yamds/maplegroveMisidechat/
├── MaplegroveMisidechat.java    # 主类，插件入口
├── entity/
│   └── ChatTextDisplay.java     # 文本显示实体，处理字符渲染和动画
└── listener/
    ├── chatListener.java        # 聊天事件监听器
    └── ChunkListener.java       # 区块事件监听器，清理实体
```

### 关键技术点

- **TextDisplay 实体**：使用 Minecraft 1.21+ 的文本显示实体
- **Transformation API**：通过变换实现字符的旋转、缩放和平移动画
- **骑乘机制**：字符骑乘在掉落物上实现自然的物理掉落效果
- **元数据标记**：使用 Metadata 标识插件实体，便于管理和清理

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 原作者

- **Yamds** - [GitHub](https://github.com/Yamds)

## 🙏 致谢

- 感谢《米塔》(Miside) 游戏提供的创意灵感
- 感谢 SpigotMC 社区的支持

---

<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐ Star！**

Made with ❤️ by Yamds

</div>
