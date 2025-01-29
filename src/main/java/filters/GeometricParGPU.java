package filters;

import com.aparapi.Kernel;
import com.aparapi.Range;
import helpers.BaseMath;
import model.Image;

public class GeometricParGPU implements Geometric {

    @Override
    public Image translation(Image image, int delX, int delY) {
        int w = image.w;
        int h = image.h;
        int gridSize = image.size;
        int[] grid = image.grid;
        Image newImage = image.copy();
        int[] newGrid = newImage.grid;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % w;
                int y = i / w;
                int newX = x + delX;
                int newY = y + delY;
                int j = i + delX + delY * w;
                if (0 <= newX && newX < w && 0 <= newY && newY < h) {
                    newGrid[j] = grid[i];
                }
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    @Override
    public Image center(Image image, int newW, int newH) {
        int w = image.w;
        int h = image.h;
        int delX = (int) (BaseMath.abs(newW - w) / 2);
        int delY = (int) (BaseMath.abs(newH - h) / 2);
        Image newImage = new Image(image.name, newW, newH, image.type);
        int[] newGrid = newImage.grid;
        int[] grid = image.grid;
        int gridSize = w * h;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % w;
                int y = i / w;
                if (x < newW && y < newH) {
                    int j = x + y * newW;
                    newGrid[j] = grid[i];
                }
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return translation(newImage, delX, delY);
    }

    @Override
    public Image crop(Image image, int newW, int newH) {
        int w = image.w;
        int h = image.h;
        int[] grid = image.grid;
        Image newImage = new Image(image.name, newW, newH, image.type);
        int[] newGrid = newImage.grid;
        int currW = BaseMath.min(w, newW);
        int currH = BaseMath.min(h, newH);
        int gridSize = currW * currH;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % currW;
                int y = i / currW;
                int j = x + y * newW;
                int k = x + y * w;
                newGrid[j] = grid[k];
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }


    @Override
    public Image scaling(Image image, double scaleW, double scaleH) {
        int w = image.w;
        int h = image.h;
        int[] grid = image.grid;
        int newW = (int) BaseMath.max(scaleW * w, 1);
        int newH = (int) BaseMath.max(scaleH * h, 1);
        int gridSize = newW * newH;
        Image newImage = new Image(image.name, newW, newH, image.type);
        int[] newGrid = newImage.grid;
        int maxSize = BaseMath.max(newW, newH);
        int[] rowIndex = new int[maxSize];
        int[] colIndex = new int[maxSize];
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                rowIndex[i] = BaseMath.min((int) BaseMath.round((i + 0.5) / scaleW - 0.5), w - 1);
                colIndex[i] = BaseMath.min((int) BaseMath.round((i + 0.5) / scaleH - 0.5), h - 1);
            }
        };
        kernel.execute(Range.create(maxSize));
        kernel.dispose();
        kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % newW;
                int y = i / newW;
                int j = rowIndex[x] + colIndex[y] * w;
                newGrid[i] = grid[j];
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    @Override
    public Image shearing(Image image, double angleX, double angleY) {
        double radianX = BaseMath.angle2Radians(angleX);
        double radianY = BaseMath.angle2Radians(angleY);
        double a = BaseMath.sinTailor(radianX);
        double b = BaseMath.sinTailor(radianY);
        return shearingByK(image, a, b);
    }

    @Override
    public Image shearingByK(Image image, double a, double b) {
        int w = image.w;
        int h = image.h;
        int[] grid = image.grid;
        int newW = (int) (w + BaseMath.abs(a) * h);
        int newH = (int) (h + BaseMath.abs(b) * w);
        Image newImage = new Image(image.name, newW, newH, image.type);
        int[] newGrid = newImage.grid;
        int delW = newW - w;
        int delH = newH - h;
        int gridSize = w * h;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % w;
                int y = i / w;
                int newX = (int) (x + a * y + (a > 0 ? 0 : delW));
                int newY = (int) (b * x + y + (b > 0 ? 0 : delH));
                if (0 <= newX && newX < newW && 0 <= newY && newY < newH) {
                    int j = newX + newY * newW;
                    newGrid[j] = grid[i];
                }
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    @Override
    public Image rotation90(Image image) {
        int w = image.w;
        int h = image.h;
        int gridSize = image.size;
        int[] grid = image.grid;
        Image newImage = new Image(image.name, h, w, image.type);
        int[] newGrid = newImage.grid;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % w;
                int y = i / w;
                int newX = h - y - 1;
                int newY = x;
                int j = newX + newY * h;
                newGrid[j] = grid[i];
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    @Override
    public Image rotation180(Image image) {
        int gridSize = image.size;
        int[] grid = image.grid;
        Image newImage = image.copy();
        int[] newGrid = newImage.grid;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int j = gridSize - i - 1;
                newGrid[j] = grid[i];
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    @Override
    public Image rotation270(Image image) {
        int w = image.w;
        int h = image.h;
        int gridSize = image.size;
        int[] grid = image.grid;
        Image newImage = new Image(image.name, h, w, image.type);
        int[] newGrid = newImage.grid;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int i = getGlobalId();
                int x = i % w;
                int y = i / w;
                int newX = y;
                int newY = w - x - 1;
                int j = newX + newY * h;
                newGrid[j] = grid[i];
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    @Override
    public Image rotation(Image image, double angle) {
        double radian = BaseMath.angle2Radians(angle);
        if (radian < 0) {
            radian += BaseMath.PIx2;
        }
        if (BaseMath.PId2 <= radian && radian < BaseMath.PI) {
            image = rotation90(image);
            radian -= BaseMath.PId2;
        } else if (BaseMath.PI <= radian && radian < BaseMath.PIx3d2) {
            image = rotation180(image);
            radian -= BaseMath.PI;
        } else if (BaseMath.PIx3d2 <= radian && radian < BaseMath.PIx2) {
            image = rotation270(image);
            radian -= BaseMath.PIx3d2;
        }
        int w = image.w;
        int h = image.h;
        double sin = BaseMath.sinTailor(radian);
        double cos = BaseMath.cosTailor(radian);
        double tgHalfAngle = BaseMath.tgTailor(radian / 2);
        int newW = (int) (BaseMath.abs(cos) * w + BaseMath.abs(sin) * h) + 10;
        int newH = (int) (BaseMath.abs(cos) * h + BaseMath.abs(sin) * w) + 10;
        Image newImage = shearingByK(image, -tgHalfAngle, 0);
        newImage = shearingByK(newImage, 0, sin);
        newImage = shearingByK(newImage, -tgHalfAngle, 0);
        int currW = newImage.w;
        int currH = newImage.h;
        int delWd2 = (currW - newW) / 2;
        int delHd2 = (currH - newH) / 2;
        newImage = translation(newImage, -delWd2, -delHd2);
        newImage = crop(newImage, newW, newH);
        return newImage;
    }
}
