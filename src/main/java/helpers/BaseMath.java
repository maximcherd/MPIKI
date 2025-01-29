package helpers;

public class BaseMath {
    private static final double DEGREES_TO_RADIANS = 0.017453292519943295;
    public static final double PI = 3.141592653589793;
    public static final double PId2 = 1.570796326794896;
    public static final double PIx3d2 = 4.712388980384689;
    public static final double PIx2 = 6.283185307179586;
    public static final double ALMOST_ZERO = 0.000000000000001;

    public static boolean almostZero(double x) {
        return abs(x) < ALMOST_ZERO;
    }

    public static double angle2Radians(double angle) {
        angle %= 360;
        return angle * DEGREES_TO_RADIANS;
    }

    public static long round(double x) {
        return Math.round(x);
    }

    public static double roundRadians(double radians) {
        return radians % PIx2;
    }

    public static long abs(long x) {
        return (x < 0) ? -x : x;
    }

    public static double abs(double x) {
        return (x < 0) ? -x : x;
    }

    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    public static long min(long a, long b) {
        return (a < b) ? a : b;
    }

    public static double min(double a, double b) {
        return (a < b) ? a : b;
    }

    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    public static long max(long a, long b) {
        return (a > b) ? a : b;
    }

    public static double max(double a, double b) {
        return (a > b) ? a : b;
    }

    public static double sinTailor(double radian) {
        boolean signPlus = true;
        if (radian < 0) {
            radian = -radian;
            signPlus = false;
        }
        if (PId2 <= radian && radian < PI) {
            radian = PI - radian;
        } else if (PI <= radian && radian < PIx3d2) {
            radian = radian - PI;
            signPlus = !signPlus;
        } else if (PIx3d2 <= radian && radian < PIx2) {
            radian = PIx2 - radian;
            signPlus = !signPlus;
        }
        int accuracy = 9; // >9 lead to overflow of long in factorial
        double result = 0;
        for (int i = 0; i < accuracy; i++) {
            int exp = 2 * i + 1;
            double term = pow(radian, exp) / factorial(exp);
            result += (i % 2 == 0 ? +term : -term);
        }
        result = almostZero(result) ? 0 : result;
        return signPlus ? result : -result;
    }

    public static double cosTailor(double radian) {
        return sinTailor(roundRadians(PId2 - radian));
    }

    public static double tgTailor(double radian) {
        double sin = sinTailor(radian);
        double cos =  sinTailor(roundRadians(PId2 - radian));
        if (almostZero(sin) || almostZero(cos)) {
            return 0;
        }
        return sin / cos;
    }

    public static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static double pow(double base, int exponent) {
        double result = 1;
        for (int i = 0; i < exponent; i++) {
            result *= base;
        }
        return result;
    }
}
