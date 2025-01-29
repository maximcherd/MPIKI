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
        Geometric geometric = new GeometricParGPU();
        Image image = ImageRW.loadImages("1").getLast();
        BufferedImage bImage = image.getBufferedImage();
        ImageRW.saveBImage(bImage, "before");
        Image newImage = geometric.rotation(image, 30);
//        Image newImage = geometric.shearingByK(image, -BaseMath.tgTailor(BaseMath.angle2Radians(15)), 0);
//        newImage = geometric.shearingByK(newImage, 0, BaseMath.sinTailor(BaseMath.angle2Radians(30)));
//        newImage = geometric.shearingByK(newImage, -BaseMath.tgTailor(BaseMath.angle2Radians(15)), 0);
        BufferedImage newBImage = newImage.getBufferedImage();
        ImageRW.saveBImage(newBImage, "after");
    }
}
