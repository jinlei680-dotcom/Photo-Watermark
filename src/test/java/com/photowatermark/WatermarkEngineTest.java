package com.photowatermark;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 水印引擎测试类
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class WatermarkEngineTest {
    
    @TempDir
    Path tempDir;
    
    private File testImageFile;
    private File outputFile;
    private WatermarkConfig config;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建测试图片
        testImageFile = tempDir.resolve("test.jpg").toFile();
        BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 200, 200);
        g2d.dispose();
        ImageIO.write(testImage, "jpg", testImageFile);
        
        // 创建输出文件路径
        outputFile = tempDir.resolve("output.jpg").toFile();
        
        // 创建默认配置
        config = new WatermarkConfig();
        config.setFontSize(24);
        config.setColor(Color.BLACK);
        config.setPosition(WatermarkPosition.BOTTOM_RIGHT);
        config.setOpacity(0.8f);
        config.setMargin(20);
    }
    
    @Test
    void testAddWatermarkWithValidInputs() {
        boolean result = WatermarkEngine.addWatermark(testImageFile, outputFile, "Test Watermark", config);
        assertTrue(result, "添加水印应该成功");
        assertTrue(outputFile.exists(), "输出文件应该存在");
    }
    
    @Test
    void testAddWatermarkWithNullInputFile() {
        boolean result = WatermarkEngine.addWatermark(null, outputFile, "Test Watermark", config);
        assertFalse(result, "空输入文件应该失败");
    }
    
    @Test
    void testAddWatermarkWithNonExistentInputFile() {
        File nonExistentFile = new File("non_existent.jpg");
        boolean result = WatermarkEngine.addWatermark(nonExistentFile, outputFile, "Test Watermark", config);
        assertFalse(result, "不存在的输入文件应该失败");
    }
    
    @Test
    void testAddWatermarkWithEmptyWatermarkText() {
        boolean result = WatermarkEngine.addWatermark(testImageFile, outputFile, "", config);
        assertFalse(result, "空水印文本应该失败");
    }
    
    @Test
    void testAddWatermarkWithNullWatermarkText() {
        boolean result = WatermarkEngine.addWatermark(testImageFile, outputFile, null, config);
        assertFalse(result, "空水印文本应该失败");
    }
    
    @Test
    void testAddWatermarkWithNullConfig() {
        boolean result = WatermarkEngine.addWatermark(testImageFile, outputFile, "Test Watermark", null);
        assertFalse(result, "空配置应该失败");
    }
    
    @Test
    void testIsSupportedImageFormatWithValidFormats() {
        assertTrue(WatermarkEngine.isSupportedImageFormat(new File("test.jpg")));
        assertTrue(WatermarkEngine.isSupportedImageFormat(new File("test.jpeg")));
        assertTrue(WatermarkEngine.isSupportedImageFormat(new File("test.png")));
        assertTrue(WatermarkEngine.isSupportedImageFormat(new File("test.gif")));
        assertTrue(WatermarkEngine.isSupportedImageFormat(new File("test.bmp")));
    }
    
    @Test
    void testIsSupportedImageFormatWithInvalidFormats() {
        assertFalse(WatermarkEngine.isSupportedImageFormat(new File("test.txt")));
        assertFalse(WatermarkEngine.isSupportedImageFormat(new File("test.doc")));
        assertFalse(WatermarkEngine.isSupportedImageFormat(new File("test")));
    }
    
    @Test
    void testIsSupportedImageFormatWithNullFile() {
        assertFalse(WatermarkEngine.isSupportedImageFormat(null));
    }
    
    @Test
    void testGetImageDimensions() {
        String dimensions = WatermarkEngine.getImageDimensions(testImageFile);
        assertEquals("200x200", dimensions, "图片尺寸应该正确");
    }
    
    @Test
    void testGetImageDimensionsWithInvalidFile() {
        File invalidFile = tempDir.resolve("invalid.txt").toFile();
        try {
            invalidFile.createNewFile();
        } catch (IOException e) {
            fail("创建测试文件失败");
        }
        
        String dimensions = WatermarkEngine.getImageDimensions(invalidFile);
        assertEquals("未知", dimensions, "无效文件应返回'未知'");
    }
    
    @Test
    void testAddWatermarkWithDifferentPositions() {
        WatermarkPosition[] positions = WatermarkPosition.values();
        
        for (WatermarkPosition position : positions) {
            config.setPosition(position);
            File positionOutputFile = tempDir.resolve("output_" + position.name() + ".jpg").toFile();
            
            boolean result = WatermarkEngine.addWatermark(testImageFile, positionOutputFile, "Test", config);
            assertTrue(result, "位置 " + position + " 应该成功");
            assertTrue(positionOutputFile.exists(), "输出文件应该存在");
        }
    }
}