import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.Scanner;
import java.time.LocalDate;
public class FSM {
    static ArrayList<String> symbols;
    static ArrayList<String> states;

    public static void main(String[] args) {
        LocalDate currentDate = LocalDate.now();
        Scanner scan = new Scanner(System.in);
        System.out.println("FSM DESIGNER<verisonNo> " + currentDate);
        while(true) {
            System.out.print("? ");
            String command = scan.nextLine();
            if (command.contains(";")) {
                String[] inputs = command.split(" ");
            } else {
                System.out.println("Warning: ; is excepted.");
            }
            int semicolonIndex = command.indexOf(';');

            String readLine = "";
            if (semicolonIndex != -1) {
                readLine = command.substring(0, semicolonIndex);
            } else {
                readLine += command;
            }
            processCommand();
        }
    }
    public static void processCommand() {

    }
}