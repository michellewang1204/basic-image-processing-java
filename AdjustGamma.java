import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class AdjustGamma {
  public static double gammaSmall = 0.5;
  public static double gammaBig = 1.8;

  public static void main(String[] args) {
    try {
      // 讀取原始彩色照片
      BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
      int width = originalImage.getWidth();
      int height = originalImage.getHeight();

      // 調整 gamma 值<1
      BufferedImage gammaSmallImage = adjustGamma(originalImage, gammaSmall, width, height);
      GrayScale.saveImage(gammaSmallImage, "gammaSmall.jpg");

      // 調整 gamma 值>1
      BufferedImage gammaBigImage = adjustGamma(originalImage, gammaBig, width, height);
      GrayScale.saveImage(gammaBigImage, "gammaBig.jpg");

      // 拉開對比度 (gamma=1)
      BufferedImage highContrastImage = adjustGamma(originalImage, 1, width, height);
      GrayScale.saveImage(highContrastImage, "highContrast.jpg");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 調整 gamma 值
  public static BufferedImage adjustGamma(BufferedImage image, double gamma, int width, int height) {
    BufferedImage adjustedImage = new BufferedImage(
        width, height, BufferedImage.TYPE_BYTE_GRAY);
    image = GrayScale.convertToGrayscale(image, width, height);
    int max = -1;
    int min = 256;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = image.getRGB(i, j) & 0xFF;
        if (rgb > max) {
          max = rgb;
        }
        if (rgb < min) {
          min = rgb;
        }
      }
    }
    int gap = max - min;

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int grayValue = image.getRGB(i, j) & 0xFF;

        int adjustedValue = (int) (255 * Math.pow((double) (grayValue - min) / gap, gamma));

        int adjustedRgb = GrayScale.colorToRGB(255, adjustedValue, adjustedValue, adjustedValue);
        adjustedImage.setRGB(i, j, adjustedRgb);
      }
    }

    return adjustedImage;
  }

}