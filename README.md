# Photo Watermark Tool

一个基于Java的命令行工具，用于为照片添加基于EXIF拍摄日期的水印。

## 功能特性

- 📸 **EXIF信息读取**：自动提取照片的拍摄日期和相机信息
- 🎨 **灵活的水印配置**：支持自定义字体、颜色、位置、透明度等
- 📁 **批量处理**：支持单个文件或整个目录的批量处理
- ⚙️ **配置文件支持**：通过配置文件管理默认设置
- 🔧 **命令行友好**：丰富的命令行选项，易于集成到工作流中

## 系统要求

- Java 8 或更高版本
- Maven 3.6+ （用于构建）

## 快速开始

### 构建项目

```bash
# 克隆项目
git clone https://github.com/your-username/Photo-Watermark-1.git
cd Photo-Watermark-1

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包可执行JAR
mvn package
```

### 基本使用

```bash
# 为单个图片添加水印
java -jar target/photo-watermark-tool.jar -i photo.jpg -t "我的水印文本"

# 批量处理目录中的所有图片
java -jar target/photo-watermark-tool.jar -i /path/to/photos -o /path/to/output

# 使用EXIF拍摄日期作为水印
java -jar target/photo-watermark-tool.jar -i photo.jpg --use-exif-date

# 自定义水印样式
java -jar target/photo-watermark-tool.jar -i photo.jpg -t "水印" --font-size 32 --color red --position bottom-left
```

## 命令行选项

| 选项 | 描述 | 示例 |
|------|------|------|
| `-i, --input` | 输入文件或目录路径 | `-i photo.jpg` |
| `-o, --output` | 输出目录路径 | `-o /path/to/output` |
| `-t, --text` | 自定义水印文本 | `-t "我的水印"` |
| `--use-exif-date` | 使用EXIF拍摄日期作为水印 | `--use-exif-date` |
| `--font-size` | 字体大小 (默认: 24) | `--font-size 32` |
| `--color` | 字体颜色 | `--color red` |
| `--position` | 水印位置 | `--position bottom-right` |
| `--opacity` | 透明度 (0.0-1.0) | `--opacity 0.8` |
| `--margin` | 边距像素 | `--margin 20` |
| `--shadow` | 启用文字阴影 | `--shadow` |
| `-r, --recursive` | 递归处理子目录 | `-r` |
| `--show-config` | 显示当前配置 | `--show-config` |
| `-h, --help` | 显示帮助信息 | `-h` |
| `-v, --version` | 显示版本信息 | `-v` |

## 水印位置选项

- `top-left` - 左上角
- `top-right` - 右上角  
- `center` - 居中
- `bottom-left` - 左下角
- `bottom-right` - 右下角（默认）

## 颜色选项

支持以下颜色名称：
- `black` (默认)
- `white`
- `red`
- `green`
- `blue`
- `yellow`
- `cyan`
- `magenta`
- `gray`

## 配置文件

程序会在以下位置查找配置文件 `watermark.properties`：

1. 当前工作目录
2. 用户主目录
3. 程序资源目录（默认配置）

### 配置文件示例

```properties
# 水印字体配置
watermark.font.size=24
watermark.font.color=black

# 水印位置和样式
watermark.position=bottom-right
watermark.opacity=0.8
watermark.margin=20
watermark.shadow=true

# 输出配置
output.directory=
recursive.processing=false

# 默认水印文本
default.watermark.text=
use.exif.date=true
```

## 支持的图片格式

- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- BMP (.bmp)

## 开发

### 项目结构

```
src/
├── main/java/com/photowatermark/
│   ├── PhotoWatermarkTool.java      # 主程序入口
│   ├── ExifReader.java              # EXIF信息读取
│   ├── WatermarkEngine.java         # 水印绘制引擎
│   ├── FileProcessor.java           # 文件批量处理
│   ├── ConfigManager.java           # 配置管理
│   ├── WatermarkConfig.java         # 水印配置类
│   └── WatermarkPosition.java       # 水印位置枚举
├── main/resources/
│   └── watermark.properties         # 默认配置文件
└── test/java/com/photowatermark/
    ├── ExifReaderTest.java          # EXIF读取测试
    ├── WatermarkEngineTest.java     # 水印引擎测试
    └── FileProcessorTest.java       # 文件处理测试
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=ExifReaderTest

# 生成测试报告
mvn surefire-report:report
```

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 更新日志

### v1.0.0
- 初始版本发布
- 支持基本的水印添加功能
- 支持EXIF信息读取
- 支持批量处理
- 支持配置文件管理
