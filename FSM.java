import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
public class FSM {
    static ArrayList<String> symbols = new ArrayList<>();
    static ArrayList<String> states = new ArrayList<>();
    static boolean logging = false;

    public static void main(String[] args) {
        LocalDate currentDate = LocalDate.now();
        Scanner scan = new Scanner(System.in);
        System.out.println("FSM DESIGNER<verisonNo> " + currentDate);
        if(args.length!=0) {
            // Read the file and process its commands first.
        }
        while(true) {
                System.out.print("? ");
                String command = scan.nextLine();
                String readLine;
                if (command.contains(";")) {
                    int semicolonIndex = command.indexOf(';');
                    readLine = command.substring(0, semicolonIndex);
                    if(logging) Log(null,command);
                    processCommand(readLine);
                } else {
                    System.out.println("Warning: ';' is excepted.");
                }
        }
    }
    public static void processCommand(String prompt) {
        String[] input = prompt.split(" ");
        String[] perimeters = new String[input.length-1];
        for (int i = 0; i < (input.length-1); i++) {
            perimeters[i] = input[i+1];
        }

        switch (input[0]) {
            case "EXIT":
                System.out.println("TERMINATED BY USER");
                System.exit(0);
                break;
            case "SYMBOLS":
                Symbols(perimeters);
                break;
            case "STATES":
                States(perimeters);
                break;
            case "INITIAL-STATE":
                InitialState(perimeters);
                break;
            case "FINAL-STATE":
                FinalState(perimeters);
                break;
            case "LOG":
                Log(perimeters,"");
                break;
            case "TRANSITIONS":
                Transitions(perimeters);
                break;
            case "PRINT":
                Print(perimeters);
                break;
            case "COMPILE":
                Compile(perimeters);
                break;
            case "LOAD":
                Load(perimeters);
                break;
            case "CLEAR":
                Clear();
                break;
            case "EXECUTE":
                Execute(perimeters);
                break;
        }
    }

    public static void Symbols(String[] perimeters) {
        if(perimeters.length==0) {
            System.out.print("{ ");
            for(String symbol : symbols) {
                System.out.print(symbol + " ");
            }
            System.out.println("}");
        } else {
            boolean contains;
            for(String perimeter : perimeters) {
                contains = false;
                if(perimeter.length()==1) {
                    for(String existingSymbols : symbols) {
                        if(perimeter.equalsIgnoreCase(existingSymbols)) {
                            contains = true;
                            break;
                        }
                    }
                    if(!contains) {
                        symbols.add(perimeter);
                    } else {
                        System.out.println("Warning: " + perimeter.toUpperCase() + " is already declared as a symbol. ");
                    }
                } else System.out.println("Invalid Symbol: " + perimeter);
            }
        }
    }

    public static void States(String[] perimeters) {
        // Add into states if not existent or print states.
    }

    public static void InitialState(String[] perimeters) {
        // Set initial state.
    }

    public static void FinalState(String[] perimeters) {
        // Set final state.
    }

    public static void Log(String[] perimeters, String log) {
        // Start or stop saving log.
    }

    public static void Transitions(String[] perimeters) {
        // Create transitions for execution.
    }

    public static void Print(String[] perimeters) {
        // Very complicated stuff here ngl...
    }

    public static void Compile(String[] perimeters) {
        // Serialize
    }

    public static void Load(String[] perimeters) {
        // Deserialize
    }

    public static void Clear() {
        // Thanos snap everything.
    }

    public static void Execute(String[] perimeters) {
        // Execute I don't know how it works at all...
    }
}