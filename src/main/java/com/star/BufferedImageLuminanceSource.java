package com.star;

import com.google.zxing.LuminanceSource;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class BufferedImageLuminanceSource extends LuminanceSource {
    private final BufferedImage image;
    private final int left;
    private final int top;

    public BufferedImageLuminanceSource(BufferedImage image2) {
        this(image2, 0, 0, image2.getWidth(), image2.getHeight());
    }

    public BufferedImageLuminanceSource(BufferedImage image2, int left2, int top2, int width, int height) {
        super(width, height);
        int sourceWidth = image2.getWidth();
        int sourceHeight = image2.getHeight();
        if (left2 + width > sourceWidth || top2 + height > sourceHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
        for (int y = top2; y < top2 + height; y++) {
            for (int x = left2; x < left2 + width; x++) {
                if ((image2.getRGB(x, y) & -16777216) == 0) {
                    image2.setRGB(x, y, -1);
                }
            }
        }
        this.image = new BufferedImage(sourceWidth, sourceHeight, 10);
        this.image.getGraphics().drawImage(image2, 0, 0, (ImageObserver) null);
        this.left = left2;
        this.top = top2;
    }

    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        this.image.getRaster().getDataElements(this.left, this.top + y, width, 1, row);
        return row;
    }

    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();
        byte[] matrix = new byte[(width * height)];
        this.image.getRaster().getDataElements(this.left, this.top, width, height, matrix);
        return matrix;
    }

    public boolean isCropSupported() {
        return true;
    }

    public LuminanceSource crop(int left2, int top2, int width, int height) {
        return new BufferedImageLuminanceSource(this.image, this.left + left2, this.top + top2, width, height);
    }

    public boolean isRotateSupported() {
        return true;
    }

    public LuminanceSource rotateCounterClockwise() {
        int sourceWidth = this.image.getWidth();
        int sourceHeight = this.image.getHeight();
        AffineTransform transform = new AffineTransform(0.0d, -1.0d, 1.0d, 0.0d, 0.0d, (double) sourceWidth);
        BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, 10);
        Graphics2D g = rotatedImage.createGraphics();
        g.drawImage(this.image, transform, (ImageObserver) null);
        g.dispose();
        int width = getWidth();
        return new BufferedImageLuminanceSource(rotatedImage, this.top, sourceWidth - (this.left + width), getHeight(), width);
    }
}