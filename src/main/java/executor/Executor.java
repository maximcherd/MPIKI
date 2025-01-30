package executor;

import filters.*;
import helpers.ImageRW;
import model.Image;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Executor {
    public static final boolean SAVE_IMAGES = false;
    public static final boolean GENERATE_IMAGE = true;
    public static final int STEPS_COUNT = 1;
    public static final int[] STEPS = getSteps();
    public static final long TIMEOUT = 600;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static final int CPU_AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final int GPU_AVAILABLE_PROCESSORS = 32;
    public static Geometric geometricSeq = new GeometricSeq();
    public static Geometric geometricParCPU = new GeometricParCPU();
    public static Geometric geometricParGPU = new GeometricParGPU();
    public static Color colorSeq = new ColorSeq();
    public static Color colorParCPU = new ColorParCPU();
    public static Color colorParGPU = new ColorParGPU();

    private static long scaling(String taskName, Image image, Geometric geometric, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (final int i : STEPS) {
                if (SAVE_IMAGES) {
                    results.add(geometric.scaling(image, (double) i / 100, (double) 100 / i));
                } else {
                    geometric.scaling(image, (double) i / 100, (double) 100 / i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImagesAsGif(results, saveDir, taskName);
        }
        return timeDelta;
    }

    private static long scalingParCPUH(Image image, Geometric geometric, String saveDir) {
        String taskName = new Exception().getStackTrace()[0].getMethodName();
        List<Future<Image>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(CPU_AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (final int i : STEPS) {
                if (SAVE_IMAGES) {
                    results.add(executorService.submit(() ->
                            geometric.scaling(image, (double) i / 100, (double) 100 / i)
                    ));
                } else {
                    executorService.submit(() ->
                            geometric.scaling(image, (double) i / 100, (double) 100 / i)
                    );
                }
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: " + taskName + " reached timeout");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImagesAsGif(ImageRW.imagesFromFuture(results), saveDir, taskName);
        }
        return timeDelta;
    }

    private static long shearing(String taskName, Image image, Geometric geometric, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (final int i : STEPS) {
                final int x = -i;
                final int y = 0;
                if (SAVE_IMAGES) {
                    results.add(geometric.shearing(image, x, y));
                } else {
                    geometric.shearing(image, x, y);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImagesAsGif(results, saveDir, taskName);
        }
        return timeDelta;
    }

    private static long shearingParCPUH(Image image, Geometric geometric, String saveDir) {
        String taskName = new Exception().getStackTrace()[0].getMethodName();
        List<Future<Image>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(CPU_AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (final int i : STEPS) {
                final int x = -i;
                final int y = 0;
                if (SAVE_IMAGES) {
                    results.add(executorService.submit(() ->
                            geometric.shearing(image, x, y)
                    ));
                } else {
                    executorService.submit(() ->
                            geometric.shearing(image, x, y)
                    );
                }
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: " + taskName + " reached timeout");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImagesAsGif(ImageRW.imagesFromFuture(results), saveDir, taskName);
        }
        return timeDelta;
    }

    private static long rotation(String taskName, Image image, Geometric geometric, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (final int i : STEPS) {
                if (SAVE_IMAGES) {
                    results.add(geometric.rotation(image, i));
                } else {
                    geometric.rotation(image, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImagesAsGif(results, saveDir, taskName);
        }
        return timeDelta;
    }

    private static long rotationParCPUH(Image image, Geometric geometric, String saveDir) {
        String taskName = new Exception().getStackTrace()[0].getMethodName();
        List<Future<Image>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(CPU_AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (final int i : STEPS) {
                if (SAVE_IMAGES) {
                    results.add(executorService.submit(() ->
                            geometric.rotation(image, i)
                    ));
                } else {
                    executorService.submit(() ->
                            geometric.rotation(image, i)
                    );
                }
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: " + taskName + " reached timeout");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImagesAsGif(ImageRW.imagesFromFuture(results), saveDir, taskName);
        }
        return timeDelta;
    }

    private static long grayscale(String taskName, List<Image> images, Color color, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (Image image : images) {
                if (SAVE_IMAGES) {
                    results.add(color.grayscale(image));
                } else {
                    color.grayscale(image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImages(results, saveDir, taskName);
        }
        return timeDelta;
    }

    private static long grayscaleParCPUH(List<Image> images, Color color, String saveDir) {
        String taskName = new Exception().getStackTrace()[0].getMethodName();
        List<Future<Image>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(CPU_AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (Image image : images) {
                if (SAVE_IMAGES) {
                    results.add(executorService.submit(() ->
                            color.grayscale(image)
                    ));
                } else {
                    executorService.submit(() ->
                            color.grayscale(image)
                    );
                }
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: " + taskName + " reached timeout");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        }
        return timeDelta;
    }

    private static long redMask(String taskName, List<Image> images, Color color, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (Image image : images) {
                if (SAVE_IMAGES) {
                    results.add(color.redMask(image));
                } else {
                    color.redMask(image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImages(results, saveDir, taskName);
        }
        return timeDelta;
    }

    private static long redMaskParCPUH(List<Image> images, Color color, String saveDir) {
        String taskName = new Exception().getStackTrace()[0].getMethodName();
        List<Future<Image>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(CPU_AVAILABLE_PROCESSORS);
        boolean isCompleted = false;
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (Image image : images) {
                if (SAVE_IMAGES) {
                    results.add(executorService.submit(() ->
                            color.redMask(image)
                    ));
                } else {
                    executorService.submit(() ->
                            color.redMask(image)
                    );
                }
            }
            executorService.shutdown();
            isCompleted = executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        if (!isCompleted) {
            executorService.shutdownNow();
            System.out.println("WARNING: " + taskName + " reached timeout");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        if (SAVE_IMAGES) {
            ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        }
        return timeDelta;
    }

    public static void testColor(
            Object object, Method methodBase, Method methodCPUH, List<Image> images, String imageDir
    ) throws Exception {
        String taskNameSeq = "Seq";
        String taskNameCPU = "ParCPU";
        String taskNameGPU = "ParGPU";
        long timeSeq = (long) methodBase.invoke(object, taskNameSeq, images, colorSeq, imageDir);
        long timeParCPU = (long) methodBase.invoke(object, taskNameCPU, images, colorParCPU, imageDir);
        long timeParGPU = (long) methodBase.invoke(object, taskNameGPU, images, colorParGPU, imageDir);
        long timeParCPUH = 0;
        double effParCPUH = 0;
        if (images.size() > 1) {
            timeParCPUH = (long) methodCPUH.invoke(object, images, colorSeq, imageDir);
            effParCPUH = (double) timeSeq / timeParCPUH / CPU_AVAILABLE_PROCESSORS * 100;
        }
        double effParCPU = (double) timeSeq / timeParCPU / CPU_AVAILABLE_PROCESSORS * 100;
        double effParGPU = (double) timeSeq / timeParGPU / GPU_AVAILABLE_PROCESSORS * 100;

        System.out.println(methodBase.getName() + " test complete:");
        System.out.println("CPU  efficiency: " + String.format("%05.2f", effParCPU) + "%");
        System.out.println("GPU  efficiency: " + String.format("%05.2f", effParGPU) + "%");
        if (images.size() > 1) {
            System.out.println("CPUH efficiency: " + String.format("%05.2f", effParCPUH) + "%");
        }
    }

    public static void testGeometric(
            Object object, Method methodBase, Method methodCPUH, Image image, String imageDir
    ) throws Exception {
        String taskNameSeq = "Seq";
        String taskNameCPU = "ParCPU";
        String taskNameGPU = "ParGPU";
        long timeSeq = (long) methodBase.invoke(object, taskNameSeq, image, geometricSeq, imageDir);
        long timeParCPU = (long) methodBase.invoke(object, taskNameCPU, image, geometricParCPU, imageDir);
        long timeParGPU = (long) methodBase.invoke(object, taskNameGPU, image, geometricParGPU, imageDir);
        long timeParCPUH = 0;
        double effParCPUH = 0;
        if (STEPS_COUNT > 1) {
            timeParCPUH = (long) methodCPUH.invoke(object, image, geometricSeq, imageDir);
            effParCPUH = (double) timeSeq / timeParCPUH / CPU_AVAILABLE_PROCESSORS * 100;
        }
        double effParCPU = (double) timeSeq / timeParCPU / CPU_AVAILABLE_PROCESSORS * 100;
        double effParGPU = (double) timeSeq / timeParGPU / GPU_AVAILABLE_PROCESSORS * 100;

        System.out.println(methodBase.getName() + " test complete:");
        System.out.println("CPU  efficiency: " + String.format("%05.2f", effParCPU) + "%");
        System.out.println("GPU  efficiency: " + String.format("%05.2f", effParGPU) + "%");
        if (STEPS_COUNT > 1) {
            System.out.println("CPUH efficiency: " + String.format("%05.2f", effParCPUH) + "%");
        }
    }

    private static Image generateImage(String name, int w, int h) {
        Random random = new Random();
        int gridSize = w * h;
        int[] grid = new int[gridSize];
        for (int i = 0; i < gridSize; i++) {
            grid[i] = random.nextInt();
        }
        return new Image(name, grid, w, h, BufferedImage.TYPE_INT_ARGB);
    }

    private static int[] getSteps() {
        int[] steps = new int[STEPS_COUNT];
        for (int i = 0; i < STEPS_COUNT; i++) {
            steps[i] = (int) (360 * Math.random());
        }
        return steps;
    }

    public static void main(String[] args) throws Exception {
        String imageDir = "1";
        String imagesDir = "2";
        System.out.println("CPU processors: " + CPU_AVAILABLE_PROCESSORS);
        System.out.println("GPU processors: " + GPU_AVAILABLE_PROCESSORS);
        Image image;
        List<Image> images;
        if (GENERATE_IMAGE) {
            imageDir = imagesDir = "generated";
            image = generateImage("test", 20000, 20000);
            images = new ArrayList<>();
            images.add(image);
        } else {
            images = ImageRW.loadImages(imageDir);
            image = images.getFirst();
            images = ImageRW.loadImages(imagesDir);
        }

        // методы для теста обработки преобразований цвета
        Method grayscaleBase = Executor.class.getDeclaredMethod("grayscale", String.class, List.class, Color.class, String.class);
        Method grayscaleCPUH = Executor.class.getDeclaredMethod("grayscaleParCPUH", List.class, Color.class, String.class);
        Method redMaskBase = Executor.class.getDeclaredMethod("redMask", String.class, List.class, Color.class, String.class);
        Method redMaskCPUH = Executor.class.getDeclaredMethod("redMaskParCPUH", List.class, Color.class, String.class);

        // методы для теста обработки геометрических преобразований
        Method scalingBase = Executor.class.getDeclaredMethod("scaling", String.class, Image.class, Geometric.class, String.class);
        Method scalingCPUH = Executor.class.getDeclaredMethod("scalingParCPUH", Image.class, Geometric.class, String.class);
        Method shearingBase = Executor.class.getDeclaredMethod("shearing", String.class, Image.class, Geometric.class, String.class);
        Method shearingCPUH = Executor.class.getDeclaredMethod("shearingParCPUH", Image.class, Geometric.class, String.class);
        Method rotationBase = Executor.class.getDeclaredMethod("rotation", String.class, Image.class, Geometric.class, String.class);
        Method rotationCPUH = Executor.class.getDeclaredMethod("rotationParCPUH", Image.class, Geometric.class, String.class);

        // запуск тестов
//        testColor(new Executor(), grayscaleBase, grayscaleCPUH, images, imagesDir);
//        testColor(new Executor(), redMaskBase, redMaskCPUH, images, imagesDir);
//        testGeometric(new Executor(), scalingBase, scalingCPUH, image, imageDir);
//        testGeometric(new Executor(), shearingBase, shearingCPUH, image, imageDir);
        testGeometric(new Executor(), rotationBase, rotationCPUH, image, imageDir);
    }
}
