package filters;

import model.Image;

import java.util.concurrent.TimeUnit;


public interface Geometric {
    int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    long TIMEOUT = 600;
    TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    Image translation(Image image, int delX, int delY);

    Image center(Image image, int newW, int newH);

    Image crop(Image image, int newW, int newH);

    Image scaling(Image image, double scaleW, double scaleH);

    Image shearing(Image image, double angleX, double angleY);

    Image shearingByK(Image image, double a, double b);

    Image rotation90(Image image);

    Image rotation180(Image image);

    Image rotation270(Image image);

    Image rotation(Image image, double angle);
}
