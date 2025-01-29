package helpers;

public class ColorARGB {
    public static int argb2int(int[] argb) {
        return (0xFF & argb[0]) << 24 | (0xFF & argb[1]) << 16 | (0xFF & argb[2]) << 8 | (0xFF & argb[3]);
    }

    public static int[] int2argb(int argb) {
        return new int[]{(argb >> 24) & 0xFF, (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF};
    }

    public static int[] argb2hsv(int[] argb) {
        double rNorm = (double) argb[1] / 255;
        double gNorm = (double) argb[2] / 255;
        double bNorm = (double) argb[3] / 255;
        double cMax = BaseMath.max(rNorm, BaseMath.max(gNorm, bNorm));
        double cMin = BaseMath.min(rNorm, BaseMath.min(gNorm, bNorm));
        int v = (int) (cMax * 255);
        if (cMax == cMin) {
            return new int[]{0, 0, v};
        }
        double c = cMax - cMin;
        int s = (int) (c / cMax * 100);
        double rc = (cMax - rNorm) / c;
        double gc = (cMax - gNorm) / c;
        double bc = (cMax - bNorm) / c;
        int h;
        if (rNorm == cMax) {
            h = (int) (0 + bc - gc);
        } else if (gNorm == cMax) {
            h = (int) (2 + rc - bc);
        } else {
            h = (int) (4 + gc - rc);
        }
        h = (h % 6) * 180;
        return new int[]{h, s, v};
    }
}
