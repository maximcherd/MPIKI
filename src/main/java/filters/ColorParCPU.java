package filters;

import helpers.ColorARGB;
import model.Image;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ColorParCPU implements Color {
    @Override
    public Image grayscale(Image image) {
        Image newImage = image.copy();
        newImage.type = TYPE_GRAY;
        final int gridSize = image.size;
        final int[] grid = image.grid;
        int[] newGrid = newImage.grid;
        final int chunkSize = gridSize / AVAILABLE_PROCESSORS;
        ExecutorService executorService = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        try {
            for (int chunkStart = 0; chunkStart < gridSize; chunkStart += chunkSize) {
                final int start = chunkStart;
                executorService.submit(() -> {
                    for (int i = start; i < gridSize && i < start + chunkSize; i++) {
                        final int color = grid[i];
                        final int[] argb = ColorARGB.int2argb(color);
                        newGrid[i] = (int) (0.2989 * argb[1] + 0.5870 * argb[2] + 0.1140 * argb[3]);
                    }
                });
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: grayscale gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: grayscale reached timeout");
        }
        return newImage;
    }

    @Override
    public Image redMask(Image image) {
        final int[] red1from = new int[]{0, 75, 50};
        final int[] red1to = new int[]{8, 255, 255};
        final int[] red2from = new int[]{172, 75, 50};
        final int[] red2to = new int[]{180, 255, 255};

        Image newImage = image.copy();
        final int gridSize = image.size;
        final int[] grid = image.grid;
        int[] newGrid = newImage.grid;
        final int chunkSize = gridSize / AVAILABLE_PROCESSORS;
        ExecutorService executorService = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        try {
            for (int chunkStart = 0; chunkStart < gridSize; chunkStart += chunkSize) {
                final int start = chunkStart;
                executorService.submit(() -> {
                    for (int i = start; i < gridSize && i < start + chunkSize; i++) {
                        int color = grid[i];
                        int[] argb = ColorARGB.int2argb(color);
                        int[] hsv = ColorARGB.argb2hsv(argb);
                        boolean isRed1 = true;
                        boolean isRed2 = true;
                        for (int j = 0; j < 3; j++) {
                            if (hsv[j] < red1from[j] || red1to[j] < hsv[j]) {
                                isRed1 = false;
                            }
                            if (hsv[j] < red2from[j] || red2to[j] < hsv[j]) {
                                isRed2 = false;
                            }
                        }
                        if (isRed1 || isRed2) {
                            newGrid[i] = color;
                        }
                    }
                });
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: grayscale gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: grayscale reached timeout");
        }
        return newImage;
    }

    @Override
    public Image contrast(Image image, double scale) {
        return null;
    }

    @Override
    public Image brightness(Image image, double scale) {
        return null;
    }
}
