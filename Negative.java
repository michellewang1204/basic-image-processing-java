import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class Negative {

    public static void main(String[] args) {
        try {
            // 讀取原始彩色照片
            BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // 調整為負片
            BufferedImage negativeImage = invertImageColors(originalImage, width, height);
            GrayScale.saveImage(negativeImage, "negative.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 調整為負片
    public static BufferedImage invertImageColors(BufferedImage image, int width, int height) {
        BufferedImage negativeImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image = GrayScale.convertToGrayscale(image, width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y) & 0xFF;
                int grayValue = 255 - rgb;

                int negativeRGB = GrayScale.colorToRGB(255, grayValue, grayValue, grayValue);
                negativeImage.setRGB(x, y, negativeRGB);
            }
        }
        return negativeImage;
    }
}