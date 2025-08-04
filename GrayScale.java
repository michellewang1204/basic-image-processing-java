import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class GrayScale {
    public static void main(String[] args) {
        try {
            // 讀取原始彩色照片
            BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // 將彩色照片轉換為灰階影像
            BufferedImage grayscaleImage = convertToGrayscale(originalImage, width, height);
            saveImage(grayscaleImage, "grayscale.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int colorToRGB(int alpha, int red, int green, int blue) {
        int rgb = 0;
        rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
        return rgb;
    }

    // 灰階影像
    public static BufferedImage convertToGrayscale(BufferedImage image, int width, int height) {
        BufferedImage grayscaleImage = new BufferedImage(
                width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                int grayValue = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                int grayRgb = colorToRGB(255, grayValue, grayValue, grayValue);
                grayscaleImage.setRGB(i, j, grayRgb);
            }
        }

        return grayscaleImage;
    }

    public static void saveImage(BufferedImage image, String fileName) throws IOException {
        // 儲存處理後的圖片
        ImageIO.write(image, "jpg", new File(fileName));
    }
}
