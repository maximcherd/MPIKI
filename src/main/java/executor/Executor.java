package executor;

import filters.*;
import helpers.ImageRW;
import model.Image;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Executor {
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
            for (int i = 200; i > 50; i -= 2) {
                results.add(geometric.scaling(image, (double) i / 100, (double) 100 / i));
            }
            for (int i = 50; i < 200; i += 2) {
                results.add(geometric.scaling(image, (double) i / 100, (double) 100 / i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        ImageRW.saveImages(results, saveDir, taskName);
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
            for (int i = 200; i > 50; i -= 2) {
                final int j = i;
                results.add(executorService.submit(() ->
                        geometric.scaling(image, (double) j / 100, (double) 100 / j)
                ));
            }
            for (int i = 50; i < 200; i += 2) {
                final int j = i;
                results.add(executorService.submit(() ->
                        geometric.scaling(image, (double) j / 100, (double) 100 / j)
                ));
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
        ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        return timeDelta;
    }

    private static long shearing(String taskName, Image image, Geometric geometric, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (int i = 0; i < 360; i += 5) {
                results.add(geometric.shearing(image, -i, 0));
            }
            for (int i = 0; i < 360; i += 5) {
                results.add(geometric.shearing(image, 0, -i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        ImageRW.saveImages(results, saveDir, taskName);
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
            for (int i = 0; i < 360; i += 5) {
                final int j = i;
                results.add(executorService.submit(() ->
                        geometric.shearing(image, -j, 0)
                ));
            }
            for (int i = 0; i < 360; i += 5) {
                final int j = i;
                results.add(executorService.submit(() ->
                        geometric.shearing(image, 0, -j)
                ));
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
        ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        return timeDelta;
    }

    private static long rotation(String taskName, Image image, Geometric geometric, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (int i = 0; i < 360; i += 2) {
                results.add(geometric.rotation(image, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        ImageRW.saveImages(results, saveDir, taskName);
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
            for (int i = 0; i < 360; i += 5) {
                final int j = i;
                results.add(executorService.submit(() ->
                        geometric.rotation(image, j)
                ));
            }
            for (int i = 0; i < 360; i += 5) {
                final int j = i;
                results.add(executorService.submit(() ->
                        geometric.rotation(image, j)
                ));
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
        ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        return timeDelta;
    }

    private static long grayscale(String taskName, List<Image> images, Color color, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (Image image : images) {
                results.add(color.grayscale(image));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        ImageRW.saveImages(results, saveDir, taskName);
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
                results.add(executorService.submit(() ->
                        color.grayscale(image)
                ));
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
        ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        return timeDelta;
    }

    private static long redMask(String taskName, List<Image> images, Color color, String saveDir) {
        taskName = new Exception().getStackTrace()[0].getMethodName() + taskName;
        List<Image> results = new ArrayList<>();
        System.out.println("INFO " + taskName + " started...");
        long startTime = System.currentTimeMillis();
        try {
            for (Image image : images) {
                results.add(color.redMask(image));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("WARNING: " + taskName + " gone wrong");
        }
        long endTime = System.currentTimeMillis();
        long timeDelta = endTime - startTime;
        System.out.println("INFO: " + taskName + " completed in " + timeDelta + " ms");
        ImageRW.saveImages(results, saveDir, taskName);
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
                results.add(executorService.submit(() ->
                        color.redMask(image)
                ));
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
        ImageRW.saveImages(ImageRW.imagesFromFuture(results), saveDir, taskName);
        return timeDelta;
    }

//    public static void startTestsGeometric() {
//        long timeScalingSeq = scalingSeq();
//        long timeScalingParCPU = scalingParCPU();
//        long timeScalingParGPU = scalingParGPU();
//        double effScalingParCPU = (double) timeScalingSeq / timeScalingParCPU / AVAILABLE_PROCESSORS * 100;
//        double effScalingParGPU = (double) timeScalingSeq / timeScalingParGPU * 100;
//
//        long timeShearingSeq = shearingSeq();
//        long timeShearingParCPU = shearingParCPU();
//        long timeShearingParGPU = shearingParGPU();
//        double effShearingParCPU = (double) timeShearingSeq / timeShearingParCPU / AVAILABLE_PROCESSORS * 100;
//        double effShearingParGPU = (double) timeShearingSeq / timeShearingParGPU * 100;
//
//        long timeRotationSeq = rotationSeq();
//        long timeRotationParCPU = rotationParCPU();
//        long timeRotationParGPU = rotationParGPU();
//        double effRotationParCPU = (double) timeRotationSeq / timeRotationParCPU / AVAILABLE_PROCESSORS * 100;
//        double effRotationParGPU = (double) timeRotationSeq / timeRotationParGPU * 100;
//
//        System.out.println("Scaling CPU efficiency: " + String.format("%1.2f", effScalingParCPU) + "%");
//        System.out.println("Scaling GPU efficiency: " + String.format("%1.2f", effScalingParGPU) + "%");
//        System.out.println("Rotation CPU efficiency: " + String.format("%1$,.2f", effShearingParCPU) + "%");
//        System.out.println("Rotation GPU efficiency: " + String.format("%1$,.2f", effShearingParGPU) + "%");
//        System.out.println("Rotation CPU efficiency: " + String.format("%1$,.2f", effRotationParCPU) + "%");
//        System.out.println("Rotation GPU efficiency: " + String.format("%1$,.2f", effRotationParGPU) + "%");
//    }

//    public static void startTestsColor(String imageDir) {
//        String taskNameSeq = "Seq";
//        String taskNameCPU = "ParCPU";
//        String taskNameGPU = "ParGPU";
//        List<Image> images = ImageRW.loadImages(imageDir);
//
//        long timeGrayscaleSeq = grayscale(taskNameSeq, images, colorSeq, imageDir);
//        long timeGrayscaleParCPUH = grayscaleParCPUH(images, colorSeq, imageDir);
//        long timeGrayscaleParCPU = grayscale(taskNameCPU, images, colorParCPU, imageDir);
//        long timeGrayscaleParGPU = grayscale(taskNameGPU, images, colorParGPU, imageDir);
//        double effGrayscaleParCPUH = (double) timeGrayscaleSeq / timeGrayscaleParCPUH / CPU_AVAILABLE_PROCESSORS * 100;
//        double effGrayscaleParCPU = (double) timeGrayscaleSeq / timeGrayscaleParCPU / CPU_AVAILABLE_PROCESSORS * 100;
//        double effGrayscaleParGPU = (double) timeGrayscaleSeq / timeGrayscaleParGPU / GPU_AVAILABLE_PROCESSORS * 100;
//
//        long timeRedMaskSeq = redMask(taskNameSeq, images, colorSeq, imageDir);
//        long timeRedMaskParCPUH = redMaskParCPUH(images, colorSeq, imageDir);
//        long timeRedMaskParCPU = redMask(taskNameCPU, images, colorParCPU, imageDir);
//        long timeRedMaskParGPU = redMask(taskNameGPU, images, colorParGPU, imageDir);
//        double effRedMaskParCPUH = (double) timeRedMaskSeq / timeRedMaskParCPUH / CPU_AVAILABLE_PROCESSORS * 100;
//        double effRedMaskParCPU = (double) timeRedMaskSeq / timeRedMaskParCPU / CPU_AVAILABLE_PROCESSORS * 100;
//        double effRedMaskParGPU = (double) timeRedMaskSeq / timeRedMaskParGPU / GPU_AVAILABLE_PROCESSORS * 100;
//
//        System.out.println("Grayscale CPUH efficiency: " + String.format("%04.2f", effGrayscaleParCPUH) + "%");
//        System.out.println("Grayscale CPU  efficiency: " + String.format("%04.2f", effGrayscaleParCPU) + "%");
//        System.out.println("Grayscale GPU  efficiency: " + String.format("%04.2f", effGrayscaleParGPU) + "%");
//        System.out.println("RedMask   CPUH efficiency: " + String.format("%04.2f", effRedMaskParCPUH) + "%");
//        System.out.println("RedMask   CPU  efficiency: " + String.format("%04.2f", effRedMaskParCPU) + "%");
//        System.out.println("RedMask   GPU  efficiency: " + String.format("%04.2f", effRedMaskParGPU) + "%");
//    }

    public static void testColor(
            Object object, Method methodBase, Method methodCPUH, List<Image> images, String imageDir
    ) throws Exception {
        String taskNameSeq = "Seq";
        String taskNameCPU = "ParCPU";
        String taskNameGPU = "ParGPU";
        long timeSeq = (long) methodBase.invoke(object, taskNameSeq, images, colorSeq, imageDir);
        long timeParCPUH = (long) methodCPUH.invoke(object, images, colorSeq, imageDir);
        long timeParCPU = (long) methodBase.invoke(object, taskNameCPU, images, colorParCPU, imageDir);
        long timeParGPU = (long) methodBase.invoke(object, taskNameGPU, images, colorParGPU, imageDir);
        double effParCPUH = (double) timeSeq / timeParCPUH / CPU_AVAILABLE_PROCESSORS * 100;
        double effParCPU = (double) timeSeq / timeParCPU / CPU_AVAILABLE_PROCESSORS * 100;
        double effParGPU = (double) timeSeq / timeParGPU / GPU_AVAILABLE_PROCESSORS * 100;

        System.out.println(methodBase.getName() + " test complete:");
        System.out.println("CPUH efficiency: " + String.format("%04.2f", effParCPUH) + "%");
        System.out.println("CPU  efficiency: " + String.format("%04.2f", effParCPU) + "%");
        System.out.println("GPU  efficiency: " + String.format("%04.2f", effParGPU) + "%");
    }

    public static void testGeometric(
            Object object, Method methodBase, Method methodCPUH, Image image, String imageDir
    ) throws Exception {
        String taskNameSeq = "Seq";
        String taskNameCPU = "ParCPU";
        String taskNameGPU = "ParGPU";
        long timeSeq = (long) methodBase.invoke(object, taskNameSeq, image, geometricSeq, imageDir);
        long timeParCPUH = (long) methodCPUH.invoke(object, image, geometricSeq, imageDir);
        long timeParCPU = (long) methodBase.invoke(object, taskNameCPU, image, geometricParCPU, imageDir);
        long timeParGPU = (long) methodBase.invoke(object, taskNameGPU, image, geometricParGPU, imageDir);
        double effParCPUH = (double) timeSeq / timeParCPUH / CPU_AVAILABLE_PROCESSORS * 100;
        double effParCPU = (double) timeSeq / timeParCPU / CPU_AVAILABLE_PROCESSORS * 100;
        double effParGPU = (double) timeSeq / timeParGPU / GPU_AVAILABLE_PROCESSORS * 100;

        System.out.println(methodBase.getName() + " test complete:");
        System.out.println("CPUH efficiency: " + String.format("%04.2f", effParCPUH) + "%");
        System.out.println("CPU  efficiency: " + String.format("%04.2f", effParCPU) + "%");
        System.out.println("GPU  efficiency: " + String.format("%04.2f", effParGPU) + "%");
    }

    public static void main(String[] args) throws Exception {
        String imageDirColor = "2";
        String imageDirGeometric = "1";
        System.out.println("CPU processors: " + CPU_AVAILABLE_PROCESSORS);
        System.out.println("GPU processors: " + GPU_AVAILABLE_PROCESSORS);
        List<Image> imagesColor = ImageRW.loadImages(imageDirColor);
        List<Image> imagesGeometric = ImageRW.loadImages(imageDirColor);
        Image imageGeometric = imagesGeometric.getFirst();

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
        testColor(new Executor(), grayscaleBase, grayscaleCPUH, imagesColor, imageDirColor);
        testColor(new Executor(), redMaskBase, redMaskCPUH, imagesColor, imageDirColor);
        testGeometric(new Executor(), scalingBase, scalingCPUH, imageGeometric, imageDirGeometric);
        testGeometric(new Executor(), shearingBase, shearingCPUH, imageGeometric, imageDirGeometric);
        testGeometric(new Executor(), rotationBase, rotationCPUH, imageGeometric, imageDirGeometric);
    }
}
