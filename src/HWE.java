import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Scanner;

public class HWE extends Application {

    static double startP = 0;
    static int genAmount = 0;
    static int amountOfPopulations = 0;

    static ArrayList<Integer>populationAmounts = new ArrayList<>();
    static ArrayList<XYChart.Series>series = new ArrayList<>();


    public static String generateZygote(double p, int amountOfZygotes) {
        String zygotes = "";
        for (int i = 1; i <= amountOfZygotes; i++) {
            double number1 = Math.random();
            double number2 = Math.random();
            String pt1, pt2;
            if (number1 < p) {
                pt1 = "A";
            } else {
                pt1 = "B";
            }
            if (number2 < p) {
                pt2 = "A";
            } else {
                pt2 = "B";
            }
            zygotes = zygotes+(pt1 + pt2);
        }
        return zygotes;
    }



    public static ArrayList<Double> getP(double startP, int generations, int zygotes) {
        double p = startP;
        ArrayList<Double> pVals = new ArrayList<>();
        for (int i = 0; i <= generations; i++) {
            String currentGeneration = generateZygote(p, zygotes);
            currentGeneration = currentGeneration.replace("B","");
            pVals.add(currentGeneration.length()/(zygotes*2.0));
            System.out.println("["+(((double) i/generations)*100.0)+"%]");
        }
        return pVals;
    }

    @Override public void start(Stage stage) {
        stage.setTitle("HWE EQ");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generations");
        yAxis.setLabel("Frequency of P");
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Changes in p values across " + genAmount + " generations with different population sizes");

        for (int i = 0; i < amountOfPopulations; i++){
            series.get(i).setName("Population:" + populationAmounts.get(i));

            ArrayList<Double> pVals = getP(startP, genAmount, populationAmounts.get(i));
            System.out.println(pVals);
            for (int j = 0; j<=pVals.size()-1; j++){
                double currentP = pVals.get(j);
                series.get(i).getData().add(new XYChart.Data(j, currentP));
            }
        }



        Scene scene  = new Scene(lineChart,800,600);

        for (int i = 0; i < amountOfPopulations; i++){
            lineChart.getData().add(series.get(i));
        }

        lineChart.setCreateSymbols(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter P Value:");
        startP = Double.parseDouble(keyboard.next());
        System.out.println("Enter Amount of Generations:");
        genAmount = Integer.parseInt(keyboard.next());
        System.out.println("Enter Amount of Populations:");
        amountOfPopulations = Integer.parseInt(keyboard.next());

        for(int i = 0; i <amountOfPopulations; i++){
            System.out.println("Enter Population Size:");
            populationAmounts.add(Integer.parseInt(keyboard.next()));
            series.add(new XYChart.Series<>());
        }




        if (series.size() == amountOfPopulations){
            System.out.println("Status: Ready");

            launch(args);
        }
    }
}