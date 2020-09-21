package com.the.udemy.concurrency.performance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Latency {
    private static final String SOURCE_FILE = "many-flowers.jpg";
    private static final String SINGLE_THREAD_DES_FILE = "out/single-thread-many-flowers.jpg";
    private static final String MULTI_THREAD_DES_FILE = "out/multi-thread-many-flowers.jpg";

    public static void main(String args[]) throws IOException, InterruptedException {
        BufferedImage originalImg = ImageIO.read(Latency.class.getClassLoader().getResource(SOURCE_FILE));
        BufferedImage resultImg = new BufferedImage(originalImg.getWidth()
                , originalImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        reColorAndMetricSingleThread(originalImg, resultImg);
        File output = new File(SINGLE_THREAD_DES_FILE);
        output.mkdirs();
        ImageIO.write(resultImg, "jpg", output);

        System.out.println("\n\n");

        reColorAndMetricMultiThread(originalImg, resultImg, 6);
        output = new File(MULTI_THREAD_DES_FILE);
        output.mkdirs();
        ImageIO.write(resultImg, "jpg", output);

    }

    private static void reColorAndMetricSingleThread(BufferedImage originalImg, BufferedImage resultImg) {
        System.out.println("============================================================================================");
        System.out.println("                             Re-color image using single thread");
        System.out.println("============================================================================================");
        System.out.println();
        long start = System.currentTimeMillis();
        reColorSingleThreaded(originalImg, resultImg);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Recolor duration is %d mil", end - start));
    }

    private static void reColorAndMetricMultiThread(BufferedImage originalImg, BufferedImage resultImg, int threadNumber)
            throws InterruptedException {
        System.out.println("============================================================================================");
        System.out.println(String.format("                             Re-color image using %d threads", threadNumber));
        System.out.println("============================================================================================");
        System.out.println();
        long start = System.currentTimeMillis();
        reColorMultiThreaded(originalImg, resultImg, threadNumber);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Recolor duration is %d mil", end - start));
    }

    public static void reColorSingleThreaded(BufferedImage originalImg, BufferedImage resultImg) {
        reColorImg(originalImg, resultImg, 0, 0, originalImg.getWidth(), originalImg.getHeight());
    }

    public static void reColorMultiThreaded(BufferedImage originalImg, BufferedImage resultImg, int threadNumber) throws InterruptedException {
        int width = originalImg.getWidth();
        int height = originalImg.getHeight() / threadNumber;

        List<Thread> threads = new ArrayList<>(threadNumber);
        for (int i = 0;  i < threadNumber; i ++) {
            final int theadMultiplier = i;
            threads.add(new Thread(() -> {
                int topCorner = height * theadMultiplier;
                reColorImg(originalImg, resultImg, 0, topCorner, width, height);
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    public static void reColorImg(BufferedImage originalImg, BufferedImage resultImg, int leftCorner, int topCorner,
                                  int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImg.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < resultImg.getHeight(); y++) {
                reColorPixel(originalImg, resultImg, x, y);
            }
        }
    }

    public static void reColorPixel(BufferedImage originalImg, BufferedImage resultImg, int x, int y) {
        int rgb = originalImg.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed, newGreen, newBlue;
        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRgb = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImg, x, y, newRgb);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    private static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    private static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    private static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}
