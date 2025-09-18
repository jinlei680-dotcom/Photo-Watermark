package com.photowatermark;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EXIF信息读取器
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class ExifReader {
    
    private static final Logger logger = LoggerFactory.getLogger(ExifReader.class);
    
    // 日期格式化器
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * 从图片文件中提取拍摄日期
     * 
     * @param imageFile 图片文件
     * @return 格式化的拍摄日期字符串，如果无法获取则返回null
     */
    public static String extractDateTaken(File imageFile) {
        if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
            logger.warn("无效的图片文件: {}", imageFile);
            return null;
        }
        
        try {
            // 读取图片元数据
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            
            // 尝试从EXIF SubIFD目录获取拍摄时间
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            
            if (directory != null) {
                // 尝试获取原始拍摄时间
                Date dateTaken = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                
                if (dateTaken != null) {
                    String formattedDate = DATE_FORMATTER.format(dateTaken);
                    logger.debug("从文件 {} 提取到拍摄日期: {}", imageFile.getName(), formattedDate);
                    return formattedDate;
                }
                
                // 如果没有原始拍摄时间，尝试获取数字化时间
                Date dateDigitized = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);
                if (dateDigitized != null) {
                    String formattedDate = DATE_FORMATTER.format(dateDigitized);
                    logger.debug("从文件 {} 提取到数字化日期: {}", imageFile.getName(), formattedDate);
                    return formattedDate;
                }
            }
            
            // 如果EXIF SubIFD中没有找到，尝试从其他目录查找
            for (Directory dir : metadata.getDirectories()) {
                if (dir.hasTagName(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
                    Date dateTaken = dir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                    if (dateTaken != null) {
                        String formattedDate = DATE_FORMATTER.format(dateTaken);
                        logger.debug("从文件 {} 的其他目录提取到拍摄日期: {}", imageFile.getName(), formattedDate);
                        return formattedDate;
                    }
                }
            }
            
            logger.warn("文件 {} 中未找到拍摄时间信息", imageFile.getName());
            return null;
            
        } catch (ImageProcessingException e) {
            logger.error("处理图片元数据时出错: {}", imageFile.getName(), e);
            return null;
        } catch (IOException e) {
            logger.error("读取图片文件时出错: {}", imageFile.getName(), e);
            return null;
        } catch (Exception e) {
            logger.error("提取EXIF信息时发生未知错误: {}", imageFile.getName(), e);
            return null;
        }
    }
    
    /**
     * 检查文件是否包含EXIF信息
     * 
     * @param imageFile 图片文件
     * @return 如果包含EXIF信息返回true，否则返回false
     */
    public static boolean hasExifData(File imageFile) {
        if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
            return false;
        }
        
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            return directory != null && directory.getTagCount() > 0;
        } catch (Exception e) {
            logger.debug("检查EXIF信息时出错: {}", imageFile.getName(), e);
            return false;
        }
    }
    
    /**
     * 获取图片的详细EXIF信息（用于调试）
     * 
     * @param imageFile 图片文件
     * @return EXIF信息字符串
     */
    public static String getExifInfo(File imageFile) {
        if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
            return "无效的图片文件";
        }
        
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            StringBuilder info = new StringBuilder();
            
            for (Directory directory : metadata.getDirectories()) {
                info.append("目录: ").append(directory.getName()).append("\n");
                
                for (com.drew.metadata.Tag tag : directory.getTags()) {
                    info.append("  ").append(tag.getTagName())
                        .append(": ").append(tag.getDescription()).append("\n");
                }
                
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        info.append("  错误: ").append(error).append("\n");
                    }
                }
                info.append("\n");
            }
            
            return info.toString();
            
        } catch (Exception e) {
            return "读取EXIF信息时出错: " + e.getMessage();
        }
    }
}