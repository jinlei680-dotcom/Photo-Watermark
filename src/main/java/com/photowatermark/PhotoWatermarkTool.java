package com.photowatermark;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;

/**
 * 照片水印工具主程序
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class PhotoWatermarkTool {
    
    private static final Logger logger = LoggerFactory.getLogger(PhotoWatermarkTool.class);
    
    public static void main(String[] args) {
        try {
            // 创建命令行选项
            Options options = createOptions();
            
            // 解析命令行参数
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            
            // 处理帮助选项
            if (cmd.hasOption("help") || args.length == 0) {
                printHelp(options);
                return;
            }
            
            // 处理版本选项
            if (cmd.hasOption("version")) {
                printVersion();
                return;
            }
            
            // 处理配置显示选项
            if (cmd.hasOption("show-config")) {
                showCurrentConfig(cmd);
                return;
            }
            
            // 解析配置参数
            WatermarkConfig config = parseConfig(cmd);
            
            // 获取输入和输出路径
            String inputPath = cmd.getOptionValue("input");
            String outputPath = cmd.getOptionValue("output");
            
            if (inputPath == null) {
                System.err.println("错误: 必须指定输入路径");
                printHelp(options);
                System.exit(1);
            }
            
            // 执行水印处理
            processWatermark(inputPath, outputPath, config, cmd);
            
        } catch (ParseException e) {
            System.err.println("命令行参数解析错误: " + e.getMessage());
            printHelp(createOptions());
            System.exit(1);
        } catch (Exception e) {
            logger.error("程序执行过程中发生错误", e);
            System.err.println("程序执行失败: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 创建命令行选项
     * 
     * @return 命令行选项
     */
    private static Options createOptions() {
        Options options = new Options();
        
        // 基本选项
        options.addOption("h", "help", false, "显示帮助信息");
        options.addOption("v", "version", false, "显示版本信息");
        options.addOption("c", "config", true, "指定配置文件路径");
        options.addOption(null, "show-config", false, "显示当前配置");
        
        // 输入输出选项
        options.addOption("i", "input", true, "输入文件或目录路径 (必需)");
        options.addOption("o", "output", true, "输出目录路径");
        options.addOption("r", "recursive", false, "递归处理子目录");
        
        // 水印文本选项
        options.addOption("t", "text", true, "自定义水印文本");
        options.addOption("e", "exif", false, "使用EXIF拍摄日期作为水印");
        
        // 水印样式选项
        options.addOption("s", "size", true, "字体大小 (默认: 24)");
        options.addOption("f", "font", true, "字体名称 (默认: Arial)");
        options.addOption("p", "position", true, "水印位置 (TOP_LEFT|TOP_RIGHT|CENTER|BOTTOM_LEFT|BOTTOM_RIGHT)");
        options.addOption("a", "alpha", true, "透明度 (0.0-1.0, 默认: 0.8)");
        options.addOption("m", "margin", true, "边距 (像素, 默认: 20)");
        
        // 颜色选项
        options.addOption(null, "color", true, "水印颜色 (格式: R,G,B 如 255,255,255)");
        options.addOption(null, "shadow", false, "启用阴影效果");
        options.addOption(null, "shadow-color", true, "阴影颜色 (格式: R,G,B)");
        
        return options;
    }
    
    /**
     * 解析配置参数
     * 
     * @param cmd 命令行参数
     * @return 水印配置
     */
    private static WatermarkConfig parseConfig(CommandLine cmd) {
        // 创建配置管理器
        ConfigManager configManager;
        if (cmd.hasOption("config")) {
            configManager = new ConfigManager(cmd.getOptionValue("config"));
        } else {
            configManager = new ConfigManager();
        }
        
        // 从配置文件创建基础配置
        WatermarkConfig config = configManager.createWatermarkConfig();
        
        // 命令行参数覆盖配置文件设置
        if (cmd.hasOption("size")) {
            try {
                int fontSize = Integer.parseInt(cmd.getOptionValue("size"));
                config.setFontSize(fontSize);
            } catch (NumberFormatException e) {
                System.err.println("警告: 无效的字体大小，使用默认值");
            }
        }
        
        if (cmd.hasOption("font")) {
            config.setFontName(cmd.getOptionValue("font"));
        }
        
        if (cmd.hasOption("position")) {
            try {
                WatermarkPosition position = WatermarkPosition.valueOf(cmd.getOptionValue("position").toUpperCase());
                config.setPosition(position);
            } catch (IllegalArgumentException e) {
                System.err.println("警告: 无效的水印位置，使用默认值");
            }
        }
        
        if (cmd.hasOption("alpha")) {
            try {
                float alpha = Float.parseFloat(cmd.getOptionValue("alpha"));
                if (alpha >= 0.0f && alpha <= 1.0f) {
                    config.setOpacity(alpha);
                } else {
                    System.err.println("警告: 透明度必须在0.0-1.0之间，使用默认值");
                }
            } catch (NumberFormatException e) {
                System.err.println("警告: 无效的透明度值，使用默认值");
            }
        }
        
        if (cmd.hasOption("margin")) {
            try {
                int margin = Integer.parseInt(cmd.getOptionValue("margin"));
                config.setMargin(margin);
            } catch (NumberFormatException e) {
                System.err.println("警告: 无效的边距值，使用默认值");
            }
        }
        
        if (cmd.hasOption("color")) {
            Color color = parseColor(cmd.getOptionValue("color"));
            if (color != null) {
                config.setColor(color);
            }
        }
        
        if (cmd.hasOption("shadow")) {
            config.setEnableShadow(true);
        }
        
        if (cmd.hasOption("shadow-color")) {
            Color shadowColor = parseColor(cmd.getOptionValue("shadow-color"));
            if (shadowColor != null) {
                config.setShadowColor(shadowColor);
            }
        }
        
        return config;
    }
    
    /**
     * 解析颜色字符串
     * 
     * @param colorStr 颜色字符串 (格式: R,G,B)
     * @return 颜色对象
     */
    private static Color parseColor(String colorStr) {
        try {
            String[] rgb = colorStr.split(",");
            if (rgb.length == 3) {
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());
                
                if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                    return new Color(r, g, b);
                }
            }
        } catch (NumberFormatException e) {
            // 忽略解析错误
        }
        
        System.err.println("警告: 无效的颜色格式，应为 R,G,B (如 255,255,255)");
        return null;
    }
    
    /**
     * 执行水印处理
     * 
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     * @param config 水印配置
     * @param cmd 命令行参数
     */
    private static void processWatermark(String inputPath, String outputPath, WatermarkConfig config, CommandLine cmd) {
        // 确定输出目录
        File outputDir;
        if (outputPath != null) {
            outputDir = new File(outputPath);
        } else {
            ConfigManager configManager = new ConfigManager();
            outputDir = new File(configManager.getOutputDirectory());
        }
        
        // 确定水印文本和处理选项
        boolean useExifDate = cmd.hasOption("exif");
        String customWatermark = cmd.getOptionValue("text");
        
        if (!useExifDate && (customWatermark == null || customWatermark.trim().isEmpty())) {
            ConfigManager configManager = new ConfigManager();
            useExifDate = configManager.useExifDate();
            customWatermark = configManager.getDefaultWatermarkText();
        }
        
        boolean recursive = cmd.hasOption("recursive");
        
        // 创建文件处理器
        FileProcessor processor = new FileProcessor(config, useExifDate, customWatermark, outputDir, recursive);
        
        // 执行处理
        System.out.println("开始处理...");
        System.out.println("输入路径: " + inputPath);
        System.out.println("输出目录: " + outputDir.getAbsolutePath());
        System.out.println("水印配置: " + config);
        
        FileProcessor.ProcessResult result = processor.processPath(inputPath);
        
        if (result.isSuccess()) {
            System.out.println("处理完成！");
            System.out.println(result.getMessage());
        } else {
            System.err.println("处理失败: " + result.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 显示当前配置
     * 
     * @param cmd 命令行参数
     */
    private static void showCurrentConfig(CommandLine cmd) {
        ConfigManager configManager;
        if (cmd.hasOption("config")) {
            configManager = new ConfigManager(cmd.getOptionValue("config"));
        } else {
            configManager = new ConfigManager();
        }
        
        configManager.printCurrentConfig();
    }
    
    /**
     * 打印帮助信息
     * 
     * @param options 命令行选项
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        
        System.out.println("Photo Watermark Tool v1.0.0");
        System.out.println("一个用于给照片添加水印的命令行工具\n");
        
        formatter.printHelp("java -jar photo-watermark-tool.jar [选项]", 
                          "\n选项说明:", options, 
                          "\n使用示例:\n" +
                          "  # 使用EXIF日期作为水印处理单个文件\n" +
                          "  java -jar photo-watermark-tool.jar -i photo.jpg -e -o ./output\n\n" +
                          "  # 使用自定义文本批量处理目录\n" +
                          "  java -jar photo-watermark-tool.jar -i ./photos -t \"我的照片\" -r -o ./output\n\n" +
                          "  # 自定义水印样式\n" +
                          "  java -jar photo-watermark-tool.jar -i photo.jpg -t \"水印\" -s 32 -p CENTER --color 255,0,0\n");
    }
    
    /**
     * 打印版本信息
     */
    private static void printVersion() {
        System.out.println("Photo Watermark Tool");
        System.out.println("版本: 1.0.0");
        System.out.println("作者: PhotoWatermark Team");
        System.out.println("Java版本: " + System.getProperty("java.version"));
    }
}