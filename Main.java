import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        FSM fsm = new FSM();
        fsm.start(args);
    }
}

class FSM implements Serializable {
    private ArrayList<String> symbols = new ArrayList<>();
    private ArrayList<String> states = new ArrayList<>();
    private String initialState = "";
    private ArrayList<String> finalState = new ArrayList<>();
    private ArrayList<String> transitions = new ArrayList<>();
    private boolean logging = false;
    private String fileName = "";

    public void start(String[] args) {
        LocalDate currentDate = LocalDate.now();
        System.out.println("FSM DESIGNER<1.0> " + currentDate);
        if(args.length!=0) {
            try(Scanner reader = new Scanner(Paths.get(args[0]))) {
                String commandLine = "";
                while(reader.hasNextLine()) {
                    String command = reader.nextLine();
                    if(logging) log("PLZLOG/" + command);

                    if (command.contains(";")) {
                        int semicolonIndex = command.indexOf(';');
                        commandLine += " " + command.substring(0, semicolonIndex).trim();
                        processFileCommand(commandLine.trim());
                        commandLine = "";
                    } else commandLine += " " + command.trim();
                }
            } catch (SecurityException e) {
                System.out.println("Security Issue!");
            } catch(FileNotFoundException e) {
                System.out.println("File not found!");
            } catch (IOException e) {
                System.out.println("Something went wrong with I/O through args[]");
            }
        }
        takeInput();
    }
    private void takeInput() {
        String commandLine = "", input;
        Scanner scan = new Scanner(System.in);
        while(true) {
            System.out.print("? ");
            input = scan.nextLine();

            if (input.contains(";")) {
                int semicolonIndex = input.indexOf(';');
                commandLine += " " + input.substring(0, semicolonIndex).trim();
                if (logging) log("PLZLOG/" + input);
                processCommand(commandLine.trim());
                commandLine = "";
            } else commandLine += " " + input.trim();
        }
    }
    private void processCommand(String prompt) {
        String[] input = prompt.split(" ");
        String[] parameters = new String[input.length-1];
        for (int i = 0; i < (input.length-1); i++) parameters[i] = input[i+1];

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
                case "FINAL-STATES":
                    finalStates(parameters);
                    break;
                case "LOG":
                    log(prompt);
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
                    execute(parameters[0]);
                    break;
                default:
                    throw new UnknownCommandException(input[0]);
            }
        } catch (UnknownCommandException | InvalidInputException | HasNotBeenDeclaredBefore e) {
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
            for(String parameter : parameters) {
                try {
                    for (char c : parameter.toCharArray()) if (!Character.isLetterOrDigit(c)) throw new NotAlphanumericException(parameter);
                    if (parameter.length() == 2) {
                        if(initialState.isEmpty()) initialState = parameter.toUpperCase();
                        for (String existingStates : states) {
                            if (parameter.equalsIgnoreCase(existingStates)) {
                                throw new AlreadyDeclaredException(parameter.toUpperCase());
                            }
                        }
                        states.add(parameter.toUpperCase());
                    } else throw new InvalidInputException(parameter);
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
                if(parameter.equalsIgnoreCase(initialState)) throw new AlreadyDeclaredException(parameter);
                boolean found = false;
                for (String existingStates : states) {
                    if (parameter.equalsIgnoreCase(existingStates)) {
                        found = true;
                        break;
                    }
                }
                initialState = parameter.toUpperCase();
                if (!found) {
                    states.add(parameter);
                    throw new HasNotBeenDeclaredBefore(parameter);
                }
            } catch (InvalidInputException | NotAlphanumericException | HasNotBeenDeclaredBefore | AlreadyDeclaredException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void finalStates(String[] parameters) throws InvalidInputException {
        if(parameters.length == 0) throw new InvalidInputException("due to missing state!");
        for (String parameter : parameters) {
            try {
                for (char c : parameter.toCharArray()) {
                    if (!Character.isLetterOrDigit(c)) throw new NotAlphanumericException(parameter);
                }
                if (!(parameter.length() == 2)) throw new InvalidInputException(parameter);
                for(String initials : finalState) {
                    if(parameter.equalsIgnoreCase(initials)) throw new AlreadyDeclaredException(parameter);
                }
                boolean found = false;
                for (String existingStates : states) {
                    if (parameter.equalsIgnoreCase(existingStates)) {
                        found = true;
                        break;
                    }
                }
                finalState.add(parameter.toUpperCase());
                if (!found) {
                    states.add(parameter);
                    throw new HasNotBeenDeclaredBefore(parameter);
                }
            } catch (InvalidInputException | NotAlphanumericException | HasNotBeenDeclaredBefore | AlreadyDeclaredException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void log(String prompt) {
        if(prompt.equals("LOG")) {
            logging = !logging;
            return;
        }
        if(prompt.startsWith("PLZLOG/")) {
            int index = prompt.indexOf('/');
            prompt = prompt.substring(index+1).trim();

            try {
                if(!Files.exists(Paths.get(fileName))) Files.createFile(Paths.get(fileName));

                try (PrintWriter writer = new PrintWriter(new FileWriter(Paths.get(fileName).toFile(), true))) {
                    writer.println(prompt);
                    writer.flush();
                }
            } catch (SecurityException e) {
                System.out.println("Security Issue: " + e.getMessage());
            } catch (IOException e) {
                if(fileName.isEmpty()) {
                    System.out.println("Warning: A file name is required for logging!");
                } else {
                    System.out.println("Warning: Logging failed!");
                }
            }
        } else {
            String[] commands = prompt.trim().split(" ");
            if(commands.length!=2) {
                System.out.println("Warning: Invalid Input, too many arguments for LOG command. Expected is 1");
                return;
            }
            if(!commands[1].endsWith(".txt")) commands[1] += ".txt";
            if(!logging) logging = true;
            setFileName(commands[1]);
        }
    }

    private void transitions(String directPrompt) throws InvalidInputException {
        int gap = directPrompt.trim().indexOf(' ');
        String prompt = "";
        if (gap != -1) prompt = directPrompt.substring(gap + 1).trim();

        if(prompt.isEmpty()) {
            System.out.println("TRANSITIONS:");
            for(String transition : transitions) {
                System.out.print("{ " + transition);
                System.out.println(" }");
            }
        } else {
            String[] mainParts = prompt.trim().split(",");
            for (int i = 0; i < mainParts.length; i++) {
                try {
                    String[] elements = mainParts[i].trim().split("\\s+");
                    if (elements.length != 3) throw new InvalidInputException(mainParts[i]);

                    String symbol = elements[0].toUpperCase();
                    String currentState = elements[1].toUpperCase();
                    String nextState = elements[2].toUpperCase();

                    String notDeclared = "";
                    boolean s=false,cs=false,ns=false;
                    if (!symbols.isEmpty()) {
                        for (String string : symbols) if (symbol.equals(string)) s=true;
                    }
                    if(!s) notDeclared += symbol + " ";

                    if (!states.isEmpty()) {
                        for (String state : states) {
                            if (state.equals(currentState)) cs=true;
                            if (state.equals(nextState)) ns=true;
                        }
                    }
                    if(!cs) notDeclared += currentState + " ";
                    if(!ns) notDeclared += nextState + " ";

                    if(!notDeclared.isEmpty()) throw new HasNotBeenDeclaredBefore(notDeclared + " (or must be at 'symbol currentState nextState' format divided by comma)");

                    boolean set = false;
                    for (int j = 0; j < transitions.size(); j++) {
                        String[] existingTransitions = transitions.get(j).split(" ");
                        if (existingTransitions.length == 3 && existingTransitions[0].equals(symbol) && existingTransitions[1].equals(currentState)) {
                            transitions.set(j, symbol + " " + currentState + " " + nextState);
                            set=true;
                            break;
                        }
                    }
                    if(!set) transitions.add(symbol + " " + currentState + " " + nextState);
                } catch (HasNotBeenDeclaredBefore | InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void print(String[] parameters) throws InvalidInputException {
        if(parameters.length == 0) {
            System.out.println("Logging: " + logging);
            System.out.print("SYMBOLS: { ");
            for(String symbol : symbols) System.out.print(symbol + " ");
            System.out.println("}");
            System.out.print("STATES: [ ");
            for(String state : states) System.out.print(state + " ");
            System.out.println("]");
            System.out.print("INITIAL STATE: " + initialState);
            System.out.print("\nFINAL STATES: ");
            for(String finalStates : finalState) System.out.print(finalStates + " ");
            System.out.println("\nTRANSITIONS:");
            for(String transition : transitions) {
                System.out.print("{ " + transition);
                System.out.println(" }");
            }
        } else if (parameters.length == 1) {
            String outFile = parameters[0];
            if (!outFile.endsWith(".txt")) outFile += ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
                if(!Files.exists(Paths.get(outFile))) Files.createFile(Paths.get(outFile));

                if(!symbols.isEmpty()){
                    writer.print("SYMBOLS");
                    for (String sym : symbols) writer.print(" " + sym);
                    writer.println(";");
                }

                if(!states.isEmpty()) {
                    writer.print("STATES");
                    for (String st : states) writer.print(" " + st);
                    writer.println(";");
                }

                if (!initialState.isEmpty()) if(!initialState.equals(states.getFirst())) writer.println("INITIAL-STATE " + initialState + ";");

                if(!finalState.isEmpty()) {
                    writer.print("FINAL-STATES");
                    for (String fs : finalState) writer.print(" " + fs);
                    writer.println(";");
                }

                if(!transitions.isEmpty()) {
                    writer.print("TRANSITIONS");
                    for (int i = 0; i < transitions.size(); i++) {
                        writer.print(" " + transitions.get(i));
                        if (i < transitions.size() - 1) writer.print(",");
                    }
                    writer.println(";");
                }
                writer.flush();
            } catch (IOException e) {
                System.out.println("Warning: Cannot create or write to file " + outFile);
            }
        } else throw new InvalidInputException(", too many arguments! Expected 1 or 0");
    }

    private void compile(String[] parameters) throws InvalidInputException {
        if(parameters.length==0) throw new InvalidInputException(", too few arguments.");
        if(parameters.length>1) throw new InvalidInputException(", too many arguments.");
        String fileName = parameters[0];
        if(!fileName.endsWith(".ser")) fileName += ".ser";

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            if(!Files.exists(Paths.get(fileName))) Files.createFile(Paths.get(fileName));

            out.writeObject(this);
            out.flush();
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
        if (fileName.endsWith(".txt")) {

            try (Scanner reader = new Scanner(Paths.get(fileName))) {
                while (reader.hasNextLine()) {
                    String commandLine = "";
                    while(reader.hasNextLine()) {
                        String command = reader.nextLine();
                        if(logging) log("PLZLOG/" + command);

                        if (command.contains(";")) {
                            int semicolonIndex = command.indexOf(';');
                            commandLine += " " + command.substring(0, semicolonIndex).trim();
                            processFileCommand(commandLine.trim());
                            commandLine = "";
                        } else commandLine += " " + command.trim();
                    }
                }
            } catch (IOException e) {
                System.out.println("Something went wrong with file I/O!");
            } catch (SecurityException e) {
                System.out.println("Security Exception!");
            }

        } else {
            if(!fileName.endsWith(".ser")) fileName += ".ser";
            FSM readFSM = null;

            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
                readFSM = (FSM) in.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("Warning: File not found!");
            } catch (InvalidClassException e) {
                System.out.println("Warning: Version not compatible." );
            } catch (StreamCorruptedException e) {
                System.out.println("Warning: File corrupted.");
            } catch (OptionalDataException e) {
                System.out.println("Warning: Unexpected data found.");
            } catch (ClassNotFoundException e) {
                System.out.println("Warning: Class not found.");
            } catch (IOException e) {
                System.out.println("Warning: Something went wrong!");
            }

            if (readFSM == null) throw new InvalidInputException(fileName);
            this.setSymbols(readFSM.getSymbols());
            this.setInitialState(readFSM.getInitialState());
            this.setStates(readFSM.getStates());
            this.setFinalState(readFSM.getFinalState());
            this.setTransitions(readFSM.getTransitions());
            this.setLogging(readFSM.getLogging());
            System.out.println("Object loading successful!");
        }
    }

    private void clear() {
        symbols = new ArrayList<>();
        states = new ArrayList<>();
        initialState = "";
        finalState = new ArrayList<>();
        transitions = new ArrayList<>();
    }

    private void processFileCommand(String command) {
        if (command == null || command.trim().isEmpty()) return;
        String[] commandParts = command.trim().split(";");
        processCommand(commandParts[0].trim());
    }

    public void execute(String input) throws HasNotBeenDeclaredBefore {
        if (initialState.isEmpty()) throw new HasNotBeenDeclaredBefore("Initial State has not been declared before.");

        String currentState = initialState;

        for (int i = 0; i < input.length(); i++) {
            String symbol = String.valueOf(input.charAt(i));
            boolean foundTransition = false;

            for (String transition : transitions) {
                String[] parts = transition.split(" ");

                String from = parts[0];
                String via = parts[1];
                String to = parts[2];

                if (via.equals(currentState) && from.equals(symbol)) {
                    currentState = to;
                    foundTransition = true;
                    break;
                }
            }

            if (!foundTransition) {
                System.out.println("NO: No transition found for symbol '" + symbol + "' from state '" + currentState + "'");
                return;
            }
        }

        if (finalState.contains(currentState)) {
            System.out.println("YES");
        } else {
            System.out.println("NO: Ended at non-final state '" + currentState + "'");
        }
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
    public String getInitialState() {
        return initialState;
    }
    public void setInitialState(String initialState) {
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
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
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