package filters;

import com.aparapi.Kernel;
import com.aparapi.Range;
import helpers.BaseMath;
import model.Image;


public class ColorParGPU implements Color {

    @Override
    public Image grayscale(Image image) {
        Image newImage = image.copy();
        final int gridSize = image.size;
        final int[] grid = image.grid;
        int[] newGrid = newImage.grid;
        newImage.type = TYPE_GRAY;
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                final int i = getGlobalId();
                final int color = grid[i];
                newGrid[i] = (int) (0.2989 * ((color >> 16) & 0xFF) + 0.5870 * ((color >> 8) & 0xFF) + 0.1140 * (color & 0xFF));
            }
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
        return newImage;
    }

    private void grayscale(int[] grid, int[] newGrid) {

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
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                final int i = getGlobalId();
                final int color = grid[i];
                final int red = (color >> 16) & 0xFF;
                final int green = (color >> 8) & 0xFF;
                final int blue = color & 0xFF;
                final double rNorm = (double) red / 255;
                final double gNorm = (double) green / 255;
                final double bNorm = (double) blue / 255;
                final double cMax = BaseMath.max(rNorm, BaseMath.max(gNorm, bNorm));
                final double cMin = BaseMath.min(rNorm, BaseMath.min(gNorm, bNorm));
                int h = 0;
                int s = 0;
                int v = (int) (cMax * 255);
                if (cMax != cMin) {
                    final double c = cMax - cMin;
                    s = (int) (c / cMax * 100);
                    final double rc = (cMax - rNorm) / c;
                    final double gc = (cMax - gNorm) / c;
                    final double bc = (cMax - bNorm) / c;
                    if (rNorm == cMax) {
                        h = (int) (0 + bc - gc);
                    } else if (gNorm == cMax) {
                        h = (int) (2 + rc - bc);
                    } else {
                        h = (int) (4 + gc - rc);
                    }
                    h = (h % 6) * 180;
                }
                final int[] hsv = new int[]{h, s, v};
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
        };
        kernel.execute(Range.create(gridSize));
        kernel.dispose();
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
