# Photo Watermark Tool - 用户使用指南

本指南将帮助您快速上手 Photo Watermark Tool，包含详细的使用示例和最佳实践。

## 目录

- [快速开始](#快速开始)
- [基本用法](#基本用法)
- [高级功能](#高级功能)
- [配置文件](#配置文件)
- [批量处理](#批量处理)
- [编程接口](#编程接口)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)

---

## 快速开始

### 1. 环境准备

确保您的系统已安装：
- Java 8 或更高版本
- Maven 3.6+（如果需要从源码构建）

### 2. 获取程序

**方式一：下载预编译版本**
```bash
# 下载最新版本的JAR文件
wget https://github.com/your-username/Photo-Watermark-1/releases/latest/photo-watermark-tool.jar
```

**方式二：从源码构建**
```bash
git clone https://github.com/your-username/Photo-Watermark-1.git
cd Photo-Watermark-1
mvn clean package
# 生成的JAR文件位于 target/photo-watermark-tool.jar
```

### 3. 第一次使用

```bash
# 为单张照片添加简单水印
java -jar photo-watermark-tool.jar -i photo.jpg -t "我的第一个水印"

# 查看帮助信息
java -jar photo-watermark-tool.jar --help
```

---

## 基本用法

### 单文件处理

#### 添加自定义文本水印
```bash
java -jar photo-watermark-tool.jar -i photo.jpg -t "© 2024 张三"
```

#### 使用EXIF拍摄日期作为水印
```bash
java -jar photo-watermark-tool.jar -i photo.jpg --use-exif-date
```

#### 指定输出目录
```bash
java -jar photo-watermark-tool.jar -i photo.jpg -o /path/to/output -t "水印文本"
```

### 目录批量处理

#### 处理目录中的所有图片
```bash
java -jar photo-watermark-tool.jar -i /path/to/photos -o /path/to/output --use-exif-date
```

#### 递归处理子目录
```bash
java -jar photo-watermark-tool.jar -i /path/to/photos -r --use-exif-date
```

### 自定义水印样式

#### 调整字体大小和颜色
```bash
java -jar photo-watermark-tool.jar -i photo.jpg -t "大红字" --font-size 48 --color red
```

#### 设置水印位置和透明度
```bash
java -jar photo-watermark-tool.jar -i photo.jpg -t "左上角水印" --position top-left --opacity 0.6
```

#### 添加文字阴影和边距
```bash
java -jar photo-watermark-tool.jar -i photo.jpg -t "带阴影" --shadow --margin 30
```

---

## 高级功能

### 水印位置详解

| 位置选项 | 描述 | 适用场景 |
|----------|------|----------|
| `top-left` | 左上角 | 版权标识 |
| `top-right` | 右上角 | 作者署名 |
| `center` | 居中 | 强调水印 |
| `bottom-left` | 左下角 | 时间戳 |
| `bottom-right` | 右下角 | 默认位置，最常用 |

### 颜色和透明度

#### 支持的颜色
```bash
# 基本颜色
--color black    # 黑色（默认）
--color white    # 白色
--color red      # 红色
--color green    # 绿色
--color blue     # 蓝色
--color yellow   # 黄色
--color cyan     # 青色
--color magenta  # 洋红色
--color gray     # 灰色
```

#### 透明度设置
```bash
# 透明度范围：0.0（完全透明）到 1.0（完全不透明）
--opacity 0.3    # 30% 不透明度，适合背景水印
--opacity 0.8    # 80% 不透明度，默认值
--opacity 1.0    # 完全不透明，适合重要标识
```

### 组合使用示例

#### 专业摄影水印
```bash
java -jar photo-watermark-tool.jar \
  -i wedding_photos/ \
  -o watermarked_photos/ \
  -t "© 2024 专业摄影工作室 | 联系电话：138-0000-0000" \
  --font-size 20 \
  --color white \
  --position bottom-right \
  --opacity 0.8 \
  --shadow \
  --margin 25 \
  -r
```

#### 旅行照片时间戳
```bash
java -jar photo-watermark-tool.jar \
  -i travel_photos/ \
  -o timestamped_photos/ \
  --use-exif-date \
  --font-size 16 \
  --color yellow \
  --position bottom-left \
  --opacity 0.9 \
  --shadow \
  -r
```

#### 社交媒体水印
```bash
java -jar photo-watermark-tool.jar \
  -i social_media_photos/ \
  -t "@我的用户名" \
  --font-size 24 \
  --color white \
  --position top-right \
  --opacity 0.7 \
  --shadow
```

---

## 配置文件

### 创建配置文件

在当前目录或用户主目录创建 `watermark.properties` 文件：

```properties
# 水印字体配置
watermark.font.size=24
watermark.font.color=white

# 水印位置和样式
watermark.position=bottom-right
watermark.opacity=0.8
watermark.margin=20
watermark.shadow=true

# 输出配置
output.directory=/Users/username/WatermarkedPhotos
recursive.processing=true

# 默认水印文本
default.watermark.text=© 2024 我的照片
use.exif.date=false
```

### 使用配置文件

```bash
# 程序会自动加载配置文件
java -jar photo-watermark-tool.jar -i photos/

# 查看当前配置
java -jar photo-watermark-tool.jar --show-config

# 命令行参数会覆盖配置文件设置
java -jar photo-watermark-tool.jar -i photos/ --font-size 32
```

---

## 批量处理

### 处理策略

#### 按文件类型过滤
```bash
# 只处理JPEG文件
find /path/to/photos -name "*.jpg" -o -name "*.jpeg" | \
xargs -I {} java -jar photo-watermark-tool.jar -i {} -t "水印"
```

#### 按日期分组处理
```bash
# 为不同年份的照片添加不同水印
java -jar photo-watermark-tool.jar -i photos/2023/ -t "© 2023 回忆" --use-exif-date
java -jar photo-watermark-tool.jar -i photos/2024/ -t "© 2024 新篇章" --use-exif-date
```

### 性能优化

#### 大量文件处理
```bash
# 使用较小的字体和简单样式提高处理速度
java -jar photo-watermark-tool.jar \
  -i large_photo_collection/ \
  --use-exif-date \
  --font-size 16 \
  --opacity 0.8 \
  --margin 15 \
  -r
```

#### 并行处理（Shell脚本示例）
```bash
#!/bin/bash
# parallel_watermark.sh

input_dir="$1"
output_dir="$2"
watermark_text="$3"

# 创建输出目录
mkdir -p "$output_dir"

# 并行处理（限制并发数为CPU核心数）
find "$input_dir" -name "*.jpg" -o -name "*.jpeg" -o -name "*.png" | \
xargs -n 1 -P $(nproc) -I {} bash -c '
  filename=$(basename "{}")
  java -jar photo-watermark-tool.jar -i "{}" -o "'$output_dir'" -t "'$watermark_text'"
'
```

---

## 编程接口

### Java API 使用示例

#### 基本水印添加
```java
import com.photowatermark.*;
import java.awt.Color;
import java.io.File;

public class WatermarkExample {
    public static void main(String[] args) {
        // 创建配置
        WatermarkConfig config = new WatermarkConfig();
        config.setFontSize(24);
        config.setColor(Color.WHITE);
        config.setPosition(WatermarkPosition.BOTTOM_RIGHT);
        config.setOpacity(0.8f);
        config.setShadowEnabled(true);
        
        // 添加水印
        File input = new File("photo.jpg");
        File output = new File("watermarked_photo.jpg");
        
        boolean success = WatermarkEngine.addWatermark(
            input, output, "© 2024 我的照片", config
        );
        
        if (success) {
            System.out.println("水印添加成功！");
        } else {
            System.out.println("水印添加失败！");
        }
    }
}
```

#### 批量处理示例
```java
import com.photowatermark.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BatchWatermarkExample {
    public static void main(String[] args) {
        // 加载配置
        WatermarkConfig config = ConfigManager.loadConfig();
        
        // 创建处理器
        FileProcessor processor = new FileProcessor(config);
        
        // 批量处理
        ProcessResult result = processor.processPath(
            "/path/to/photos",     // 输入目录
            "/path/to/output",     // 输出目录
            null,                  // 使用默认水印文本
            true                   // 使用EXIF日期
        );
        
        // 输出结果
        System.out.printf("处理完成 - 总计: %d, 成功: %d, 失败: %d, 耗时: %d ms%n",
            result.getTotalCount(),
            result.getSuccessCount(),
            result.getFailureCount(),
            result.getProcessingTime()
        );
    }
}
```

#### EXIF信息读取示例
```java
import com.photowatermark.ExifReader;
import java.io.File;
import java.util.Map;

public class ExifExample {
    public static void main(String[] args) {
        File photo = new File("photo.jpg");
        
        // 检查是否有EXIF数据
        if (ExifReader.hasExifData(photo)) {
            // 获取拍摄日期
            String dateTaken = ExifReader.extractDateTaken(photo);
            System.out.println("拍摄日期: " + dateTaken);
            
            // 获取所有EXIF信息
            Map<String, String> exifInfo = ExifReader.getExifInfo(photo);
            exifInfo.forEach((key, value) -> 
                System.out.println(key + ": " + value)
            );
        } else {
            System.out.println("该图片不包含EXIF数据");
        }
    }
}
```

---

## 最佳实践

### 1. 水印设计原则

#### 可读性优先
```bash
# 好的做法：使用对比色和阴影
java -jar photo-watermark-tool.jar -i photo.jpg -t "水印" --color white --shadow

# 避免：在浅色背景上使用浅色水印
java -jar photo-watermark-tool.jar -i photo.jpg -t "水印" --color yellow --opacity 0.3
```

#### 适当的大小和位置
```bash
# 好的做法：根据图片大小调整字体
java -jar photo-watermark-tool.jar -i large_photo.jpg -t "水印" --font-size 32
java -jar photo-watermark-tool.jar -i small_photo.jpg -t "水印" --font-size 16

# 好的做法：选择不影响主体的位置
java -jar photo-watermark-tool.jar -i portrait.jpg -t "水印" --position bottom-right
```

### 2. 文件管理

#### 保持原文件
```bash
# 好的做法：指定输出目录，保留原文件
java -jar photo-watermark-tool.jar -i photos/ -o watermarked/ -t "水印"

# 避免：直接覆盖原文件
```

#### 有意义的文件命名
```bash
# 程序会自动添加 "_watermarked" 后缀
# 输入：photo.jpg -> 输出：photo_watermarked.jpg
```

### 3. 批量处理优化

#### 预处理检查
```bash
# 检查支持的文件格式
find photos/ -type f \( -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.png" \) | wc -l
```

#### 分批处理大量文件
```bash
# 每次处理1000个文件
find photos/ -name "*.jpg" | head -1000 | \
xargs -I {} java -jar photo-watermark-tool.jar -i {} -t "水印"
```

### 4. 配置管理

#### 为不同用途创建不同配置
```bash
# 社交媒体配置
cp watermark.properties social_media.properties
# 编辑 social_media.properties...

# 专业摄影配置
cp watermark.properties professional.properties
# 编辑 professional.properties...
```

---

## 常见问题

### Q1: 为什么有些图片没有添加水印？

**A:** 可能的原因：
1. 图片格式不支持（只支持 JPG, PNG, GIF, BMP）
2. 图片文件损坏
3. 权限不足，无法写入输出目录
4. 水印文本为空且未启用EXIF日期

**解决方法：**
```bash
# 检查文件格式
java -jar photo-watermark-tool.jar -i photo.xxx --show-config

# 检查权限
ls -la /path/to/output/

# 确保有水印文本
java -jar photo-watermark-tool.jar -i photo.jpg -t "测试水印"
```

### Q2: 如何处理没有EXIF数据的图片？

**A:** 设置默认水印文本：
```bash
# 方法1：命令行指定
java -jar photo-watermark-tool.jar -i photo.jpg -t "默认水印"

# 方法2：配置文件设置
echo "default.watermark.text=默认水印文本" >> watermark.properties
```

### Q3: 水印太大或太小怎么办？

**A:** 根据图片尺寸调整：
```bash
# 获取图片尺寸
java -jar photo-watermark-tool.jar -i photo.jpg --show-config

# 大图片使用大字体
java -jar photo-watermark-tool.jar -i large_photo.jpg -t "水印" --font-size 48

# 小图片使用小字体
java -jar photo-watermark-tool.jar -i small_photo.jpg -t "水印" --font-size 12
```

### Q4: 如何批量处理不同子目录？

**A:** 使用递归选项或脚本：
```bash
# 方法1：递归处理
java -jar photo-watermark-tool.jar -i root_photos/ -r -t "水印"

# 方法2：Shell脚本
for dir in photos/*/; do
  java -jar photo-watermark-tool.jar -i "$dir" -t "$(basename "$dir") 专辑"
done
```

### Q5: 程序运行很慢怎么办？

**A:** 优化建议：
1. 减小字体大小
2. 关闭阴影效果
3. 使用简单的水印文本
4. 分批处理大量文件
5. 确保有足够的内存

```bash
# 快速处理模式
java -Xmx2g -jar photo-watermark-tool.jar \
  -i photos/ \
  --font-size 16 \
  --opacity 0.8 \
  -t "简单水印"
```

---

## 技术支持

如果您遇到问题或有建议，请：

1. 查看 [API文档](API.md) 了解详细的技术信息
2. 查看 [开发者指南](DEVELOPER_GUIDE.md) 了解项目结构
3. 在 GitHub 上提交 Issue
4. 发送邮件至：support@photowatermark.com

---

**祝您使用愉快！** 📸✨