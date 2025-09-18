package com.photowatermark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 批量文件处理器
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class FileProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    
    private final WatermarkConfig config;
    private final boolean useExifDate;
    private final String customWatermark;
    private final File outputDir;
    private final boolean recursive;
    
    // 处理统计
    private int totalFiles = 0;
    private int processedFiles = 0;
    private int successfulFiles = 0;
    private int failedFiles = 0;
    
    /**
     * 构造函数
     * 
     * @param config 水印配置
     * @param useExifDate 是否使用EXIF日期作为水印
     * @param customWatermark 自定义水印文本
     * @param outputDir 输出目录
     * @param recursive 是否递归处理子目录
     */
    public FileProcessor(WatermarkConfig config, boolean useExifDate, String customWatermark, 
                        File outputDir, boolean recursive) {
        this.config = config;
        this.useExifDate = useExifDate;
        this.customWatermark = customWatermark;
        this.outputDir = outputDir;
        this.recursive = recursive;
    }
    
    /**
     * 处理单个文件或目录
     * 
     * @param inputPath 输入路径
     * @return 处理结果
     */
    public ProcessResult processPath(String inputPath) {
        File inputFile = new File(inputPath);
        
        if (!inputFile.exists()) {
            logger.error("输入路径不存在: {}", inputPath);
            return new ProcessResult(false, "输入路径不存在");
        }
        
        List<File> imageFiles = new ArrayList<>();
        
        if (inputFile.isFile()) {
            if (WatermarkEngine.isSupportedImageFormat(inputFile)) {
                imageFiles.add(inputFile);
            } else {
                logger.warn("不支持的文件格式: {}", inputFile.getName());
                return new ProcessResult(false, "不支持的文件格式");
            }
        } else if (inputFile.isDirectory()) {
            collectImageFiles(inputFile, imageFiles, recursive);
        }
        
        if (imageFiles.isEmpty()) {
            logger.warn("未找到支持的图片文件");
            return new ProcessResult(false, "未找到支持的图片文件");
        }
        
        totalFiles = imageFiles.size();
        logger.info("找到 {} 个图片文件待处理", totalFiles);
        
        // 确保输出目录存在（如果指定了输出目录）
        if (outputDir != null && !outputDir.exists() && !outputDir.mkdirs()) {
            logger.error("无法创建输出目录: {}", outputDir.getAbsolutePath());
            return new ProcessResult(false, "无法创建输出目录");
        }
        
        // 批量处理文件
        return processBatch(imageFiles);
    }
    
    /**
     * 批量处理图片文件
     * 
     * @param imageFiles 图片文件列表
     * @return 处理结果
     */
    private ProcessResult processBatch(List<File> imageFiles) {
        long startTime = System.currentTimeMillis();
        
        // 使用线程池进行并行处理
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), imageFiles.size());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        List<Future<Boolean>> futures = new ArrayList<>();
        
        try {
            for (File imageFile : imageFiles) {
                Future<Boolean> future = executor.submit(() -> processImageFile(imageFile));
                futures.add(future);
            }
            
            // 等待所有任务完成
            for (Future<Boolean> future : futures) {
                try {
                    boolean success = future.get();
                    processedFiles++;
                    if (success) {
                        successfulFiles++;
                    } else {
                        failedFiles++;
                    }
                } catch (Exception e) {
                    logger.error("处理任务时出错", e);
                    processedFiles++;
                    failedFiles++;
                }
            }
            
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String message = String.format("处理完成 - 总计: %d, 成功: %d, 失败: %d, 耗时: %d ms", 
                                     totalFiles, successfulFiles, failedFiles, duration);
        
        logger.info(message);
        
        return new ProcessResult(failedFiles == 0, message);
    }
    
    /**
     * 处理单个图片文件
     * 
     * @param imageFile 图片文件
     * @return 是否处理成功
     */
    private boolean processImageFile(File imageFile) {
        try {
            // 确定水印文本
            String watermarkText = determineWatermarkText(imageFile);
            
            if (watermarkText == null || watermarkText.trim().isEmpty()) {
                logger.warn("无法确定水印文本，跳过文件: {}", imageFile.getName());
                return false;
            }
            
            // 生成输出文件路径
            File outputFile = generateOutputFile(imageFile);
            
            // 添加水印
            boolean success = WatermarkEngine.addWatermark(imageFile, outputFile, watermarkText, config);
            
            if (success) {
                logger.debug("成功处理文件: {} -> {}", imageFile.getName(), outputFile.getName());
            } else {
                logger.error("处理文件失败: {}", imageFile.getName());
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("处理文件时发生异常: {}", imageFile.getName(), e);
            return false;
        }
    }
    
    /**
     * 确定水印文本
     * 
     * @param imageFile 图片文件
     * @return 水印文本
     */
    private String determineWatermarkText(File imageFile) {
        String result = null;
        
        if (useExifDate) {
            String exifDate = ExifReader.extractDateTaken(imageFile);
            if (exifDate != null && !exifDate.trim().isEmpty()) {
                result = exifDate;
            } else {
                logger.warn("文件 {} 无法提取EXIF日期，使用自定义水印", imageFile.getName());
                result = customWatermark;
            }
        } else {
            result = customWatermark;
        }
        
        // 如果结果为null或空，使用默认水印文本
        if (result == null || result.trim().isEmpty()) {
            result = config.getDefaultWatermarkText();
        }
        
        return result;
    }
    
    /**
     * 生成输出文件路径
     * 
     * @param inputFile 输入文件
     * @return 输出文件
     */
    private File generateOutputFile(File inputFile) {
        String fileName = inputFile.getName();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        
        // 添加水印后缀
        String outputFileName = baseName + "_watermarked" + extension;
        
        // 如果没有指定输出目录，使用输入文件的目录
        File targetDir = (outputDir != null) ? outputDir : inputFile.getParentFile();
        
        return new File(targetDir, outputFileName);
    }
    
    /**
     * 递归收集图片文件
     * 
     * @param directory 目录
     * @param imageFiles 图片文件列表
     * @param recursive 是否递归
     */
    private void collectImageFiles(File directory, List<File> imageFiles, boolean recursive) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isFile() && WatermarkEngine.isSupportedImageFormat(file)) {
                imageFiles.add(file);
            } else if (file.isDirectory() && recursive) {
                collectImageFiles(file, imageFiles, true);
            }
        }
    }
    
    /**
     * 获取处理统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStatistics() {
        return String.format("处理统计 - 总计: %d, 已处理: %d, 成功: %d, 失败: %d", 
                           totalFiles, processedFiles, successfulFiles, failedFiles);
    }
    
    /**
     * 处理结果类
     */
    public static class ProcessResult {
        private final boolean success;
        private final String message;
        
        public ProcessResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("ProcessResult{success=%s, message='%s'}", success, message);
        }
    }
}