package com.photowatermark;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EXIF读取器测试类
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class ExifReaderTest {
    
    @TempDir
    Path tempDir;
    
    private File testImageFile;
    private File invalidFile;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建测试文件
        testImageFile = tempDir.resolve("test.jpg").toFile();
        testImageFile.createNewFile();
        
        invalidFile = tempDir.resolve("invalid.txt").toFile();
        invalidFile.createNewFile();
    }
    
    @Test
    void testExtractDateTakenWithNullFile() {
        String result = ExifReader.extractDateTaken(null);
        assertNull(result, "空文件应返回null");
    }
    
    @Test
    void testExtractDateTakenWithNonExistentFile() {
        File nonExistentFile = new File("non_existent_file.jpg");
        String result = ExifReader.extractDateTaken(nonExistentFile);
        assertNull(result, "不存在的文件应返回null");
    }
    
    @Test
    void testExtractDateTakenWithInvalidFile() {
        String result = ExifReader.extractDateTaken(invalidFile);
        assertNull(result, "无效的图片文件应返回null");
    }
    
    @Test
    void testHasExifDataWithNullFile() {
        boolean result = ExifReader.hasExifData(null);
        assertFalse(result, "空文件应返回false");
    }
    
    @Test
    void testHasExifDataWithNonExistentFile() {
        File nonExistentFile = new File("non_existent_file.jpg");
        boolean result = ExifReader.hasExifData(nonExistentFile);
        assertFalse(result, "不存在的文件应返回false");
    }
    
    @Test
    void testHasExifDataWithInvalidFile() {
        boolean result = ExifReader.hasExifData(invalidFile);
        assertFalse(result, "无效的图片文件应返回false");
    }
    
    @Test
    void testGetExifInfoWithNullFile() {
        String result = ExifReader.getExifInfo(null);
        assertEquals("无效的图片文件", result, "空文件应返回错误信息");
    }
    
    @Test
    void testGetExifInfoWithNonExistentFile() {
        File nonExistentFile = new File("non_existent_file.jpg");
        String result = ExifReader.getExifInfo(nonExistentFile);
        assertEquals("无效的图片文件", result, "不存在的文件应返回错误信息");
    }
    
    @Test
    void testGetExifInfoWithInvalidFile() {
        String result = ExifReader.getExifInfo(invalidFile);
        assertTrue(result.startsWith("读取EXIF信息时出错:"), "无效文件应返回错误信息");
    }
}