import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class HWE {

    private static double getNextP(double curretP, int populationSize) {
        double a = 0, b = 0;
        for(int i = 0; i <= 2*populationSize; i++){
            if(curretP >= ThreadLocalRandom.current().nextInt(0,2)){
                a++;
            }else{
                b++;
            }
        }
        return a/(a+b);
    }

    public static String getP(final double startP, final int populationSize, final int generations) {
        double p = startP;
        String pVals = "= {";
        for (int i = 1; i <= generations; i++) {
            pVals = pVals + p +"; ";
            p = getNextP(p, populationSize);
        }
        pVals = pVals + p +"}";
        System.out.println(pVals);

        return pVals;
    }

    public static void main(String[] args) {
        getP(0.5,10,100);
    }
}
