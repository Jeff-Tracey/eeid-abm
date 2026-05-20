package com.eid.lib.stochastic;

/*
 * RandomNumberGenerator.java
 * Created a static MTRandom object and supplies static methods to generate pseudo-
 *     random numbers.
 * 
 * THESE METHODS MUST BE SUBJECT TO RIGOROUS TESTING AGAINST THE TARGET DIST'NS.
 * 
 * @author Jeff A. Tracey
 * Copyright of Jeff A. Tracey, PhD 2009 (c)
 */

public class RandomNumberGenerator {
    /**
     * 
     */
    private static MTRandom rng = new MTRandom();
    // other static final instance vars
    
    /**
     * 
     * @return a random boolean.
     */
    public static boolean drawBoolean() {
        return rng.nextBoolean();
    }
    
    /**
     * Return an int between 0 and maxVal - 1 inclusive.
     * @param maxVal
     * @return a random integer.
     */
    public static int drawInteger(int maxVal) {
        return rng.nextInt(maxVal); // try catch IllegalArgumentException
    }
    
    /**
     * 
     * @return a random double.
     */
    public static double drawDouble() {
        return rng.nextDouble();
    }
    
    /**
     * Draw a single value from an exponential distribution with the rate given
     * as the argument.
     * @param rate the parameter of the exponential distribution.  This parameter
     * must be > 0.0.
     * @return a pseduo-random draw from an exponential distribution.  If the rate
     * parameters is <= 0.0, positive inifinity is returned.
     */
    public static double rExponential(double rate) {
        if (rate > 0.0) {
            double u = rng.nextDouble();
            return -1.0*Math.log(1.0 - u)/rate;
        } else {
            return Double.POSITIVE_INFINITY; // this is an error code
        }
    }
    
    /**
     * 
     * @param mean
     * @param var
     * @return
     */
    public static double rNormal(double mean, double var) {
        // add check on parameter ranges
        double fac, rsq, v1, v2;
        double res1, res2;

        do {
            v1 = 2.0 * rng.nextDouble() - 1.0;
            v2 = 2.0 * rng.nextDouble() - 1.0;
            rsq = v1 * v1 + v2 * v2;
        } while (rsq >= 1.0 || rsq == 0.0);
        fac = Math.sqrt(-2.0 * Math.log(rsq) / rsq);
        res1 = v2 * fac;
        res2 = res1 * Math.sqrt(var) + mean;
        return res2;
    }
    
    /**
     * Draw a single index from a multinomial distribution based on the probabilities
     * for each index in the argument.
     * @param probs an array of probabilities.  The array must have a length > 0
     * and all elements must be non-negative.  The elements are rescaled to ensure
     * that they add to 1.0.
     * @return a randomly selected index.  If there is an error, a -1 will be returned.
     */
    public static int rSample(double[] probs) {
        if ((probs != null) && (probs.length > 0)) {
            double probSum = 0.0;
            for (int i = 0; i < probs.length; i++) {
                if ((probs[i] >= 0.0) && (!Double.isNaN(probs[i]))) { // all values must be non-negative
                    probSum += probs[i];
                } else {
                    System.out.println("Warning: Negative or NaN probability passed to RandomNumberGenerator.rSample().");
                    return -1; // this is an error code
                }
            }
            if ((probSum > 0.0) && (!Double.isNaN(probSum))) {
                double u = probSum*rng.nextDouble(); // a more efficient way to scale sum of probs to 1.0
                int currInd = 0;
                double cumSum = probs[0];
                while ((u > cumSum) && (currInd < (probs.length - 1))) {
                    currInd++;
                    cumSum += probs[currInd];
                }
                return currInd; // the sampled index
            } else {
                //  THIS COULD CAUSE UNWANTED BEHAVIOR...SHOULD THROW AN ERROR...
                System.out.println("Warning: Index drawn from uniform in RandomNumberGenerator.rSample().");
                return RandomNumberGenerator.drawInteger(probs.length); // this is an error code; could return sample with equal prob
            }
        } else {
            System.out.println("Warning: Null probabilities passed to RandomNumberGenerator.rSample().");
            return -1; // this is an error code
        }
    }
    
    /**
     * 
     * @param n
     * @return
     */
    public static int[] permuteIntegers(int n) {
        if (n > 0) {
            // create output array
            int[] res = new int[n];
            for (int i = 0; i < n; i++) {
                res[i] = i;
            }
            // permute values in output array
            int swapIndex, tmpInt;
            for (int i = (n - 1); i > 0; i--) {
                swapIndex = RandomNumberGenerator.drawInteger(i + 1);
                tmpInt = res[i];
                res[i] = res[swapIndex];
                res[swapIndex] = tmpInt;
            }
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * Supports the rVonMises method.
     * 
     * @param ang
     * @return
     */
    private static double angleModulus(double ang) {
        double d = Math.floor(ang / (2.0 * Math.PI));
        double r = ang - 2.0 * Math.PI * d;
        if (r > Math.PI) {
            r -= 2.0 * Math.PI;
        }
        if (r < -1.0 * Math.PI) {
            r += 2.0 * Math.PI;
        }
        return r;
    }
    
    /**
     * 
     * @param mean
     * @param kappa
     * @return
     */
    public static double rVonMises(double mean, double kappa) {
        // add check on parameter ranges
        mean = angleModulus(mean);
        double theta;
        if (kappa < 0.000001) { // just draw a uniform RV
            theta = 2.0 * Math.PI * RandomNumberGenerator.drawDouble() - Math.PI;
        } else {
            double a, b, r, z, f, c;
            double u1, u2, u3;
            boolean do_draws = true;
            // let u1, u2, u3 be RNGs  on [0,1] drawn each time (i), (ii), or (iv) is executed
            a = 1.0 + Math.sqrt(1.0 + 4.0 * kappa * kappa);
            b = (a - Math.sqrt(2.0 * a)) / (2.0 * kappa);
            r = (1.0 + b * b) / (2.0 * b);
            f = Double.NaN;
            while (do_draws) {
                // (i)
                u1 = RandomNumberGenerator.drawDouble();
                z = Math.cos(Math.PI * u1);
                f = (1.0 + r * z) / (r + z);
                c = kappa * (r - f);
                u2 = RandomNumberGenerator.drawDouble();
                // (ii) 
                if ((c * (2.0 - c) - u2) > 0.0) {
                    do_draws = false;
                } else {
                    // (iii) 
                    if ((Math.log(c / u2) + 1.0 - c) >= 0.0) {
                        do_draws = false;
                    }
                }
            }
            // (iv)
            u3 = RandomNumberGenerator.drawDouble();
            if (u3 > 0.5) {
                theta = mean + Math.acos(f);
            } else if (u3 < 0.5) {
                theta = mean - Math.acos(f);
            } else {
                theta = mean;
            } // u3 == 0.5
        }
        theta = angleModulus(theta);
        return theta;
    }
    
    /**
     * 
     * @param alpha
     * @param beta
     * @return
     */
    public static double rGamma(double alpha, double beta) {
        double x, y;        // x is standard gamma, y is gamma w/ scale parameter
        double u, u1, u2;   // uniform random variates
        double b, p, c;     // nums for Algorithm GS and GKM3
        double aa, d, w, f; // nums for Algorithm GKM3
        if (alpha <= 0) {
            System.err.println("Error in randGamma1(): shape parameter <= 0.");
            System.exit(0);
        }
        if (beta <= 0) {
            System.err.println("Error in randGamma1(): scale parameter <= 0.");
            System.exit(0);
        }

        //cout << "In randGamma1() with alpha = " << a << " and lambda = " << l << endl; // FOR DEBUGGING

        // parameters in range, generate random variate
        if ((alpha > 0) && (alpha < 1)) {
            // use Ahrens and Dieter algorithm GS
            while (true) { // use break to escape
                u1 = RandomNumberGenerator.drawDouble();
                u2 = RandomNumberGenerator.drawDouble();
                b = 1.0 + alpha / Math.E;
                p = b * u1;
                if (p <= 1) {
                    x = Math.pow(p, 1.0 / alpha);
                    c = Math.pow(Math.E, -x);
                    if (u2 <= c) {
                        break;
                    }
                } else {
                    c = (b - p) / alpha;
                    x = -Math.log(c);
                    c = Math.pow(x, (alpha - 1.0));
                    if (u2 <= c) {
                        break;
                    }
                }
            }
        } // you know, if a == 1, it's an exponential distribution
        else {
            // use Cheng and Feast algorithm GKM3
            // set up constants
            aa = alpha - 1.0;
            b = (alpha - 1.0 / (6.0 * alpha)) / aa;
            c = 2.0 / aa;
            d = c + 2.0;
            while (true) { // use break to escape
                // 1.
                if (alpha < 2.5) {
                    u1 = RandomNumberGenerator.drawDouble();
                    u2 = RandomNumberGenerator.drawDouble();
                } else { // a >= 2.5
                    while (true) { // use break to escape
                        u = RandomNumberGenerator.drawDouble();
                        u1 = RandomNumberGenerator.drawDouble();
                        u2 = u1 + (1 - 1.86 * u) / (Math.sqrt(alpha));
                        if ((0 < u2) && (u2 < 1)) {
                            break;
                        }
                    }
                }
                // 2.
                w = (b * u1) / u2;
                f = c * u2 - d + w + 1 / w;
                if (f <= 0) {
                    x = aa * w;
                    break; // 4.
                } else { // 3.
                    f = c * Math.log(u2) - Math.log(w) + w - 1.0;
                    if (f < 0) {
                        x = aa * w;
                        break; // 4.
                    }
                }
            }
        }
        y = x / beta;
        return y;
    }
    
}
