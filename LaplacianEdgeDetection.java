import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class LaplacianEdgeDetection {
    public static void main(String[] args) {
        try {
            // 讀取原始彩色照片
            BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            BufferedImage laplacianImage = applyLaplacianFilter(originalImage, width, height);
            GrayScale.saveImage(laplacianImage, "laplacian.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Laplacian 邊緣偵測
    public static BufferedImage applyLaplacianFilter(BufferedImage image, int width, int height) {
        BufferedImage laplacianImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image = AdjustGamma.adjustGamma(image, 1, width, height);
        int sum = 0;
        for (int y = 1; y < height - 1; y++)
            for (int x = 1; x < width - 1; x++) {
                sum = (-1 * (image.getRGB(x - 1, y - 1) & 0xff)) + (-1 * (image.getRGB(x, y - 1) & 0xff))
                        + (-1 * (image.getRGB(x + 1, y - 1) & 0xff)) + (-1 * (image.getRGB(x - 1, y) & 0xff))
                        + (8 * (image.getRGB(x, y) & 0xff)) + (-1 * (image.getRGB(x + 1, y) & 0xff))
                        + (-1 * (image.getRGB(x - 1, y + 1) & 0xff)) + (-1 * (image.getRGB(x, y + 1) & 0xff))
                        + (-1 * (image.getRGB(x + 1, y + 1) & 0xff));
                int laplacianRgb = GrayScale.colorToRGB(255, sum, sum, sum);
                laplacianImage.setRGB(x, y, laplacianRgb);
            }

        return laplacianImage;
    }
}
