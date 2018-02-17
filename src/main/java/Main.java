import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter 1 for google charts, Enter 2 for jfx chart");
        final double response = Double.parseDouble(keyboard.next());
        if(response == 1){
            GSheets.manual();
        } else {
            jfxApp.main(args);
        }
    }
}
