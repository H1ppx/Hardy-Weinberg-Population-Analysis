import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class HWE extends Application {

    private double pVal;
    private int genAmount;

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Hardy-Weinberg Equilibrium Model");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generations");
        yAxis.setLabel("Frequency of P");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1);
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("P Value vs Generation vs Population Size");
        lineChart.setCreateSymbols(false);

        ArrayList<ArrayList> csvData = new ArrayList<>();

        Scene scene  = new Scene(new Group());

        Label pLabel = new Label("Start P:");
        TextField pTextfield = new TextField();

        Label genLabel = new Label("Generations:");
        TextField genTextField = new TextField();

        Label popLabel = new Label("Population Size:");
        TextField popTextField = new TextField();

        Button add1 = new Button("Add");
        Button add2 = new Button("Add");
        Button add3 = new Button("Add");

        Circle confirm1 = new Circle(15, Color.RED);
        Circle confirm2 = new Circle(15, Color.RED);

        HBox hboxP = new HBox();
        HBox hboxGen = new HBox();
        HBox hboxPop = new HBox();

        VBox vbox = new VBox();

        hboxP.getChildren().addAll(pLabel, pTextfield, add1, confirm1);
        hboxGen.getChildren().addAll(genLabel, genTextField, add2, confirm2);
        hboxPop.getChildren().addAll(popLabel, popTextField, add3);

        hboxP.setSpacing(10);
        hboxP.setPadding(new Insets(10, 5, 10, 5));

        hboxGen.setSpacing(10);
        hboxGen.setPadding(new Insets(10, 5, 10, 5));

        hboxPop.setSpacing(10);
        hboxPop.setPadding(new Insets(10, 5, 10, 5));

        vbox.getChildren().addAll(lineChart, hboxP, hboxGen, hboxPop);

        add1.setOnAction(event -> {
            pVal = Double.parseDouble(pTextfield.getText());
            pTextfield.setEditable(false);
            confirm1.setFill(Color.GREEN);
            hboxP.getChildren().remove(2);
        });

        add2.setOnAction(event -> {
            genAmount = Integer.parseInt(genTextField.getText());
            genTextField.setEditable(false);
            confirm2.setFill(Color.GREEN);
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(genAmount);
            hboxGen.getChildren().remove(2);
        });

        add3.setOnAction(event -> {
            int popSize = Integer.parseInt(popTextField.getText());
            popTextField.clear();

            XYChart.Series tempSeries = new XYChart.Series();
            tempSeries.setName("Population:" + popSize);
            ArrayList<Double> tempfile


            ArrayList<Double> pVals = getP(pVal, popSize, genAmount);
            for (double i = 0; i<=pVals.size()-1; i++){
                double currentP = pVals.get((int) i);
                tempSeries.getData().add(new XYChart.Data(i, currentP));
            }

            lineChart.getData().add(tempSeries);

        });

        ((Group)scene.getRoot()).getChildren().add(vbox);
        stage.setScene(scene);
        stage.show();
    }

    private double getNextP(double curretP, int populationSize) {
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

    private ArrayList<Double> getP(final double startP, final int populationSize, final int generations) {
        double p = startP;
        ArrayList<Double> pVals = new ArrayList<>();
        for (int i = 0; i <= generations; i++) {
            pVals.add(p);
            p = getNextP(p, populationSize);
        }

        return pVals;
    }

}
