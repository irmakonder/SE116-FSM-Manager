import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
public class FSM {
    static ArrayList<String> symbols;
    static ArrayList<String> states;
    static boolean logging = false;

    public static void main(String[] args) {
        LocalDate currentDate = LocalDate.now();
        Scanner scan = new Scanner(System.in);
        System.out.println("FSM DESIGNER<verisonNo> " + currentDate);
        while(true) {
            System.out.print("? ");
            String command = scan.nextLine();
            if (command.contains(";")) {
                String[] inputs = command.split(" ");
                int semicolonIndex = command.indexOf(';');

                String readLine = "";
                if (semicolonIndex != -1) {
                    readLine = command.substring(0, semicolonIndex);
                } else {
                    readLine += command;
                }
                processCommand(readLine);
            } else {
                System.out.println("Warning: ; is excepted.");
            }
        }
    }
    public static void processCommand(String prompt) {
        String[] input = prompt.split(" ");
        switch (input[0]) {
            case "exit":
                System.out.println("TERMINATED BY USER");
                System.exit(0);
                break;
            case "symbols":
                break;
            case "initial-state":
                break;
            case "final-state":
                break;
            case "log":
                break;
            case "states":
                break;
            case "transitions":
                break;
            case "print":
                break;
            case "compile":
                break;
            case "load":
                break;
        }
    }
}