import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class HWE2 extends Application {

    private static double startP;
    private static int genAmount;
    private static int amountOfPopulations;
    private static ArrayList<Integer>populationSizes = new ArrayList<>();

    private static ArrayList<XYChart.Series>series = new ArrayList<>();

    private static final String STRING_ARRAY_SAMPLE = "./test.csv";

    public HWE2(){
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter P Value:");
        startP = Double.parseDouble(keyboard.next());
        System.out.println("Enter Amount of Generations:");
        genAmount = Integer.parseInt(keyboard.next());
        System.out.println("Enter Amount of Populations:");
        amountOfPopulations = Integer.parseInt(keyboard.next());

        for(int i = 0; i <amountOfPopulations; i++){
            System.out.println("Enter Population Size:");
            populationSizes.add(Integer.parseInt(keyboard.next()));
            series.add(new XYChart.Series<>());
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
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
            series.get(i).setName("Population:" + populationSizes.get(i));

            ArrayList<Double> pVals = getP(startP, populationSizes.get(i), genAmount);
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

    public void runJFX(String[] args){
        if (series.size() == amountOfPopulations){
            launch(args);
        }
    }

    public void runGenerateCSV() throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(STRING_ARRAY_SAMPLE));
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);

        String[] headerRecord = new String[amountOfPopulations];
        for (int i = 0; i < amountOfPopulations; i++){
            headerRecord[i] = "Population:" + populationSizes.get(i);
            System.out.println(headerRecord[i]);
        }
        csvWriter.writeNext(headerRecord);
    }

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

    private static ArrayList<Double> getP(final double startP, final int populationSize, final int generations) {
        double p = startP;
        ArrayList<Double> pVals = new ArrayList<>();
        for (int i = 0; i <= generations; i++) {
            pVals.add(p);
            p = getNextP(p, populationSize);
        }

        return pVals;
    }

}
