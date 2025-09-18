package com.photowatermark;

/**
 * 水印位置枚举
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public enum WatermarkPosition {
    TOP_LEFT("左上角"),
    TOP_RIGHT("右上角"),
    CENTER("居中"),
    BOTTOM_LEFT("左下角"),
    BOTTOM_RIGHT("右下角");
    
    private final String description;
    
    WatermarkPosition(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}