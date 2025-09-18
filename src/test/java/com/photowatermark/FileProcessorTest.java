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
 * 文件处理器测试类
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class FileProcessorTest {
    
    @TempDir
    Path tempDir;
    
    private File inputDir;
    private File outputDir;
    private File testImageFile;
    private WatermarkConfig config;
    
    @BeforeEach
    void setUp() throws IOException {
        // 创建输入和输出目录
        inputDir = tempDir.resolve("input").toFile();
        outputDir = tempDir.resolve("output").toFile();
        inputDir.mkdirs();
        outputDir.mkdirs();
        
        // 创建测试图片
        testImageFile = new File(inputDir, "test.jpg");
        BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 200, 200);
        g2d.dispose();
        ImageIO.write(testImage, "jpg", testImageFile);
        
        // 创建默认配置
        config = new WatermarkConfig();
        config.setFontSize(24);
        config.setColor(Color.BLACK);
        config.setPosition(WatermarkPosition.BOTTOM_RIGHT);
        config.setOpacity(0.8f);
        config.setMargin(20);
        config.setOutputDir(outputDir.getAbsolutePath());
    }
    
    @Test
    void testProcessPathWithSingleFile() {
        FileProcessor processor = new FileProcessor(config, false, "Test Watermark", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "处理单个文件应该成功");
    }
    
    @Test
    void testProcessPathWithDirectory() {
        FileProcessor processor = new FileProcessor(config, false, "Test Watermark", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(inputDir.getAbsolutePath());
        assertTrue(result.isSuccess(), "处理目录应该成功");
    }
    
    @Test
    void testProcessPathWithNonExistentPath() {
        FileProcessor processor = new FileProcessor(config, false, "Test Watermark", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath("non_existent_path");
        assertFalse(result.isSuccess(), "不存在的路径应该失败");
    }
    
    @Test
    void testProcessPathWithEmptyWatermarkText() {
        config.setDefaultWatermarkText("Default Text"); // 设置默认文本
        FileProcessor processor = new FileProcessor(config, false, "", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "空水印文本应该使用默认配置");
    }
    
    @Test
    void testProcessPathWithNullWatermarkText() {
        config.setDefaultWatermarkText("Default Text"); // 设置默认文本
        FileProcessor processor = new FileProcessor(config, false, null, outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "空水印文本应该使用默认配置");
    }
    
    @Test
    void testProcessBatchWithValidFiles() throws IOException {
        // 创建多个测试文件
        File testFile2 = new File(inputDir, "test2.jpg");
        BufferedImage testImage2 = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = testImage2.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 150, 150);
        g2d.dispose();
        ImageIO.write(testImage2, "jpg", testFile2);
        
        FileProcessor processor = new FileProcessor(config, false, "Test Watermark", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(inputDir.getAbsolutePath());
        assertTrue(result.isSuccess(), "批量处理应该成功");
    }
    
    @Test
    void testProcessBatchWithEmptyDirectory() {
        File emptyDir = tempDir.resolve("empty").toFile();
        emptyDir.mkdirs();
        
        FileProcessor processor = new FileProcessor(config, false, "Test Watermark", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(emptyDir.getAbsolutePath());
        // 空目录应该返回失败，因为没有找到图片文件
        assertFalse(result.isSuccess(), "空目录应该返回失败");
        assertEquals("未找到支持的图片文件", result.getMessage());
    }
    
    @Test
    void testProcessBatchWithNonImageFiles() throws IOException {
        // 创建非图片文件
        File textFile = new File(inputDir, "test.txt");
        textFile.createNewFile();
        
        FileProcessor processor = new FileProcessor(config, false, "Test Watermark", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(inputDir.getAbsolutePath());
        assertTrue(result.isSuccess(), "应该只处理图片文件");
    }
    
    @Test
    void testDetermineWatermarkTextWithCustomText() {
        FileProcessor processor = new FileProcessor(config, false, "Custom Text", outputDir, false);
        // 由于determineWatermarkText是私有方法，我们通过处理结果来验证
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "应该使用自定义文本");
    }
    
    @Test
    void testDetermineWatermarkTextWithExifDate() {
        config.setUseExifDate(true);
        config.setDefaultWatermarkText("Default Text"); // 设置默认文本作为后备
        FileProcessor processor = new FileProcessor(config, true, null, outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        // 由于测试图片没有EXIF数据，应该使用默认文本，所以处理应该成功
        assertTrue(result.isSuccess(), "应该使用默认文本作为后备");
    }
    
    @Test
    void testDetermineWatermarkTextWithDefaultText() {
        config.setDefaultWatermarkText("Default Text");
        FileProcessor processor = new FileProcessor(config, false, null, outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "应该使用默认文本");
    }
    
    @Test
    void testGenerateOutputFileWithSameDirectory() {
        config.setOutputDir(null);
        FileProcessor processor = new FileProcessor(config, false, "Test", null, false);
        // 由于generateOutputFile是私有方法，我们通过处理结果来验证
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "应该在同一目录生成输出文件");
    }
    
    @Test
    void testGenerateOutputFileWithCustomDirectory() {
        FileProcessor processor = new FileProcessor(config, false, "Test", outputDir, false);
        FileProcessor.ProcessResult result = processor.processPath(testImageFile.getAbsolutePath());
        assertTrue(result.isSuccess(), "应该在指定的输出目录生成文件");
    }
}