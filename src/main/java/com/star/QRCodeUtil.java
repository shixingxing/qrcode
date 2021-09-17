package com.star;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;

public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    private static final int HEIGHT = 60;
    private static final int QRCODE_SIZE = 300;
    private static final int WIDTH = 60;

    private static BufferedImage createImage(String content, String imgPath, boolean needCompress) throws Exception {
        int i;
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, (int) QRCODE_SIZE, (int) QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, 1);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    i = -16777216;
                } else {
                    i = -1;
                }
                image.setRGB(x, y, i);
            }
        }
        if (imgPath != null && !"".equals(imgPath)) {
            insertImage(image, imgPath, needCompress);
        }
        return image;
    }

    private static void insertImage(BufferedImage source, String imgPath, boolean needCompress) throws Exception {
        if (new File(imgPath).exists()) {
            Image src = ImageIO.read(new File(imgPath));
            int width = src.getWidth((ImageObserver) null);
            int height = src.getHeight((ImageObserver) null);
            if (needCompress) {
                if (width > 60) {
                    width = 60;
                }
                if (height > 60) {
                    height = 60;
                }
                Image image = src.getScaledInstance(width, height, 4);
                Graphics g = new BufferedImage(width, height, 1).getGraphics();
                g.drawImage(image, 0, 0, (ImageObserver) null);
                g.dispose();
                src = image;
            }
            Graphics2D graph = source.createGraphics();
            int x = (300 - width) / 2;
            int y = (300 - height) / 2;
            graph.drawImage(src, x, y, width, height, (ImageObserver) null);
            Shape shape = new RoundRectangle2D.Float((float) x, (float) y, (float) width, (float) width, 6.0f, 6.0f);
            graph.setStroke(new BasicStroke(3.0f));
            graph.draw(shape);
            graph.dispose();
        }
    }

    public static void encode(String content, String imgPath, String destPath, boolean needCompress) throws Exception {
        String file;
        BufferedImage image = createImage(content, imgPath, needCompress);
        mkdirs(destPath);
        if (imgPath == "") {
            file = String.valueOf(new Random().nextInt(99999999)) + ".jpg";
        } else {
            file = imgPath;
        }
        ImageIO.write(image, FORMAT_NAME, new File(String.valueOf(destPath) + "/" + file));
    }

    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    public static void encode(String content, String imgPath, String destPath) throws Exception {
        encode(content, imgPath, destPath, false);
    }

    public static void encode(String content, String destPath, boolean needCompress) throws Exception {
        encode(content, (String) null, destPath, needCompress);
    }

    public static void encode(String content, String destPath) throws Exception {
        encode(content, (String) null, destPath, false);
    }

    public static void encode(String content, String imgPath, OutputStream output, boolean needCompress) throws Exception {
        ImageIO.write(createImage(content, imgPath, needCompress), FORMAT_NAME, output);
    }

    public static void encode(String content, OutputStream output) throws Exception {
        encode(content, (String) null, output, false);
    }

    public static String decode(File file) throws Exception {
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        return new MultiFormatReader().decode(bitmap, hints).getText();
    }

    public static String decode(String path) throws Exception {
        return decode(new File(path));
    }

}
