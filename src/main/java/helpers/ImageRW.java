package helpers;

import filters.Geometric;
import filters.GeometricParGPU;
import filters.GeometricSeq;
import model.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class ImageRW {
    static final String[] IMAGE_FORMATS = new String[]{
            "png", "jpg"
    };
    private static final String DOT = ".";
    private static final String IMAGE_FORMAT_DEFAULT = "png";
    private static final String GIF_FORMAT = "gif";
    private static final String DIR_DATA_PATH = "src/main/resources".replace("/", File.separator);
    private static final String DIR_INPUT_PATH = "input";
    private static final String DIR_OUTPUT_PATH = "output";
    private static final int IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    private static final Geometric geometric = new GeometricParGPU();

    public static String joinPath(String... args) {
        List<String> values = new ArrayList<>(List.of(args));
        return String.join(
                File.separator,
                values
        );
    }

    private static String checkFormat(String path, String format) {
        if (!path.contains(DOT)) {
            path = String.join(DOT, path, format);
        }
        return path;
    }

    private static BufferedImage loadBImage(File image) {
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            System.out.println("WARNING: cannot load BufferedImage from " + image.getPath());
            return new BufferedImage(100, 100, IMAGE_TYPE);
        }
    }

    public static BufferedImage loadBImage(String path) {
        String fullPath = joinPath(DIR_DATA_PATH, DIR_INPUT_PATH, checkFormat(path, IMAGE_FORMAT_DEFAULT));
        return loadBImage(new File(fullPath));
    }

    public static void saveBImage(BufferedImage image, String path) {
        String fullPath = joinPath(DIR_DATA_PATH, DIR_OUTPUT_PATH, checkFormat(path, IMAGE_FORMAT_DEFAULT));
        try {
            ImageIO.write(image, IMAGE_FORMAT_DEFAULT, new File(fullPath));
        } catch (IOException e) {
            System.out.println("WARNING: cannot save BufferedImage to " + fullPath);
        }
    }

    public static void saveImages(List<Image> images, String path, String taskName) {
        System.out.println("INFO: Saving images started...");
        for (Image image : images) {
            BufferedImage result = image.getBufferedImage();
            File dir = new File(joinPath(DIR_DATA_PATH, DIR_OUTPUT_PATH, path));
            if (!dir.isDirectory() && !dir.mkdir()) {
                System.out.println("WARNING: cannot create dir " + dir.getPath());
            }
            File dirTask = new File(joinPath(DIR_DATA_PATH, DIR_OUTPUT_PATH, path, taskName));
            if (!dirTask.isDirectory() && !dirTask.mkdir()) {
                System.out.println("WARNING: cannot create dir " + dirTask.getPath());
            }
            ImageRW.saveBImage(result, joinPath(path, taskName, image.name));
        }
        System.out.println("INFO: Saving images completed");
    }

    static final FilenameFilter IMAGE_FILTER = (dir, name) -> {
        for (final String ext : IMAGE_FORMATS) {
            if (name.endsWith("." + ext)) {
                return (true);
            }
        }
        return (false);
    };

    public static List<BufferedImage> loadBImages(String path) {
        String fullPath = joinPath(DIR_DATA_PATH, DIR_INPUT_PATH, path);
        File dir = new File(fullPath);
        if (!dir.isDirectory()) {
            System.out.println("WARNING: cannot load images from " + path);
        }
        List<BufferedImage> images = new ArrayList<>();
        for (final File f : Objects.requireNonNull(dir.listFiles(IMAGE_FILTER))) {
            images.add(loadBImage(f));
        }
        return images;
    }

    public static List<Image> loadImages(String path) {
        String fullPath = joinPath(DIR_DATA_PATH, DIR_INPUT_PATH, path);
        File dir = new File(fullPath);
        if (!dir.isDirectory()) {
            System.out.println("WARNING: cannot load images from " + path);
        }
        List<Image> images = new ArrayList<>();
        for (final File f : Objects.requireNonNull(dir.listFiles(IMAGE_FILTER))) {
            BufferedImage bImage = loadBImage(f);
            images.add(new Image(f.getName(), bImage));
        }
        return images;
    }

    private static void saveBImagesAsGif_(List<BufferedImage> images, String path) {
        String fullPath = joinPath(DIR_DATA_PATH, DIR_OUTPUT_PATH, checkFormat(path, GIF_FORMAT));
        try {
            ImageOutputStream output = new FileImageOutputStream(new File(fullPath));
            GifSequenceWriter writer = new GifSequenceWriter(
                    output, images.getFirst().getType(), 10, true
            );
            for (BufferedImage image : images) {
                writer.writeToSequence(image);
            }
            writer.close();
            output.close();
        } catch (IOException e) {
            System.out.println("WARNING: cannot save gif to " + fullPath);
        }
    }

    public static void saveBImagesAsGif(List<BufferedImage> bImages, String path) {
        List<Image> images = new ArrayList<>();
        for (BufferedImage bImage : bImages) {
            images.add(new Image("", bImage));
        }
        saveImagesAsGif(images, path);
    }

    public static void saveImagesAsGif(List<Image> images, String path) {
        List<Image> newImages = scaleMaxSize(images);
        List<BufferedImage> bImages = new ArrayList<>();
        for (Image image : newImages) {
            bImages.add(image.getBufferedImage());
        }
        saveBImagesAsGif_(bImages, path);
    }

    private static List<Image> scaleMaxSize(List<Image> images) {
        int w = 0;
        int h = 0;
        for (Image image : images) {
            w = Math.max(w, image.w);
            h = Math.max(h, image.h);
        }
        List<Image> newImages = new ArrayList<>();
        for (Image image : images) {
            newImages.add(geometric.center(image, w, h));
        }
        return newImages;
    }

    public static List<Image> imagesFromFuture(List<Future<Image>> results) {
        ArrayList<Image> images = new ArrayList<>();
        for (Future<Image> result : results) {
            try {
                images.add(result.get());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("WARNING: cannot get image from Future");
            }
        }
        return images;
    }
}
