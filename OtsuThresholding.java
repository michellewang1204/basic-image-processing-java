import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class OtsuThresholding {
    public static void main(String[] args) {
        try {
            // 讀取原始彩色照片
            BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            BufferedImage otsuImage = otsuThresholding(originalImage, width, height);
            GrayScale.saveImage(otsuImage, "otsu.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Otsu二值化
    private static BufferedImage otsuThresholding(BufferedImage image, int width, int height) {
        image = AdjustGamma.adjustGamma(image, AdjustGamma.gammaBig, width, height);
        // 計算直方圖
        int[] histogram = calculateHistogram(image);

        // 計算總像素數
        int totalPixels = width * height;

        // 計算Otsu閾值
        int otsuThreshold = calculateOtsuThreshold(histogram, totalPixels);

        // 根據Otsu閾值進行二值化
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y) & 0xFF;
                int binaryPixel = (pixel > otsuThreshold) ? 255 : 0;
                int otsuRGB = GrayScale.colorToRGB(255, binaryPixel, binaryPixel, binaryPixel);
                binaryImage.setRGB(x, y, otsuRGB);
            }
        }

        return binaryImage;
    }

    // 計算直方圖
    private static int[] calculateHistogram(BufferedImage image) {
        int[] histogram = new int[256]; // 256個灰度級別

        int width = image.getWidth();
        int height = image.getHeight();

        // 遍歷影像的每個像素，增加相應灰度級別的直方圖計數
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y) & 0xFF; // 獲取灰度值
                histogram[pixel]++;
            }
        }

        return histogram;
    }

    // 計算Otsu閾值
    private static int calculateOtsuThreshold(int[] histogram, int totalPixels) {
        // 所有灰階值的加權和
        double sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        double sumB = 0;
        // i之前的像素累積比例
        int weightB = 0;
        int weightF;
        // 組間最大變異數
        double maxVariance = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            weightB += histogram[i];
            if (weightB == 0) {
                continue;
            }

            weightF = totalPixels - weightB;
            if (weightF == 0) {
                break;
            }

            sumB += i * histogram[i];
            // 灰度平均值
            double meanB = sumB / weightB;
            double meanF = (sum - sumB) / weightF;

            double varianceBetween = weightB*weightF * Math.pow((meanB - meanF), 2);

            if (varianceBetween >= maxVariance) {
                threshold = i;
                maxVariance = varianceBetween;
                
            }
        }

        return threshold;
    }
}
