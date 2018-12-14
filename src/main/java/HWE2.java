import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
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

    public static void runGoogleSheets() throws IOException, GeneralSecurityException {
        final String APPLICATION_NAME =
                "Google Sheets API Java Quickstart";

        final java.io.File DATA_STORE_DIR = new java.io.File(
                System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

        FileDataStoreFactory DATA_STORE_FACTORY;
        final JsonFactory JSON_FACTORY =
                JacksonFactory.getDefaultInstance();

        HttpTransport HTTP_TRANSPORT;

        final List<String> SCOPES =
                Arrays.asList(SheetsScopes.SPREADSHEETS);


        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

        // Build a new authorized API client service.
        InputStream in    = new FileInputStream(
                System.getProperty("user.dir")+"\\client_secret.json");

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        // https://docs.google.com/spreadsheets/d/1AljqFAAp91zZOyBGmx3mjMWjmvXJjRRBAWFvhV20lNw/edit
        String spreadsheetId = "1hj8MVQLchcMSyVkMPl66osQ8D1tE6DGkSQ-JBJxDU_c";

        String sheet ="Lab!";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<String> ranges = new ArrayList<>();
        ArrayList<List<List<Object>>> values = new ArrayList<>();
        for (int i = 1; i <= populationSizes.size();i++) {
            values.add(Arrays.asList(Arrays.asList(getP(startP, populationSizes.get(i-1), genAmount))));
            ranges.add(sheet+alphabet.charAt(i)+"56:Z");
        }

        List<ValueRange> data = new ArrayList<ValueRange>();
        for (int i = 1; i<= values.size(); i++){
            data.add(new ValueRange()
                    .setRange(ranges.get(i-1))
                    .setValues(values.get(i-1)));
        }


        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption("USER_ENTERED")
                .setData(data);
        BatchUpdateValuesResponse result =
                service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
        System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
    }

    public static void runJFX(String[] args){
        if (series.size() == amountOfPopulations){
            launch(args);
        }
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
