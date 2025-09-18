# Photo Watermark Tool - API 文档

本文档详细介绍了 Photo Watermark Tool 的核心 API 类和方法。

## 目录

- [WatermarkEngine](#watermarkengine) - 水印绘制引擎
- [FileProcessor](#fileprocessor) - 文件批量处理器
- [ExifReader](#exifreader) - EXIF 信息读取器
- [ConfigManager](#configmanager) - 配置管理器
- [WatermarkConfig](#watermarkconfig) - 水印配置类
- [WatermarkPosition](#watermarkposition) - 水印位置枚举

---

## WatermarkEngine

水印绘制引擎，负责在图片上添加水印。

### 静态方法

#### `addWatermark(File inputFile, File outputFile, String watermarkText, WatermarkConfig config)`

为图片添加水印。

**参数：**
- `inputFile` (File) - 输入图片文件
- `outputFile` (File) - 输出图片文件
- `watermarkText` (String) - 水印文本
- `config` (WatermarkConfig) - 水印配置

**返回值：**
- `boolean` - 是否成功添加水印

**示例：**
```java
File input = new File("photo.jpg");
File output = new File("watermarked_photo.jpg");
WatermarkConfig config = new WatermarkConfig();
config.setFontSize(24);
config.setColor(Color.BLACK);
config.setPosition(WatermarkPosition.BOTTOM_RIGHT);

boolean success = WatermarkEngine.addWatermark(input, output, "我的水印", config);
```

#### `isSupportedImageFormat(File file)`

检查文件是否为支持的图片格式。

**参数：**
- `file` (File) - 要检查的文件

**返回值：**
- `boolean` - 是否为支持的图片格式

**支持的格式：** JPG, JPEG, PNG, GIF, BMP

#### `getImageDimensions(File imageFile)`

获取图片尺寸信息。

**参数：**
- `imageFile` (File) - 图片文件

**返回值：**
- `String` - 图片尺寸字符串，格式为"宽x高"

---

## FileProcessor

文件批量处理器，支持单个文件和目录的批量处理。

### 构造方法

#### `FileProcessor(WatermarkConfig config)`

创建文件处理器实例。

**参数：**
- `config` (WatermarkConfig) - 水印配置

### 实例方法

#### `processPath(String inputPath, String outputDir, String customWatermark, boolean useExifDate)`

处理指定路径的文件或目录。

**参数：**
- `inputPath` (String) - 输入路径（文件或目录）
- `outputDir` (String) - 输出目录路径（可为null，使用输入文件目录）
- `customWatermark` (String) - 自定义水印文本（可为null）
- `useExifDate` (boolean) - 是否使用EXIF拍摄日期作为水印

**返回值：**
- `ProcessResult` - 处理结果对象

**示例：**
```java
WatermarkConfig config = new WatermarkConfig();
FileProcessor processor = new FileProcessor(config);

ProcessResult result = processor.processPath(
    "/path/to/photos", 
    "/path/to/output", 
    null, 
    true
);

System.out.println("处理完成 - 成功: " + result.getSuccessCount() + 
                  ", 失败: " + result.getFailureCount());
```

#### `processBatch(List<File> imageFiles, File outputDir, String customWatermark, boolean useExifDate)`

批量处理图片文件列表。

**参数：**
- `imageFiles` (List<File>) - 图片文件列表
- `outputDir` (File) - 输出目录
- `customWatermark` (String) - 自定义水印文本
- `useExifDate` (boolean) - 是否使用EXIF拍摄日期

**返回值：**
- `ProcessResult` - 处理结果对象

---

## ExifReader

EXIF 信息读取器，用于从图片中提取元数据。

### 静态方法

#### `extractDateTaken(File imageFile)`

提取图片的拍摄日期。

**参数：**
- `imageFile` (File) - 图片文件

**返回值：**
- `String` - 格式化的拍摄日期字符串（yyyy-MM-dd HH:mm:ss），如果无法提取则返回null

**示例：**
```java
File photo = new File("photo.jpg");
String dateTaken = ExifReader.extractDateTaken(photo);
if (dateTaken != null) {
    System.out.println("拍摄日期: " + dateTaken);
}
```

#### `hasExifData(File imageFile)`

检查图片是否包含EXIF数据。

**参数：**
- `imageFile` (File) - 图片文件

**返回值：**
- `boolean` - 是否包含EXIF数据

#### `getExifInfo(File imageFile)`

获取图片的详细EXIF信息。

**参数：**
- `imageFile` (File) - 图片文件

**返回值：**
- `Map<String, String>` - EXIF信息键值对映射

---

## ConfigManager

配置管理器，负责加载和管理应用程序配置。

### 静态方法

#### `loadConfig()`

加载配置文件。

**返回值：**
- `WatermarkConfig` - 加载的配置对象

**配置文件查找顺序：**
1. 当前工作目录的 `watermark.properties`
2. 用户主目录的 `watermark.properties`
3. 程序资源目录的默认配置

#### `saveConfig(WatermarkConfig config, String filePath)`

保存配置到指定文件。

**参数：**
- `config` (WatermarkConfig) - 要保存的配置
- `filePath` (String) - 配置文件路径

**示例：**
```java
WatermarkConfig config = ConfigManager.loadConfig();
config.setFontSize(32);
ConfigManager.saveConfig(config, "my-watermark.properties");
```

---

## WatermarkConfig

水印配置类，包含所有水印相关的配置选项。

### 属性和方法

#### 字体配置
- `getFontSize()` / `setFontSize(int fontSize)` - 字体大小
- `getColor()` / `setColor(Color color)` - 字体颜色

#### 位置和样式
- `getPosition()` / `setPosition(WatermarkPosition position)` - 水印位置
- `getOpacity()` / `setOpacity(float opacity)` - 透明度 (0.0-1.0)
- `getMargin()` / `setMargin(int margin)` - 边距像素
- `isShadowEnabled()` / `setShadowEnabled(boolean enabled)` - 是否启用阴影

#### 文本配置
- `getDefaultWatermarkText()` / `setDefaultWatermarkText(String text)` - 默认水印文本
- `isUseExifDate()` / `setUseExifDate(boolean useExifDate)` - 是否使用EXIF日期

#### 处理选项
- `isRecursive()` / `setRecursive(boolean recursive)` - 是否递归处理子目录
- `getOutputDirectory()` / `setOutputDirectory(String outputDir)` - 输出目录

**示例：**
```java
WatermarkConfig config = new WatermarkConfig();
config.setFontSize(28);
config.setColor(Color.WHITE);
config.setPosition(WatermarkPosition.BOTTOM_LEFT);
config.setOpacity(0.9f);
config.setMargin(15);
config.setShadowEnabled(true);
config.setDefaultWatermarkText("© 2024 我的照片");
```

---

## WatermarkPosition

水印位置枚举，定义水印在图片上的位置。

### 枚举值

- `TOP_LEFT` - 左上角
- `TOP_RIGHT` - 右上角
- `CENTER` - 居中
- `BOTTOM_LEFT` - 左下角
- `BOTTOM_RIGHT` - 右下角（默认）

**示例：**
```java
config.setPosition(WatermarkPosition.BOTTOM_RIGHT);
```

---

## ProcessResult

处理结果类，包含批量处理的统计信息。

### 方法

- `isSuccess()` - 是否处理成功
- `getSuccessCount()` - 成功处理的文件数量
- `getFailureCount()` - 处理失败的文件数量
- `getTotalCount()` - 总文件数量
- `getProcessingTime()` - 处理耗时（毫秒）
- `getErrorMessage()` - 错误消息（如果有）

---

## 异常处理

所有API方法都包含适当的异常处理：

- **IOException** - 文件读写错误
- **IllegalArgumentException** - 参数无效
- **NullPointerException** - 空指针异常

建议在使用API时进行适当的异常捕获和处理。

## 线程安全

- `WatermarkEngine` 的静态方法是线程安全的
- `ExifReader` 的静态方法是线程安全的
- `FileProcessor` 实例不是线程安全的，每个线程应使用独立的实例
- `WatermarkConfig` 不是线程安全的，应避免在多线程环境中修改同一实例

## 性能建议

1. **批量处理**：使用 `FileProcessor.processBatch()` 而不是多次调用单文件处理方法
2. **配置复用**：重复使用同一个 `WatermarkConfig` 实例
3. **文件格式检查**：在处理前使用 `WatermarkEngine.isSupportedImageFormat()` 检查文件格式
4. **内存管理**：处理大量图片时注意内存使用，考虑分批处理