package com.photowatermark;

import java.awt.Color;

/**
 * 水印配置类
 * 
 * @author PhotoWatermark Team
 * @version 1.0.0
 */
public class WatermarkConfig {
    private int fontSize = 24;
    private String fontColor = "white";
    private String fontName = "SansSerif";
    private int fontStyle = java.awt.Font.PLAIN;
    private Color color = Color.WHITE;
    private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;
    private int alpha = 80;
    private float opacity = 0.8f;
    private int margin = 20;
    private boolean enableShadow = true;
    private Color shadowColor = Color.BLACK;
    private int shadowOffset = 2;
    private boolean verbose = false;
    private String outputDir;
    private boolean recursive = false;
    private String defaultWatermarkText = "";
    private boolean useExifDate = true;
    
    // Getters and Setters
    public int getFontSize() {
        return fontSize;
    }
    
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public String getFontColor() {
        return fontColor;
    }
    
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
    
    public WatermarkPosition getPosition() {
        return position;
    }
    
    public void setPosition(WatermarkPosition position) {
        this.position = position;
    }
    
    public int getAlpha() {
        return alpha;
    }
    
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }
    
    public boolean isVerbose() {
        return verbose;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
    
    public int getMargin() {
        return margin;
    }
    
    public void setMargin(int margin) {
        this.margin = margin;
    }
    
    public boolean isEnableShadow() {
        return enableShadow;
    }
    
    public void setEnableShadow(boolean enableShadow) {
        this.enableShadow = enableShadow;
    }
    
    public Color getShadowColor() {
        return shadowColor;
    }
    
    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }
    
    public int getShadowOffset() {
        return shadowOffset;
    }
    
    public void setShadowOffset(int shadowOffset) {
        this.shadowOffset = shadowOffset;
    }
    
    public String getOutputDir() {
        return outputDir;
    }
    
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
    
    public boolean isRecursive() {
        return recursive;
    }
    
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
    
    public String getDefaultWatermarkText() {
        return defaultWatermarkText;
    }
    
    public void setDefaultWatermarkText(String defaultWatermarkText) {
        this.defaultWatermarkText = defaultWatermarkText;
    }
    
    public boolean isUseExifDate() {
        return useExifDate;
    }
    
    public void setUseExifDate(boolean useExifDate) {
        this.useExifDate = useExifDate;
    }
    
    public String getFontName() {
        return fontName;
    }
    
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
    
    public int getFontStyle() {
        return fontStyle;
    }
    
    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
    
    @Override
    public String toString() {
        return "WatermarkConfig{" +
                "fontSize=" + fontSize +
                ", fontColor='" + fontColor + '\'' +
                ", position=" + position +
                ", alpha=" + alpha +
                ", verbose=" + verbose +
                '}';
    }
}