package com.example.picture.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageHashUtil {

    private static final int PHASH_SIZE = 32;
    private static final int PHASH_SMALL_SIZE = 8;

    public static String computeMD5(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return computeMD5(fis);
        }
    }

    public static String computeMD5(InputStream inputStream) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static String computePerceptualHash(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            throw new IOException("Cannot read image file: " + file.getName());
        }
        return computePerceptualHash(img);
    }

    public static String computePerceptualHash(BufferedImage img) {
        BufferedImage resized = resizeImage(img, PHASH_SIZE, PHASH_SIZE);
        int[][] grayscale = toGrayscale(resized);
        double[][] dct = applyDCT(grayscale);
        double[][] dctLow = new double[PHASH_SMALL_SIZE][PHASH_SMALL_SIZE];
        for (int i = 0; i < PHASH_SMALL_SIZE; i++) {
            for (int j = 0; j < PHASH_SMALL_SIZE; j++) {
                dctLow[i][j] = dct[i][j];
            }
        }
        double avg = calculateAverageExcludeDC(dctLow);
        StringBuilder hash = new StringBuilder();
        for (int i = 0; i < PHASH_SMALL_SIZE; i++) {
            for (int j = 0; j < PHASH_SMALL_SIZE; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                hash.append(dctLow[i][j] > avg ? "1" : "0");
            }
        }
        return binaryToHex(hash.toString());
    }

    public static int getImageWidth(BufferedImage img) {
        return img.getWidth();
    }

    public static int getImageHeight(BufferedImage img) {
        return img.getHeight();
    }

    public static int[] getImageDimensions(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            return new int[]{0, 0};
        }
        return new int[]{img.getWidth(), img.getHeight()};
    }

    public static int hammingDistance(String hash1, String hash2) {
        if (hash1 == null || hash2 == null) {
            return Integer.MAX_VALUE;
        }
        if (hash1.length() != hash2.length()) {
            return Integer.MAX_VALUE;
        }
        String b1 = hexToBinary(hash1);
        String b2 = hexToBinary(hash2);
        int distance = 0;
        for (int i = 0; i < b1.length(); i++) {
            if (b1.charAt(i) != b2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    public static double calculateSimilarity(String hash1, String hash2) {
        int totalBits = PHASH_SMALL_SIZE * PHASH_SMALL_SIZE - 1;
        int distance = hammingDistance(hash1, hash2);
        if (distance == Integer.MAX_VALUE) {
            return 0.0;
        }
        return 1.0 - ((double) distance / totalBits);
    }

    public static String getSimilarityLevel(double similarity) {
        if (similarity >= 0.9) {
            return "HIGH";
        } else if (similarity >= 0.75) {
            return "MEDIUM";
        } else if (similarity >= 0.6) {
            return "LOW";
        }
        return "NONE";
    }

    public static double getThresholdForLevel(String level) {
        if ("HIGH".equalsIgnoreCase(level)) {
            return 0.9;
        } else if ("MEDIUM".equalsIgnoreCase(level)) {
            return 0.75;
        } else if ("LOW".equalsIgnoreCase(level)) {
            return 0.6;
        }
        return 0.9;
    }

    private static BufferedImage resizeImage(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resized;
    }

    private static int[][] toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                result[y][x] = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            }
        }
        return result;
    }

    private static double[][] applyDCT(int[][] pixels) {
        int n = pixels.length;
        double[][] result = new double[n][n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                double sum = 0.0;
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        sum += pixels[x][y]
                                * Math.cos((2 * x + 1) * u * Math.PI / (2.0 * n))
                                * Math.cos((2 * y + 1) * v * Math.PI / (2.0 * n));
                    }
                }
                double cu = u == 0 ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n);
                double cv = v == 0 ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n);
                result[u][v] = cu * cv * sum;
            }
        }
        return result;
    }

    private static double calculateAverageExcludeDC(double[][] dctLow) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < dctLow.length; i++) {
            for (int j = 0; j < dctLow[i].length; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                sum += dctLow[i][j];
                count++;
            }
        }
        return sum / count;
    }

    private static String binaryToHex(String binary) {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 4) {
            String segment = binary.substring(i, Math.min(i + 4, binary.length()));
            while (segment.length() < 4) {
                segment += "0";
            }
            hex.append(Integer.toHexString(Integer.parseInt(segment, 2)));
        }
        return hex.toString();
    }

    private static String hexToBinary(String hex) {
        StringBuilder binary = new StringBuilder();
        for (char c : hex.toCharArray()) {
            String bin = Integer.toBinaryString(Integer.parseInt(String.valueOf(c), 16));
            while (bin.length() < 4) {
                bin = "0" + bin;
            }
            binary.append(bin);
        }
        return binary.toString();
    }
}
