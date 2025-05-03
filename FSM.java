import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.nio.file.Paths;

class Main {
    public static void main(String[] args) {
        FSM fsm = new FSM();
        fsm.start(args);
    }
}

public class FSM implements Serializable {
    private ArrayList<String> symbols = new ArrayList<>();
    private ArrayList<String> states = new ArrayList<>();
    private ArrayList<String> initialState = new ArrayList<>();
    private ArrayList<String> finalState = new ArrayList<>();
    private ArrayList<String> transitions = new ArrayList<>();
    private boolean logging = false;

    public void start(String[] args) {
        LocalDate currentDate = LocalDate.now();
        Scanner scan = new Scanner(System.in);
        System.out.println("FSM DESIGNER<1.0> " + currentDate);
        if(args.length!=0) {
            // If there is args[] input
            try(Scanner reader = new Scanner(Paths.get(args[0]))) {
                while(reader.hasNextLine()) {
                    processFileCommand(reader.nextLine());
                }
            } catch (IOException e) {
                System.out.println("Something went wrong with I/O through args[]: " + e.getMessage());
            } catch (SecurityException e) {
                System.out.println("Security Issue: " + e.getMessage());
            }
        }
        takeInput();
    }
    private void takeInput(){
        String commandLine = "", input;
        Scanner scan = new Scanner(System.in);
        while(true) {
            System.out.print("? ");
            input = scan.nextLine();

            if (input.contains(";")) {
                int semicolonIndex = input.indexOf(';');
                commandLine += " " + input.substring(0, semicolonIndex).trim();
                if (logging) log(null, input);
                processCommand(commandLine.trim());
                commandLine = "";
            } else {
                commandLine += " " + input.trim();
            }
        }
    }
    private void processCommand(String prompt) {
        String[] input = prompt.split(" ");
        String[] parameters = new String[input.length-1];
        for (int i = 0; i < (input.length-1); i++) {
            parameters[i] = input[i+1];
        }
        try {
            switch (input[0]) {
                case "EXIT":
                    System.err.print("TERMINATED BY USER");
                    System.exit(0);
                    break;
                case "SYMBOLS":
                    symbols(parameters);
                    break;
                case "STATES":
                    states(parameters);
                    break;
                case "INITIAL-STATE":
                    initialState(parameters);
                    break;
                case "FINAL-STATE":
                    finalState(parameters);
                    break;
                case "LOG":
                    log(parameters, "LOG " + input[0]);
                    break;
                case "TRANSITIONS":
                    transitions(prompt);
                    break;
                case "PRINT":
                    print(parameters);
                    break;
                case "COMPILE":
                    compile(parameters);
                    break;
                case "LOAD":
                    load(parameters);
                    break;
                case "CLEAR":
                    clear();
                    break;
                case "EXECUTE":
                    execute(parameters);
                    break;
                default:
                    throw new UnknownCommandException(input[0]);
            }
        } catch (UnknownCommandException | InvalidInputException e) {
            System.out.println(e.getMessage());
        }
    }

    private void symbols(String[] parameters) {
        if(parameters.length==0) {
            System.out.print("{ ");
            for(String symbol : symbols) {
                System.out.print(symbol + " ");
            }
            System.out.println("}");
        } else {
            for(String parameter : parameters) {
                try {
                    for (char c : parameter.toCharArray()) {
                        if (!Character.isLetterOrDigit(c)) {
                            throw new NotAlphanumericException(parameter);
                        }
                    }
                    if (parameter.length() == 1) {
                        for (String existingSymbols : symbols) {
                            if (parameter.equalsIgnoreCase(existingSymbols)) {
                                throw new AlreadyDeclaredException(parameter.toUpperCase());
                            }
                        }
                        symbols.add(parameter.toUpperCase());
                    } else throw new InvalidInputException(parameter);
                } catch (InvalidInputException | NotAlphanumericException | AlreadyDeclaredException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void states(String[] parameters) {
        if(parameters.length==0) {
            System.out.print("[ ");
            for(String state : states) {
                System.out.print(state + " ");
            }
            System.out.println("]");
        } else {
            for(String perimeter : parameters) {
                try {
                    for (char c : perimeter.toCharArray()) {
                        if (!Character.isLetterOrDigit(c)) {
                            throw new NotAlphanumericException(perimeter);
                        }
                    }
                    if (perimeter.length() == 2) {
                        for (String existingStates : states) {
                            if (perimeter.equalsIgnoreCase(existingStates)) {
                                throw new AlreadyDeclaredException(perimeter.toUpperCase());
                            }
                        }
                        states.add(perimeter.toUpperCase());
                    } else throw new InvalidInputException(perimeter);
                } catch (InvalidInputException | NotAlphanumericException | AlreadyDeclaredException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void initialState(String[] parameters) throws InvalidInputException {
        if(parameters.length == 0) throw new InvalidInputException("due to missing state!");
        for (String parameter : parameters) {
            try {
                for (char c : parameter.toCharArray()) {
                    if (!Character.isLetterOrDigit(c)) throw new NotAlphanumericException(parameter);
                }
                if (!(parameter.length() == 2)) throw new InvalidInputException(parameter);
                for(String initials : initialState) {
                    if(parameter.equalsIgnoreCase(initials)) throw new AlreadyDeclaredException(parameter);
                }
                boolean found = false;
                for (String existingStates : states) {
                    if (parameter.equalsIgnoreCase(existingStates)) {
                        found = true;
                        break;
                    }
                }
                initialState.add(parameter.toUpperCase());
                if (!found) {
                    states.add(parameter);
                    throw new HasNotBeenDeclaredBefore(parameter);
                }
            } catch (InvalidInputException | NotAlphanumericException | HasNotBeenDeclaredBefore | AlreadyDeclaredException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void finalState(String[] parameters) throws InvalidInputException {
        if(parameters.length == 0) throw new InvalidInputException("due to missing state!");
        for (String perimeter : parameters) {
            try {
                for (char c : perimeter.toCharArray()) {
                    if (!Character.isLetterOrDigit(c)) throw new NotAlphanumericException(perimeter);
                }
                if (!(perimeter.length() == 2)) throw new InvalidInputException(perimeter);
                for(String initials : finalState) {
                    if(perimeter.equalsIgnoreCase(initials)) throw new AlreadyDeclaredException(perimeter);
                }
                boolean found = false;
                for (String existingStates : states) {
                    if (perimeter.equalsIgnoreCase(existingStates)) {
                        found = true;
                        break;
                    }
                }
                finalState.add(perimeter.toUpperCase());
                if (!found) {
                    states.add(perimeter);
                    throw new HasNotBeenDeclaredBefore(perimeter);
                }
            } catch (InvalidInputException | NotAlphanumericException | HasNotBeenDeclaredBefore | AlreadyDeclaredException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void log(String[] perimeters, String log) {
        // Start or stop saving log.
    }

    private void transitions(String prompt) throws InvalidInputException {
        String[] parts = prompt.substring("TRANSITIONS".length()).trim().split(",");
        for(int i = 0; i < parts.length;i++){
            String[] elements = parts[i].trim().split(" ");
            if(elements.length !=  3)throw new InvalidInputException (prompt);
            String symbol = elements[0].toUpperCase();
            String currentState = elements[1].toUpperCase();
            String result = elements[2].toUpperCase();
            for (int j=0; j < symbols.size();j++){
                if (symbols.get(j).equals(symbol))throw new InvalidInputException (symbol + " " + currentState + " " + result);
            }
            for (int j = 0; j < states.size();j++){
                if (states.get(j).equals(currentState))  throw new InvalidInputException (symbol + " " + currentState + " " + result);
                if (states.get(j).equals(result))  throw new InvalidInputException (symbol + " " + currentState + " " + result);
            }
            for (int j = 0; j < transitions.size();j++){
                String[] a = transitions.get(j).split(" ");
                if (a.length == 3 && a[0].equals(symbol) && a[1].equals(currentState)){
                    transitions.remove(j);
                    break;
                }
            }
            transitions.add(symbol + " " + currentState + " " + result);
        }

    }

    private String[] processTransition(String transition) {
        return transition.split(" ");
    }

    private void print(String[] parameters) {
        if(parameters.length == 0) {
            System.out.print("SYMBOLS: { ");
            for(String symbol : symbols) System.out.print(symbol + " ");
            System.out.println("}");
            System.out.print("STATES: [ ");
            for(String state : states) System.out.print(state + " ");
            System.out.println("]");
            System.out.print("INITIAL STATES: ");
            for(String initialStates : initialState) System.out.print(initialStates + " ");
            System.out.print("\nFINAL STATES: ");
            for(String finalStates : finalState) System.out.print(finalStates + " ");
            System.out.print("\nTRANSITIONS: { ");
            for(String transition : transitions) System.out.print(transition + " ");
            System.out.println("}");
            // Incomplete! (especially transitions part)
        } else {
            // And there is this section as well with user command input... :(
        }
    }

    private void compile(String[] parameters) throws InvalidInputException {
        if(parameters.length==0) throw new InvalidInputException(", too few arguments.");
        if(parameters.length>1) throw new InvalidInputException(", too many arguments.");
        String fileName = parameters[0];
        if(!fileName.endsWith(".ser")) throw new InvalidInputException(", file name must end with '.ser'");

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
            System.out.println("Compile process complete!");
        } catch (InvalidClassException e) {
            System.out.println("Warning: Invalid Class! " + e.getMessage());
        } catch (NotSerializableException e) {
            System.out.println("Warning: Not Serializable! " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Warning: Something went wrong! " + e.getMessage());
        }
    }

    private void load(String[] parameters) throws InvalidInputException {
        if(parameters.length==0) throw new InvalidInputException(", too few arguments.");
        if(parameters.length>1) throw new InvalidInputException(", too many arguments.");
        String fileName = parameters[0];
        if(fileName.endsWith(".ser")) {
            FSM readFSM = null;

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
                readFSM = (FSM) in.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } catch (InvalidClassException e) {
                System.out.println("Version not compatible: " + e.getMessage());
            } catch (StreamCorruptedException e) {
                System.out.println("File corrupted: " + e.getMessage());
            } catch (OptionalDataException e) {
                System.out.println("Unexpected data found: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Something went wrong! " + e.getMessage());
            }
            if(readFSM == null) throw new InvalidInputException(", failed to read the file.");
            this.setSymbols(readFSM.getSymbols());
            this.setStates(readFSM.getStates());
            this.setInitialState(readFSM.getInitialState());
            this.setFinalState(readFSM.getFinalState());
            this.setTransitions(readFSM.getTransitions());
            this.setLogging(readFSM.getLogging());
            System.out.println("Object loading successful!");

        } else if (fileName.endsWith(".txt")) {

            try(Scanner reader = new Scanner(Paths.get(fileName))) {
                while(reader.hasNextLine()) {
                    processFileCommand(reader.nextLine());
                }
            } catch (IOException e) {
                System.out.println("Something went wrong with I/O: " + e.getMessage());
            } catch (SecurityException e) {
                System.out.println("Security Exception: " + e.getMessage());
            }

        } else throw new InvalidInputException(", file extension must be either .ser or .txt ");
    }

    private void clear() {
        symbols = new ArrayList<>();
        states = new ArrayList<>();
        initialState = new ArrayList<>();
        finalState = new ArrayList<>();
        transitions = new ArrayList<>();
        System.out.println("Data cleared.");
    }

    private void processFileCommand(String command) {
        if (command == null || command.trim().isEmpty()) return;
        //Log için ayrı bölüm yaz
        String[] commandParts = command.split(";");
        processCommand(commandParts[0]);
        if(1 < commandParts.length ) log(null,command);

    }



    private void execute(String[] perimeters) {
        // Execute, I don't know how it works at all...
        System.out.println("Executing...");
    }

    public ArrayList<String> getSymbols() {
        return symbols;
    }
    public void setSymbols(ArrayList<String> symbols) {
        this.symbols = symbols;
    }
    public ArrayList<String> getStates() {
        return states;
    }
    public void setStates(ArrayList<String> states) {
        this.states = states;
    }
    public ArrayList<String> getInitialState() {
        return initialState;
    }
    public void setInitialState(ArrayList<String> initialState) {
        this.initialState = initialState;
    }
    public ArrayList<String> getFinalState() {
        return finalState;
    }
    public void setFinalState(ArrayList<String> finalState) {
        this.finalState = finalState;
    }
    public ArrayList<String> getTransitions() {
        return transitions;
    }
    public void setTransitions(ArrayList<String> transitions) {
        this.transitions = transitions;
    }
    public boolean getLogging() {
        return logging;
    }
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    
    public void execute(String input) {
        if (initialState.isEmpty()) {
            System.out.println("No initial state defined.");
            return;
        }

        String currentState = initialState.get(0);
        boolean rejected = false;

        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            boolean foundTransition = false;

            for (String transition : transitions) {
                String[] parts = transition.split(",");
                if (parts.length != 3) continue;

                String from = parts[0];
                String via = parts[1];
                String to = parts[2];

                if (from.equals(currentState) && via.equals(symbol)) {
                    currentState = to;
                    foundTransition = true;
                    break;
                }
            }

            if (!foundTransition) {
                System.out.println("Rejected: No transition found for symbol '" + symbol + "' from state '" + currentState + "'");
                rejected = true;
                break;
            }
        }

        if (!rejected) {
            if (finalState.contains(currentState)) {
                System.out.println("Accepted.");
            } else {
                System.out.println("Rejected: Ended at non-final state '" + currentState + "'");
            }
        }
    }
}

class NotAlphanumericException extends Exception {
    public NotAlphanumericException(String culprit) {
        super("Warning: Input is not Alphanumerical " + culprit);
    }
}

class InvalidInputException extends Exception {
    public InvalidInputException(String culprit) {
        super("Warning: Invalid Input " + culprit);
    }
}

class AlreadyDeclaredException extends Exception {
    public AlreadyDeclaredException(String culprit) {
        super("Warning: " + culprit + " have already been declared.");
    }
}

class UnknownCommandException extends Exception {
    public UnknownCommandException(String command) {
        super("Warning: Unknown command " + command);
    }
}

class HasNotBeenDeclaredBefore extends Exception {
    public HasNotBeenDeclaredBefore(String what) {
        super("Warning: Input has not been declared before " + what);
    }
}