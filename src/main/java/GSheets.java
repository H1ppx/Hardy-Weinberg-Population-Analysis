


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.services.sheets.v4.Sheets;
import javafx.scene.chart.XYChart;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GSheets {

    private static final String APPLICATION_NAME =
            "Google Sheets API Java Quickstart";

    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart");

    private static FileDataStoreFactory DATA_STORE_FACTORY;
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;

    private static final List<String> SCOPES =
            Arrays.asList(SheetsScopes.SPREADSHEETS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static Credential authorize() throws IOException {
        // Load client secrets.
//        InputStream in = Quickstart.class.getResourceAsStream("/client_secret.json");
        InputStream in    = new FileInputStream(
                "C:\\Users\\willc\\Documents\\Hardy-Weinberg-Population-Analysis\\client_secret.json");
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
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void manual() throws IOException {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        // https://docs.google.com/spreadsheets/d/1AljqFAAp91zZOyBGmx3mjMWjmvXJjRRBAWFvhV20lNw/edit
        String spreadsheetId = "1hj8MVQLchcMSyVkMPl66osQ8D1tE6DGkSQ-JBJxDU_c";

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter P Value:");
        final double initP = Double.parseDouble(keyboard.next());
        System.out.println("Enter Amount of Generations:");
        final int generations = Integer.parseInt(keyboard.next());
        System.out.println("Enter Amount of Populations:");
        final int amountOfPopulations = Integer.parseInt(keyboard.next());

        ArrayList<Integer> populationSizes = new ArrayList<>();
        for(int i = 0; i <amountOfPopulations; i++){
            System.out.println("Enter Population Size:");
            populationSizes.add(Integer.parseInt(keyboard.next()));
        }

        String sheet ="Lab!";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<String> ranges = new ArrayList<>();
        ArrayList<List<List<Object>>> values = new ArrayList<>();
        for (int i = 1; i <= populationSizes.size();i++) {
            values.add(Arrays.asList(Arrays.asList(HWE.getP(initP, populationSizes.get(i-1), generations))));
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

    public static void auto() throws IOException {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        // https://docs.google.com/spreadsheets/d/1AljqFAAp91zZOyBGmx3mjMWjmvXJjRRBAWFvhV20lNw/edit
        String spreadsheetId = "1hj8MVQLchcMSyVkMPl66osQ8D1tE6DGkSQ-JBJxDU_c";
        String range = "Lab!B56:Z";

        while(true) {
            List<List<Object>> values = Arrays.asList(
                    Arrays.asList(HWE.getP(0.5, 10, 1000)),
                    Arrays.asList(HWE.getP(0.5, 100, 1000)),
                    Arrays.asList(HWE.getP(0.5, 1000, 1000)),
                    Arrays.asList(HWE.getP(0.5, 10000, 1000)),
                    Arrays.asList(HWE.getP(0.5, 100000, 1000)),
                    Arrays.asList(HWE.getP(0.5, 1000000, 1000)),
                    Arrays.asList(HWE.getP(0.5, 10000000, 1000))
            );


            List<ValueRange> data = new ArrayList<ValueRange>();
            data.add(new ValueRange()
                    .setRange(range)
                    .setValues(values));
            // Additional ranges to update ...

            BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                    .setValueInputOption("USER_ENTERED")
                    .setData(data);
            BatchUpdateValuesResponse result =
                    service.spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
            System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
        }
    }

    public static void main(String[] args) throws IOException {
        manual();
    }



}