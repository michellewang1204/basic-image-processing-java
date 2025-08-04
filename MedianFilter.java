import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class MedianFilter {
  public static void main(String[] args) {
      try {
          // 讀取原始彩色照片
          BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
          int width = originalImage.getWidth();
          int height = originalImage.getHeight();

          BufferedImage medianDenoisedImage = applyMedianFilter(originalImage,width,height);
          GrayScale.saveImage(medianDenoisedImage, "medianDenoised.jpg");
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  //中值濾波器
  private static BufferedImage applyMedianFilter(BufferedImage image,int width, int height) {
    BufferedImage medianDenoisedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    image=AddPepperAndSalt.addPepperAndSaltNoise(image,AddPepperAndSalt.ratio,width,height);
      for (int y = 1; y < height - 1; y++) {
          for (int x = 1; x < width - 1; x++) {
              // 取得3x3區域的像素值
              int[] values = new int[9];

              // 提取 3x3 區域的像素值
              for (int i = -1; i <= 1; i++) {
                  for (int j = -1; j <= 1; j++) {
                      values[(i + 1) * 3 + (j + 1)] = new Color(image.getRGB(x + i, y + j)).getRed();
                  }
              }

              // 對像素值進行排序
              java.util.Arrays.sort(values);

              // 選取排序後的中間值
              int medianValue = values[4];
              int medianRgb = GrayScale.colorToRGB(255, medianValue, medianValue, medianValue);

              medianDenoisedImage.setRGB(x, y, medianRgb);
          }
      }

      return medianDenoisedImage;
  }

}
