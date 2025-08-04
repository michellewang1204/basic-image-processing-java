import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class MaxFilter {
    public static void main(String[] args) {
        try {
            // 讀取原始彩色照片
            BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            BufferedImage maxValueImage = applyMaxValueFilter(originalImage, width, height);
            GrayScale.saveImage(maxValueImage, "maxValue.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 最大值濾波器
    public static BufferedImage applyMaxValueFilter(BufferedImage image, int width, int height) {

        BufferedImage maxValueImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image = LaplacianEdgeDetection.applyLaplacianFilter(image, width, height);
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int maxValue = getMaxValue(image, x, y);
                int maxRgb = GrayScale.colorToRGB(255, maxValue, maxValue, maxValue);
                maxValueImage.setRGB(x, y, maxRgb);
            }
        }

        return maxValueImage;
    }

    private static int getMaxValue(BufferedImage image, int x, int y) {
        int maxValue = 0;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int pixelValue = new Color(image.getRGB(x + dx, y + dy)).getRed();
                maxValue = Math.max(maxValue, pixelValue);
            }
        }

        return maxValue;
    }
}
