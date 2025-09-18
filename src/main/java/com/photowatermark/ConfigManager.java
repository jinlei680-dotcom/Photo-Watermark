package com.photowatermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * 配置管理器
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    
    private static final String DEFAULT_CONFIG_FILE = "watermark.properties";
    private static final String USER_CONFIG_FILE = System.getProperty("user.home") + "/.photowatermark/config.properties";
    
    private Properties properties;
    
    /**
     * 构造函数
     */
    public ConfigManager() {
        this.properties = new Properties();
        loadDefaultConfig();
        loadUserConfig();
    }
    
    /**
     * 从指定文件加载配置
     * 
     * @param configFile 配置文件路径
     */
    public ConfigManager(String configFile) {
        this.properties = new Properties();
        loadDefaultConfig();
        loadConfigFromFile(configFile);
    }
    
    /**
     * 加载默认配置
     */
    private void loadDefaultConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.debug("成功加载默认配置文件");
            } else {
                logger.warn("未找到默认配置文件: {}", DEFAULT_CONFIG_FILE);
                setDefaultValues();
            }
        } catch (IOException e) {
            logger.error("加载默认配置文件时出错", e);
            setDefaultValues();
        }
    }
    
    /**
     * 加载用户配置
     */
    private void loadUserConfig() {
        File userConfigFile = new File(USER_CONFIG_FILE);
        if (userConfigFile.exists()) {
            loadConfigFromFile(USER_CONFIG_FILE);
        } else {
            logger.debug("用户配置文件不存在: {}", USER_CONFIG_FILE);
        }
    }
    
    /**
     * 从文件加载配置
     * 
     * @param configFile 配置文件路径
     */
    private void loadConfigFromFile(String configFile) {
        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            properties.load(inputStream);
            logger.info("成功加载配置文件: {}", configFile);
        } catch (IOException e) {
            logger.error("加载配置文件时出错: {}", configFile, e);
        }
    }
    
    /**
     * 设置默认值
     */
    private void setDefaultValues() {
        properties.setProperty("watermark.font.name", "Arial");
        properties.setProperty("watermark.font.size", "24");
        properties.setProperty("watermark.font.style", "1");
        properties.setProperty("watermark.color.red", "255");
        properties.setProperty("watermark.color.green", "255");
        properties.setProperty("watermark.color.blue", "255");
        properties.setProperty("watermark.position", "BOTTOM_RIGHT");
        properties.setProperty("watermark.opacity", "0.8");
        properties.setProperty("watermark.margin", "20");
        properties.setProperty("watermark.shadow.enabled", "true");
        properties.setProperty("watermark.shadow.offset", "2");
        properties.setProperty("watermark.shadow.color.red", "0");
        properties.setProperty("watermark.shadow.color.green", "0");
        properties.setProperty("watermark.shadow.color.blue", "0");
        properties.setProperty("output.directory", "./output");
        properties.setProperty("process.recursive", "true");
        properties.setProperty("watermark.default.text", "Photo Watermark");
        properties.setProperty("watermark.use.exif.date", "true");
        
        logger.info("使用默认配置值");
    }
    
    /**
     * 创建水印配置对象
     * 
     * @return 水印配置
     */
    public WatermarkConfig createWatermarkConfig() {
        WatermarkConfig config = new WatermarkConfig();
        
        // 字体配置
        config.setFontName(getProperty("watermark.font.name", "Arial"));
        config.setFontSize(getIntProperty("watermark.font.size", 24));
        config.setFontStyle(getIntProperty("watermark.font.style", Font.BOLD));
        
        // 颜色配置
        int red = getIntProperty("watermark.color.red", 255);
        int green = getIntProperty("watermark.color.green", 255);
        int blue = getIntProperty("watermark.color.blue", 255);
        config.setColor(new Color(red, green, blue));
        
        // 位置配置
        String positionStr = getProperty("watermark.position", "BOTTOM_RIGHT");
        try {
            config.setPosition(WatermarkPosition.valueOf(positionStr));
        } catch (IllegalArgumentException e) {
            logger.warn("无效的水印位置配置: {}, 使用默认值", positionStr);
            config.setPosition(WatermarkPosition.BOTTOM_RIGHT);
        }
        
        // 透明度和边距
        config.setOpacity(getFloatProperty("watermark.opacity", 0.8f));
        config.setMargin(getIntProperty("watermark.margin", 20));
        
        // 阴影配置
        config.setEnableShadow(getBooleanProperty("watermark.shadow.enabled", true));
        config.setShadowOffset(getIntProperty("watermark.shadow.offset", 2));
        
        int shadowRed = getIntProperty("watermark.shadow.color.red", 0);
        int shadowGreen = getIntProperty("watermark.shadow.color.green", 0);
        int shadowBlue = getIntProperty("watermark.shadow.color.blue", 0);
        config.setShadowColor(new Color(shadowRed, shadowGreen, shadowBlue));
        
        return config;
    }
    
    /**
     * 获取字符串属性
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 获取整数属性
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("无效的整数配置 {}: {}, 使用默认值 {}", key, properties.getProperty(key), defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * 获取浮点数属性
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public float getFloatProperty(String key, float defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Float.parseFloat(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("无效的浮点数配置 {}: {}, 使用默认值 {}", key, properties.getProperty(key), defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔属性
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    /**
     * 获取输出目录
     * 
     * @return 输出目录路径
     */
    public String getOutputDirectory() {
        return getProperty("output.directory", "./output");
    }
    
    /**
     * 是否递归处理
     * 
     * @return 是否递归处理
     */
    public boolean isRecursive() {
        return getBooleanProperty("process.recursive", true);
    }
    
    /**
     * 获取默认水印文本
     * 
     * @return 默认水印文本
     */
    public String getDefaultWatermarkText() {
        return getProperty("watermark.default.text", "Photo Watermark");
    }
    
    /**
     * 是否使用EXIF日期
     * 
     * @return 是否使用EXIF日期
     */
    public boolean useExifDate() {
        return getBooleanProperty("watermark.use.exif.date", true);
    }
    
    /**
     * 保存用户配置
     * 
     * @param config 水印配置
     * @return 是否保存成功
     */
    public boolean saveUserConfig(WatermarkConfig config) {
        try {
            // 创建用户配置目录
            File userConfigDir = new File(USER_CONFIG_FILE).getParentFile();
            if (!userConfigDir.exists() && !userConfigDir.mkdirs()) {
                logger.error("无法创建用户配置目录: {}", userConfigDir.getAbsolutePath());
                return false;
            }
            
            // 更新属性
            updatePropertiesFromConfig(config);
            
            // 保存到文件
            try (FileOutputStream outputStream = new FileOutputStream(USER_CONFIG_FILE)) {
                properties.store(outputStream, "Photo Watermark Tool User Configuration");
                logger.info("成功保存用户配置到: {}", USER_CONFIG_FILE);
                return true;
            }
            
        } catch (IOException e) {
            logger.error("保存用户配置时出错", e);
            return false;
        }
    }
    
    /**
     * 从配置对象更新属性
     * 
     * @param config 水印配置
     */
    private void updatePropertiesFromConfig(WatermarkConfig config) {
        properties.setProperty("watermark.font.name", config.getFontName());
        properties.setProperty("watermark.font.size", String.valueOf(config.getFontSize()));
        properties.setProperty("watermark.font.style", String.valueOf(config.getFontStyle()));
        
        Color color = config.getColor();
        properties.setProperty("watermark.color.red", String.valueOf(color.getRed()));
        properties.setProperty("watermark.color.green", String.valueOf(color.getGreen()));
        properties.setProperty("watermark.color.blue", String.valueOf(color.getBlue()));
        
        properties.setProperty("watermark.position", config.getPosition().name());
        properties.setProperty("watermark.opacity", String.valueOf(config.getOpacity()));
        properties.setProperty("watermark.margin", String.valueOf(config.getMargin()));
        
        properties.setProperty("watermark.shadow.enabled", String.valueOf(config.isEnableShadow()));
        properties.setProperty("watermark.shadow.offset", String.valueOf(config.getShadowOffset()));
        
        Color shadowColor = config.getShadowColor();
        properties.setProperty("watermark.shadow.color.red", String.valueOf(shadowColor.getRed()));
        properties.setProperty("watermark.shadow.color.green", String.valueOf(shadowColor.getGreen()));
        properties.setProperty("watermark.shadow.color.blue", String.valueOf(shadowColor.getBlue()));
    }
    
    /**
     * 打印当前配置
     */
    public void printCurrentConfig() {
        System.out.println("=== 当前配置 ===");
        properties.forEach((key, value) -> System.out.println(key + " = " + value));
        System.out.println("===============");
    }
}