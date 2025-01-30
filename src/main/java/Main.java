import filters.Geometric;
import filters.GeometricParCPU;
import filters.GeometricParGPU;
import filters.GeometricSeq;
import helpers.BaseMath;
import helpers.ImageRW;
import model.Image;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        long a = 9353;
        long b = 4635;
        double c = (double) a / b / 8 * 100;
        System.out.println(String.format("%05.2f", c));
//        Geometric geometric = new GeometricParGPU();
//        Image image = ImageRW.loadImages("1").getLast();
//        BufferedImage bImage = image.getBufferedImage();
//        ImageRW.saveBImage(bImage, "before");
//        Image newImage = geometric.rotation(image, 30);
////        Image newImage = geometric.shearingByK(image, -BaseMath.tgTailor(BaseMath.angle2Radians(15)), 0);
////        newImage = geometric.shearingByK(newImage, 0, BaseMath.sinTailor(BaseMath.angle2Radians(30)));
////        newImage = geometric.shearingByK(newImage, -BaseMath.tgTailor(BaseMath.angle2Radians(15)), 0);
//        BufferedImage newBImage = newImage.getBufferedImage();
//        ImageRW.saveBImage(newBImage, "after");
    }
}
