package model;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Image {
    final public String name;
    public int[] grid;
    final public int w;
    final public int h;
    public int type;
    final public int size;

    public Image(String name, int[] grid, int w, int h, int type) {
        this.name = name;
        this.grid = grid;
        this.w = w;
        this.h = h;
        this.type = type;
        this.size = this.grid.length;
    }

    public Image(String name, int w, int h, int type) {
        this.name = name;
        this.grid = new int[w * h];
        this.w = w;
        this.h = h;
        this.type = type;
        this.size = this.grid.length;
    }

    public Image(String name, BufferedImage image) {
        this.name = name;
        this.grid = this.image2grid(image);
        this.w = image.getWidth();
        this.h = image.getHeight();
        this.type = image.getType();
        this.size = this.grid.length;
    }

    public Image copy() {
        return new Image(this.name, this.w, this.h, this.type);
    }

    public Image copyWithGrid() {
        return new Image(this.name, this.grid.clone(), this.w, this.h, this.type);
    }

    public BufferedImage getBufferedImage() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long timeout = 600;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        BufferedImage image = new BufferedImage(this.w, this.h, this.type);
        int chunkSize = this.size / availableProcessors;
        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
        boolean isCompleted = false;
        try {
            for (int chunkStart = 0; chunkStart < this.size; chunkStart += chunkSize) {
                final int start = chunkStart;
                executorService.submit(() -> {
                    for (int i = start; i < this.size && i < start + chunkSize; i++) {
                        int x = i % this.w;
                        int y = i / this.w;
                        image.setRGB(x, y, this.grid[i]);
                    }
                });
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: getBufferedImage in " + this.name + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: getBufferedImage in " + this.name + " reached timeout");
        }
        return image;
    }

    private int[] image2grid(BufferedImage image) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long timeout = 600;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        int w = image.getWidth();
        int h = image.getHeight();
        int gridSize = w * h;
        int[] gridCurr = new int[gridSize];
        int chunkSize = gridSize / availableProcessors;
        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);
        boolean isCompleted = false;
        try {
            for (int chunkStart = 0; chunkStart < gridSize; chunkStart += chunkSize) {
                final int start = chunkStart;
                executorService.submit(() -> {
                    for (int i = start; i < gridSize && i < start + chunkSize; i++) {
                        int x = i % w;
                        int y = i / w;
                        gridCurr[i] = image.getRGB(x, y);
                    }
                });
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: getBufferedImage in " + this.name + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: getBufferedImage in " + this.name + " reached timeout");
        }
        return gridCurr;
    }
}
