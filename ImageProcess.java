import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class ImageProcess {
  public static void main(String[] args) {
      try {
          // 讀取原始彩色照片
          BufferedImage originalImage = ImageIO.read(new File("xmasdog.jpg"));
          int width = originalImage.getWidth();
          int height = originalImage.getHeight();

          // 將彩色照片轉換為灰階影像
          BufferedImage grayscaleImage = convertToGrayscale(originalImage,width,height);
          saveImage(grayscaleImage, "grayscale.jpg");

          //調整為負片
          BufferedImage negativeImage = invertImageColors(grayscaleImage,width,height);
          saveImage(negativeImage, "negative.jpg");

          // 調整 gamma 值<1 & 胡椒鹽雜訊 & 3*3中值濾波器
          double gammaSmall = 0.5;
          BufferedImage gammaSmallImage = adjustGamma(grayscaleImage, gammaSmall,width,height);
          saveImage(gammaSmallImage, "gammaSmall.jpg");

          BufferedImage saltAndPepperImage = addPepperAndSaltNoise(gammaSmallImage, 0.02,width,height);
          saveImage(saltAndPepperImage, "saltAndPepper.jpg");

          BufferedImage medianDenoisedImage = applyMedianFilter(saltAndPepperImage,width,height);
          saveImage(medianDenoisedImage, "medianDenoised.jpg");

          // 調整 gamma 值>1 & otsu二值化
          double gammaBig = 1.8;
          BufferedImage gammaBigImage = adjustGamma(grayscaleImage, gammaBig,width,height);
          saveImage(gammaBigImage, "gammaBig.jpg");

          BufferedImage otsuImage = otsuThresholding(gammaBigImage,width,height);
          saveImage(otsuImage, "otsu.jpg");

          //拉開對比度 & laplacian 邊緣偵測 & 3*3最大值濾波器
          BufferedImage highContrastImage = adjustGamma(grayscaleImage, 1,width,height);
          saveImage(highContrastImage, "highContrast.jpg");

          BufferedImage laplacianImage =applyLaplacianFilter(highContrastImage,width,height);
          saveImage(laplacianImage, "laplacian.jpg");

          BufferedImage maxValueImage =applyMaxValueFilter(laplacianImage,width,height);
          saveImage(maxValueImage, "maxValue.jpg");
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  private static int colorToRGB(int alpha,int red, int green, int blue){
    int rgb=0;
    rgb=(alpha << 24) | (red << 16) | (green << 8) | blue;
    return rgb;
  }
  
  // 灰階影像
  private static BufferedImage convertToGrayscale(BufferedImage image,int width, int height) {
      BufferedImage grayscaleImage = new BufferedImage(
        width, height, BufferedImage.TYPE_BYTE_GRAY);
      for (int i = 0; i < width; i++) {
          for (int j = 0; j <height; j++) {
              int rgb = image.getRGB(i, j);
              int r = (rgb >> 16) & 0xFF;
              int g = (rgb >> 8) & 0xFF;
              int b = rgb & 0xFF;

              int grayValue = (int)(0.299*r + 0.587*g + 0.114*b);

              int grayRgb =  colorToRGB(255, grayValue, grayValue, grayValue);
              grayscaleImage.setRGB(i, j, grayRgb);
          }
      }

      return grayscaleImage;
  }

  // 調整為負片
  private static BufferedImage invertImageColors(BufferedImage image,int width, int height) {
      BufferedImage negativeImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

      for (int y = 0; y <height; y++) {
          for (int x = 0; x < width; x++) {
              int rgb = image.getRGB(x, y)&0xFF;
              int grayValue=255-rgb;

              int negativeRGB = colorToRGB(255, grayValue, grayValue, grayValue);
              negativeImage.setRGB(x, y, negativeRGB);
          }
      }
      return negativeImage;
  }

  // 調整 gamma 值
  private static BufferedImage adjustGamma(BufferedImage image, double gamma,int width, int height) {
      BufferedImage adjustedImage = new BufferedImage(
        width, height, BufferedImage.TYPE_BYTE_GRAY);
      int max = -1;
      int min = 256;

      for(int i = 0; i < width; i++){ 
        for (int j = 0; j < height; j++) {
          int rgb=image.getRGB(i, j) & 0xFF;
          if(rgb > max){
              max = rgb;
          }
          if(rgb < min){
              min = rgb;
          }
        }
      }
      int gap=max-min;
    
      for (int i = 0; i < width; i++) {
          for (int j = 0; j < height; j++) {
              int grayValue = image.getRGB(i, j) & 0xFF;

              int adjustedValue = (int) (255 * Math.pow((double) (grayValue-min) / gap, gamma));

              int adjustedRgb = colorToRGB(255, adjustedValue, adjustedValue, adjustedValue);
              adjustedImage.setRGB(i, j, adjustedRgb);
          }
      }

      return adjustedImage;
  }

  // 胡椒鹽雜訊
  private static BufferedImage addPepperAndSaltNoise(BufferedImage image, double noiseRatio,int width, int height) {
    BufferedImage pepperAndSaltImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
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

  //中值濾波器
  private static BufferedImage applyMedianFilter(BufferedImage image,int width, int height) {
      BufferedImage medianDenoisedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

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
              int medianRgb = colorToRGB(255, medianValue, medianValue, medianValue);

              medianDenoisedImage.setRGB(x, y, medianRgb);
          }
      }

      return medianDenoisedImage;
  }

  // Laplacian 邊緣偵測
  private static BufferedImage applyLaplacianFilter(BufferedImage image,int width, int height) {
      BufferedImage laplacianImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    int sum=0;
    for(int y=1;y<height-1;y++)
    for(int x=1;x<width-1;x++) {
      sum =(-1*(image.getRGB(x-1, y-1)&0xff))+(-1*(image.getRGB(x, y-1)&0xff))+(-1*(image.getRGB(x+1, y-1)&0xff))+ (-1*(image.getRGB(x-1, y)&0xff)) + (8*(image.getRGB(x,y)&0xff)) + (-1*(image.getRGB(x+1, y)&0xff))+  (-1*(image.getRGB(x-1, y+1)&0xff))+  (-1*(image.getRGB(x, y+1)&0xff))+  (-1*(image.getRGB(x+1, y+1)&0xff));
      int laplacianRgb = colorToRGB(255, sum, sum,sum);
      laplacianImage.setRGB(x, y, laplacianRgb);
    }

      return laplacianImage;
  }

  // 最大值濾波器
  public static BufferedImage applyMaxValueFilter(BufferedImage image,int width, int height) {

      BufferedImage maxValueImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

      for (int y = 1; y < height - 1; y++) {
          for (int x = 1; x < width - 1; x++) {
              int maxValue = getMaxValue(image, x, y);
              int maxRgb = colorToRGB(255, maxValue , maxValue ,maxValue );
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

  // Otsu二值化
  private static BufferedImage otsuThresholding(BufferedImage image,int width, int height) {

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
              int otsuRGB = colorToRGB(255, binaryPixel, binaryPixel,binaryPixel);
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
    //所有灰階值的加權和
    double sum = 0;
    for (int i = 0; i < 256; i++) {
        sum += i * histogram[i];
    }

    double sumB = 0;
    //i之前的像素累積總數
    int weightB = 0;
    int weightF;
    //組間最大變異數
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
        //灰度平均值
        double meanB = sumB / weightB;
        double meanF = (sum - sumB) / weightF;

        double varianceBetween =  weightB * weightF * Math.pow((meanB - meanF), 2);

        if (varianceBetween > maxVariance) {
            maxVariance = varianceBetween;
            threshold = i;
        }
    }

    return threshold;
  }
  
  private static void saveImage(BufferedImage image, String fileName) throws IOException {
      // 儲存處理後的圖片
      ImageIO.write(image, "jpg", new File(fileName));
  }
}
