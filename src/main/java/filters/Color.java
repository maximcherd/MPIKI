package filters;

import model.Image;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public interface Color {
    int TYPE_GRAY = BufferedImage.TYPE_BYTE_GRAY;
    int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    long TIMEOUT = 600;
    TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    Image grayscale(Image image);

    Image redMask(Image image);

    Image contrast(Image image, double scale);

    Image brightness(Image image, double scale);
}
