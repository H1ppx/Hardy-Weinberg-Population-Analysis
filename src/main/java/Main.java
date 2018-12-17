import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        HWE2 hwe2 = new HWE2();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("JFX: Enter 1, Generate CSV: Enter 2");
        int dispOption = Integer.parseInt(keyboard.next());
        switch(dispOption){
            case 1:
                hwe2.runJFX(args);
                break;

            case 2:
                hwe2.runGenerateCSV();
                break;
        }

    }

}
