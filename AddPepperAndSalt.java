import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class AddPepperAndSalt {
  public static double ratio=0.02;
  public static void main(String[] args) {
      try {
          // 讀取原始彩色照片
          BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
          int width = originalImage.getWidth();
          int height = originalImage.getHeight();

          BufferedImage saltAndPepperImage = addPepperAndSaltNoise(originalImage, ratio,width,height);
          GrayScale.saveImage(saltAndPepperImage, "saltAndPepper.jpg");

      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  // 胡椒鹽雜訊
  public static BufferedImage addPepperAndSaltNoise(BufferedImage image, double noiseRatio,int width, int height) {
    BufferedImage pepperAndSaltImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    
    image= AdjustGamma.adjustGamma(image, AdjustGamma.gammaSmall,width,height);
    Random random = new Random();

    for (int y = 0; y < height; y++) {
        for (int x = 0; x< width; x++) {
            int pixel = image.getRGB(x, y);

            if (random.nextDouble() < noiseRatio) {
              int noisyPixel = random.nextBoolean() ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
              pepperAndSaltImage.setRGB(x, y, noisyPixel);
            } else {
                pepperAndSaltImage.setRGB(x, y, pixel);
            }
        }
    }

      return pepperAndSaltImage;
  }
}
