package com.photowatermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 水印绘制引擎
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class WatermarkEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(WatermarkEngine.class);
    
    /**
     * 为图片添加水印
     * 
     * @param inputFile 输入图片文件
     * @param outputFile 输出图片文件
     * @param watermarkText 水印文本
     * @param config 水印配置
     * @return 是否成功添加水印
     */
    public static boolean addWatermark(File inputFile, File outputFile, String watermarkText, WatermarkConfig config) {
        if (inputFile == null || !inputFile.exists() || !inputFile.isFile()) {
            logger.error("输入文件无效: {}", inputFile);
            return false;
        }
        
        if (watermarkText == null || watermarkText.trim().isEmpty()) {
            logger.error("水印文本不能为空");
            return false;
        }
        
        if (config == null) {
            logger.error("水印配置不能为空");
            return false;
        }
        
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(inputFile);
            if (originalImage == null) {
                logger.error("无法读取图片文件: {}", inputFile.getName());
                return false;
            }
            
            // 创建带水印的图片
            BufferedImage watermarkedImage = createWatermarkedImage(originalImage, watermarkText, config);
            
            // 确保输出目录存在
            File outputDir = outputFile.getParentFile();
            if (outputDir != null && !outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    logger.error("无法创建输出目录: {}", outputDir.getAbsolutePath());
                    return false;
                }
            }
            
            // 获取文件格式
            String format = getImageFormat(inputFile.getName());
            
            // 保存带水印的图片
            boolean success = ImageIO.write(watermarkedImage, format, outputFile);
            
            if (success) {
                logger.info("成功为图片 {} 添加水印，输出到: {}", inputFile.getName(), outputFile.getName());
            } else {
                logger.error("保存带水印图片失败: {}", outputFile.getName());
            }
            
            return success;
            
        } catch (IOException e) {
            logger.error("处理图片时发生IO错误: {}", inputFile.getName(), e);
            return false;
        } catch (Exception e) {
            logger.error("添加水印时发生未知错误: {}", inputFile.getName(), e);
            return false;
        }
    }
    
    /**
     * 创建带水印的图片
     * 
     * @param originalImage 原始图片
     * @param watermarkText 水印文本
     * @param config 水印配置
     * @return 带水印的图片
     */
    private static BufferedImage createWatermarkedImage(BufferedImage originalImage, String watermarkText, WatermarkConfig config) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        // 创建新的图片对象
        BufferedImage watermarkedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = watermarkedImage.createGraphics();
        
        try {
            // 绘制原始图片
            g2d.drawImage(originalImage, 0, 0, null);
            
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // 设置字体
            Font font = new Font(config.getFontName(), config.getFontStyle(), config.getFontSize());
            g2d.setFont(font);
            
            // 获取字体度量信息
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(watermarkText);
            int textHeight = fontMetrics.getHeight();
            
            // 计算水印位置
            Point position = calculateWatermarkPosition(width, height, textWidth, textHeight, config.getPosition(), config.getMargin());
            
            // 设置透明度
            AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, config.getOpacity());
            g2d.setComposite(alphaComposite);
            
            // 绘制阴影（如果启用）
            if (config.isEnableShadow()) {
                g2d.setColor(config.getShadowColor());
                g2d.drawString(watermarkText, position.x + config.getShadowOffset(), position.y + config.getShadowOffset());
            }
            
            // 绘制水印文本
            g2d.setColor(config.getColor());
            g2d.drawString(watermarkText, position.x, position.y);
            
            logger.debug("水印绘制完成 - 位置: ({}, {}), 文本: {}", position.x, position.y, watermarkText);
            
        } finally {
            g2d.dispose();
        }
        
        return watermarkedImage;
    }
    
    /**
     * 计算水印位置
     * 
     * @param imageWidth 图片宽度
     * @param imageHeight 图片高度
     * @param textWidth 文本宽度
     * @param textHeight 文本高度
     * @param position 水印位置
     * @param margin 边距
     * @return 水印坐标
     */
    private static Point calculateWatermarkPosition(int imageWidth, int imageHeight, int textWidth, int textHeight, 
                                                   WatermarkPosition position, int margin) {
        int x, y;
        
        switch (position) {
            case TOP_LEFT:
                x = margin;
                y = margin + textHeight;
                break;
            case TOP_RIGHT:
                x = imageWidth - textWidth - margin;
                y = margin + textHeight;
                break;
            case CENTER:
                x = (imageWidth - textWidth) / 2;
                y = (imageHeight + textHeight) / 2;
                break;
            case BOTTOM_LEFT:
                x = margin;
                y = imageHeight - margin;
                break;
            case BOTTOM_RIGHT:
            default:
                x = imageWidth - textWidth - margin;
                y = imageHeight - margin;
                break;
        }
        
        // 确保坐标不会超出图片边界
        x = Math.max(0, Math.min(x, imageWidth - textWidth));
        y = Math.max(textHeight, Math.min(y, imageHeight));
        
        return new Point(x, y);
    }
    
    /**
     * 根据文件名获取图片格式
     * 
     * @param fileName 文件名
     * @return 图片格式
     */
    private static String getImageFormat(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "jpg";
        }
        
        String lowerCaseName = fileName.toLowerCase();
        if (lowerCaseName.endsWith(".png")) {
            return "png";
        } else if (lowerCaseName.endsWith(".gif")) {
            return "gif";
        } else if (lowerCaseName.endsWith(".bmp")) {
            return "bmp";
        } else {
            return "jpg"; // 默认使用JPEG格式
        }
    }
    
    /**
     * 检查文件是否为支持的图片格式
     * 
     * @param file 文件
     * @return 是否为支持的图片格式
     */
    public static boolean isSupportedImageFormat(File file) {
        if (file == null) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || fileName.endsWith(".gif") || 
               fileName.endsWith(".bmp");
    }
    
    /**
     * 获取图片尺寸信息
     * 
     * @param imageFile 图片文件
     * @return 图片尺寸字符串，格式为"宽x高"
     */
    public static String getImageDimensions(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                return image.getWidth() + "x" + image.getHeight();
            }
        } catch (IOException e) {
            logger.debug("获取图片尺寸时出错: {}", imageFile.getName(), e);
        }
        return "未知";
    }
}