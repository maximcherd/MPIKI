package filters;

import helpers.BaseMath;
import model.Image;

public class GeometricSeq implements Geometric {

    @Override
    public Image translation(Image image, int delX, int delY) {
        Image newImage = image.copy();
        translation(image, newImage, delX, delY);
        return newImage;
    }

    private void translation(Image image, Image newImage, int delX, int delY) {
        int w = image.w;
        int h = image.h;
        int gridSize = image.size;
        int[] grid = image.grid;
        int[] newGrid = newImage.grid;
        for (int i = 0; i < gridSize; i++) {
            int x = i % w;
            int y = i / w;
            int newX = x + delX;
            int newY = y + delY;
            int j = i + delX + delY * w;
            if (0 <= newX && newX < w && 0 <= newY && newY < h) {
                newGrid[j] = grid[i];
            }
        }
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
        for (int i = 0; i < gridSize; i++) {
            int x = i % w;
            int y = i / w;
            if (x < newW && y < newH) {
                int j = x + y * newW;
                newGrid[j] = grid[i];
            }
        }
        return translation(newImage, delX, delY);
    }

    @Override
    public Image crop(Image image, int newW, int newH) {
        Image newImage = new Image(image.name, newW, newH, image.type);
        crop(image, newImage, newW, newH);
        return newImage;
    }

    private void crop(Image image, Image newImage, int newW, int newH) {
        int w = image.w;
        int h = image.h;
        int[] grid = image.grid;
        int[] newGrid = newImage.grid;
        int currW = BaseMath.min(w, newW);
        int currH = BaseMath.min(h, newH);
        int gridSize = currW * currH;
        for (int i = 0; i < gridSize; i++) {
            int x = i % currW;
            int y = i / currW;
            int j = x + y * newW;
            int k = x + y * w;
            newGrid[j] = grid[k];
        }
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
        for (int i = 0; i < maxSize; i++) {
            rowIndex[i] = BaseMath.min((int) BaseMath.round((i + 0.5) / scaleW - 0.5), w - 1);
            colIndex[i] = BaseMath.min((int) BaseMath.round((i + 0.5) / scaleH - 0.5), h - 1);
        }
        for (int i = 0; i < gridSize; i++) {
            int x = i % newW;
            int y = i / newW;
            int j = rowIndex[x] + colIndex[y] * w;
            newGrid[i] = grid[j];
        }
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
        int newW = (int) (w + BaseMath.abs(a) * h);
        int newH = (int) (h + BaseMath.abs(b) * w);
        Image newImage = new Image(image.name, newW, newH, image.type);
        shearingByK(image, newImage, a, b);
        return newImage;
    }

    public void shearingByK(Image image, Image newImage, double a, double b) {
        int w = image.w;
        int h = image.h;
        int[] grid = image.grid;
        int newW = (int) (w + BaseMath.abs(a) * h);
        int newH = (int) (h + BaseMath.abs(b) * w);
        int[] newGrid = newImage.grid;
        int delW = newW - w;
        int delH = newH - h;
        int gridSize = w * h;
        for (int i = 0; i < gridSize; i++) {
            int x = i % w;
            int y = i / w;
            int newX = (int) (x + a * y + (a > 0 ? 0 : delW));
            int newY = (int) (b * x + y + (b > 0 ? 0 : delH));
            if (0 <= newX && newX < newW && 0 <= newY && newY < newH) {
                int j = newX + newY * newW;
                int color = grid[i];
                newGrid[j] = color;
            }
        }
    }

    @Override
    public Image rotation90(Image image) {
        int w = image.w;
        int h = image.h;
        int gridSize = image.size;
        int[] grid = image.grid;
        Image newImage = new Image(image.name, h, w, image.type);
        int[] newGrid = newImage.grid;
        for (int i = 0; i < gridSize; i++) {
            int x = i % w;
            int y = i / w;
            int newX = h - y - 1;
            int newY = x;
            int j = newX + newY * h;
            newGrid[j] = grid[i];
        }
        return newImage;
    }

    @Override
    public Image rotation180(Image image) {
        int gridSize = image.size;
        int[] grid = image.grid;
        Image newImage = image.copy();
        int[] newGrid = newImage.grid;
        for (int i = 0; i < gridSize; i++) {
            int j = gridSize - i - 1;
            newGrid[j] = grid[i];
        }
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
        for (int i = 0; i < gridSize; i++) {
            int x = i % w;
            int y = i / w;
            int newX = y;
            int newY = w - x - 1;
            int j = newX + newY * h;
            newGrid[j] = grid[i];
        }
        return newImage;
    }

    @Override
    public Image rotation(Image image, double angle) {
        System.out.println("start");
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
        System.out.println("init");
        final int w = image.w;
        final int h = image.h;
        final double sin = BaseMath.sinTailor(radian);
        final double cos = BaseMath.cosTailor(radian);
        final double tgHalfAngle = BaseMath.tgTailor(radian / 2);
        final int newW = (int) (BaseMath.abs(cos) * w + BaseMath.abs(sin) * h) + 10;
        final int newH = (int) (BaseMath.abs(cos) * h + BaseMath.abs(sin) * w) + 10;

        int newW1 = (int) (w + BaseMath.abs(-tgHalfAngle) * h);
        int newH1 = (int) (h);
        int newW2 = (int) (newW1);
        int newH2 = (int) (newH1 + BaseMath.abs(sin) * newW1);
        int newW3 = (int) (newW2 + BaseMath.abs(-tgHalfAngle) * newH2);
        int newH3 = (int) (newH2);
        System.out.println("rotation");
        System.out.println("newW/newH 1=" + newW1 + "/" + newH1);
        System.out.println("newW/newH 2=" + newW2 + "/" + newH2);
        System.out.println("newW/newH 3=" + newW3 + "/" + newH3);
        Image newImage = new Image(image.name, newW3, newH3, image.type);
        shearingByK(image, newImage, -tgHalfAngle, 0);
        shearingByK(newImage, newImage, 0, sin);
        shearingByK(newImage, newImage, -tgHalfAngle, 0);
        final int currW = newImage.w;
        final int currH = newImage.h;
        final int delWd2 = (currW - newW) / 2;
        final int delHd2 = (currH - newH) / 2;
        translation(newImage, newImage, -delWd2, -delHd2);
        crop(newImage, newImage, newW, newH);
        return newImage;
    }
}
