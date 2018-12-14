import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("GSheets: Enter 1, JFX: Enter 2");
        int dispOption = Integer.parseInt(keyboard.next());
        switch(dispOption){
            case 1:
                HWE2.runGoogleSheets();
                break;

            case 2:
                HWE2.runJFX(args);
                break;
        }

    }

}
